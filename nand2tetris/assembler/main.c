#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "constants.c"
#include "table.c"
#include "code.c"
#include "parser.c"

int main(int argc, char *argv[])
{
    // check the arguments
    if (argc != 3)
    {
        printf("\nUsage: ./assembler input_file.asm output_file.hack\n\n");
        return 1;
    }
    
    // open the input and output files
    FILE *ifile = fopen(argv[1], "r");
    FILE *ofile = fopen(argv[2], "w");
    
    // check if files open
    if(!ifile)
    {
        printf("Invalid input file. Exiting . . .\n");
        return 2;
    }
    if(!ofile)
    {
        printf("Invalid output file. Exiting . . .\n");
        return 2;
    }
    
    // initialize the tables
    initSymbolTable(SYMBOL_TABLE_NAME);
    initCompTable(COMP_TABLE_NAME);
    initDJTable(DJ_TABLE_NAME);
    
    // arrays for instruction parts
    char comp[MAX_COMP_LENGTH], dest[MAX_DJ_LENGTH], jmp[MAX_DJ_LENGTH], line[MAX_LINE_LENGTH];
    
    char *symbol, *binValue;
    int currLine = 0;
    
    // first pass; read all labels and add to symbol table
    while (fgets(line, MAX_LINE_LENGTH, ifile) != NULL)
    {
        // get the instruction type
        int type = instructType(line);
        
        // A-instruction
        if (type == 0)
        {
            // increment the current line number
            ++currLine;
            
            continue;
        }
        
        // C-instruction
        else if (type == 1)
        {
            // increment the current line number
            ++currLine;
            
            continue;
        }
        
        // Label
        else if (type == 2)
        {
            // parse instruction and extract the label
            symbol = parseLabel(line);
            
            // add the label to the symbol table
            addSymbol(symbol, currLine);
            
            // clear the memory
            free(symbol);
        }
    }
    
    // reset to the beginning of file
    fseek(ifile, 0, SEEK_SET);
    
    // second pass; translating and writing to output file
    while (fgets(line, MAX_LINE_LENGTH, ifile) != NULL)
    {
        // get the instruction type
        int type = instructType(line);
        
        // A-instruction
        if (type == 0)
        {
            // parse instruction and extract symbol
            symbol = parseAinstruct(line);
            
            // translate instruction to binary
            binValue = transAinstruct(symbol);
            
            // write instruction to file
            fprintf(ofile, "%s\n", binValue);
            
            // clear the memory
            free(symbol);
            free(binValue);
        }
        
        // C-instruction
        else if (type == 1)
        {
            // parse instruction and extract 'dest', 'comp' and 'jmp' symbols
            parseCinstruct(line, dest, comp, jmp);
            
            // translate instruction to binary
            binValue = transCinstruct(dest, comp, jmp);
                        
            // write instruction to file
            fprintf(ofile, "%s\n", binValue);
            
            // clear the memory
            free(binValue);
        }
        
        // Label
        else if (type == 2)
        {
            continue;
        }
        
        // blank line or comment
        else if (type == 3)
        {
            continue;
        }
        
        // unhandled exception
        else
        {
            return 2;
        }
    }
    
    // close the files
    fclose(ifile);
    fclose(ofile);
    
    // destroy the tables
    deleteSymbolTable();
    deleteCompTable();
    deleteDJTable();
    
    return 0;
}
