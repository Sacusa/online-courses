/**
 * recover.c
 *
 * Computer Science 50
 * Problem Set 4
 *
 * Recovers JPEGs from a forensic image.
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#define BLOCK_SIZE 512

int main(int argc, char* argv[])
{
    // TODO
    if (argc != 1)
    {
        printf("\nNo command-line arguments expected. Exiting . . . \n");
        return 1;
    }
    
    char file_name[3];
    uint8_t block[BLOCK_SIZE];
    short file_open = 0, file_name_num = 0;
    FILE* outfile;
    FILE* data = fopen("card.raw", "r");
    
    if (data == NULL)
    {
        printf("\ncard.raw file not found. Exiting . . .\n");
        return 2;
    }
    
    while(fread(&block, BLOCK_SIZE, 1, data) != 0)
    {
        if (((block[0] == 0xff) && (block[1] == 0xd8) && (block[2] == 0xff) && (block[3] == 0xe0)) ||
            ((block[0] == 0xff) && (block[1] == 0xd8) && (block[2] == 0xff) && (block[3] == 0xe1)))
        {
            if (file_open == 0)
            {
                sprintf(file_name, "%03d.jpg", file_name_num);
                outfile = fopen(file_name, "w");
                
                if (outfile == NULL)
                {
                    printf("\nUnable to create jpg file. Exiting . . .\n");
                    return 3;
                }
                
                ++file_name_num;
                file_open = 1;
                
                fwrite(&block, BLOCK_SIZE, 1, outfile);
                continue;
            }
            else
            {
                fclose(outfile);
                file_open = 0;
                fseek(data, -BLOCK_SIZE, SEEK_CUR);
                continue;
            }
        }
        
        if (file_open == 1)
        {
            fwrite(&block, BLOCK_SIZE, 1, outfile);
        }
    }
    
    if (file_open == 1)
    {
        fclose(outfile);
    }
    
    return 0;
}
