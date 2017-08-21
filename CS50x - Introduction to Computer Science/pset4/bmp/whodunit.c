#include <stdio.h>
#include <stdlib.h>
#include "bmp.h"

int main(int argc, char* argv[])
{
    if (argc != 3)
    {
        printf("FormatL ./whodunit input_file.bmp output_file.bmp\n\n");
        return 1;
    }
    
    char* infile = argv[1];
    char* outfile = argv[2];
    
    FILE* inptr = fopen(infile, "r");
    FILE* outptr = fopen(outfile, "w");
    
    if (inptr == NULL)
    {
        printf("\nUnable to open %s.\nExiting. . .\n\n", infile);
        return 2;
    }
    if (outptr == NULL)
    {
        fclose(outptr);
        printf("\nUnable to create %s.\nExiting. . .\n\n", outfile);
        return 3;
    }
    
    BITMAPFILEHEADER bf;
    BITMAPINFOHEADER bi;
    fread(&bf, sizeof(BITMAPFILEHEADER), 1, inptr);
    fread(&bi, sizeof(BITMAPINFOHEADER), 1, inptr);
    
    if (bf.bfType != 0x4d42 || bf.bfOffBits != 54 || bi.biSize != 40 || bi.biBitCount != 24 || bi.biCompression != 0)
    {
        fclose(outptr);
        fclose(inptr);
        fprintf(stderr,"Unsupported file format.\n");
        return 4;
    }
    
    fwrite(&bf, sizeof(BITMAPFILEHEADER), 1, outptr);
    fwrite(&bi, sizeof(BITMAPINFOHEADER), 1, outptr);
    
    for(int i = 0, biHeight = abs(bi.biHeight); i < biHeight; ++i)
    {
        for(int j = 0; j < bi.biWidth; ++j)
        {
            RGBTRIPLE triple;
            
            fread(&triple, sizeof(RGBTRIPLE), 1, inptr);
            
            if ((triple.rgbtRed == 0xff) && (triple.rgbtGreen == 0x00) && (triple.rgbtBlue == 0x00))
            { 
                triple.rgbtRed = 0xff;
                triple.rgbtGreen = 0xff;
                triple.rgbtBlue = 0xff;
            }
            
            fwrite(&triple, sizeof(RGBTRIPLE), 1, outptr);
        }
    }
    
    return 0;
}
