#include <stdio.h>
#include <stdlib.h>
#include "bmp.h"

int main(int argc, char* argv[])
{
    if (argc != 4)
    {
        printf("Format ./resize resize_factor input_file.bmp output_file.bmp\n\n");
        return 1;
    }
    
    int fac = atoi(argv[1]);
    char* infile = argv[2];
    char* outfile = argv[3];
    
    FILE* inptr = fopen(infile, "r");
    FILE* outptr = fopen(outfile, "w");
    
    if ((fac < 1) || (fac > 100))
    {
        printf("\nThe value of factor must be between 1 and 100.\nExiting. . .\n\n");
        return 2;
    }
    if (inptr == NULL)
    {
        printf("\nUnable to open %s.\nExiting. . .\n\n", infile);
        return 3;
    }
    if (outptr == NULL)
    {
        fclose(outptr);
        printf("\nUnable to create %s.\nExiting. . .\n\n", outfile);
        return 4;
    }
    
    BITMAPFILEHEADER bf;
    BITMAPINFOHEADER bi;
    fread(&bf, sizeof(BITMAPFILEHEADER), 1, inptr);
    fread(&bi, sizeof(BITMAPINFOHEADER), 1, inptr);
    
    if (bf.bfType != 0x4d42 || bf.bfOffBits != 54 || bi.biSize != 40 || bi.biBitCount != 24 || bi.biCompression != 0)
    {
        fclose(outptr);
        fclose(inptr);
        fprintf(stderr,"Unsupported file format.\nExiting. . .\n\n");
        return 5;
    }
    
    int in_file_padding = (4 - (bi.biWidth * sizeof(RGBTRIPLE)) % 4) % 4;
    
    bi.biWidth *= fac;
    bi.biHeight *= fac;
    
    int padding =  (4 - (bi.biWidth * sizeof(RGBTRIPLE)) % 4) % 4;
     
    bi.biSizeImage = (bi.biWidth * abs(bi.biHeight) * sizeof(RGBTRIPLE)) + (padding * abs(bi.biHeight));
    bf.bfSize = sizeof(BITMAPFILEHEADER) + sizeof(BITMAPINFOHEADER) + bi.biSizeImage;
    fwrite(&bf, sizeof(BITMAPFILEHEADER), 1, outptr);
    fwrite(&bi, sizeof(BITMAPINFOHEADER), 1, outptr);
    
    bi.biWidth /= fac;
    for(int i = 0, biHeight = abs(bi.biHeight); i < biHeight; ++i)
    {
        for(int j = 0; j < bi.biWidth; ++j)
        {
            RGBTRIPLE triple;
            
            fread(&triple, sizeof(RGBTRIPLE), 1, inptr);
            
            for(int k = 0; k < fac; ++k)
            {
                fwrite(&triple, sizeof(RGBTRIPLE), 1, outptr);
            }
        }
        
        for(int j = 0; j < padding; ++j)
        {
            fputc(0x00, outptr);
        }
        
        if ((i + 1) % fac != 0)
        {
            fseek(inptr, -(bi.biWidth * sizeof(RGBTRIPLE)), SEEK_CUR);
        }
        else
        {
            fseek(inptr, in_file_padding, SEEK_CUR);
        }
    }
    
    fclose(inptr);
    fclose(outptr);
    
    return 0;
}
