#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include <ctype.h>

// the current address for variables
int currAddr = 16;

/**
 * converts the given 'value' to binary, stores it into a string, and returns
 * it
 */
char *convertToBin(int value)
{
    int binLength = 0;
    
    // get the length of the binary value
    while (pow(2, binLength) <= value)
    {
        ++binLength;
    }
    ++binLength;
    
    // allocate space for the binary value
    char *binValue = (char *)malloc(binLength * sizeof(char));
    
    // get the binary value
    for (int i = 0; i < (binLength - 1); ++i)
    {
        binValue[binLength - i - 2] = (value % 2) + 48;
        value /= 2;
    }
    binValue[binLength - 1] = '\0';
    
    return binValue;
}

/**
 * translates the given symbol of an A-instruction into binary
 */
char *transAinstruct(char symbol[])
{
    int i = 0, index = 0, value = 0;
    char *binSymbolValue, *binValue;
    
    // check if the symbol is a number
    if (isdigit(symbol[0]))
    {
        value = atoi(symbol);
    }
    
    // else, parse the symbol
    else
    {
        // check if symbol exists in the table, and get its index if it does
        if ((index = symbolIndex(symbol)) != -1)
        {
            // get the value of the symbol
            value = symbolTable.value[index];
        }
        
        // if it doesn't, add it to the symbol table
        else
        {
            // add the symbol to the symbol table
            addSymbol(symbol, currAddr);
            
            // update the current address
            ++currAddr;
            
            // call the function back recursively
            return (transAinstruct(symbol));
        }        
    }
    
    // get the binary value of 'value' in a string
    binSymbolValue = convertToBin(value);
    
    // allocate space for the binary value of the A-instruction
    binValue = (char *)malloc((INSTRUCT_WIDTH + 2) * sizeof(char));
    
    // add the first bit of the A-instruction
    binValue[0] = '0';
    
    // add buffer '0' bits to the A-instruction
    int buffer = INSTRUCT_WIDTH - strlen(binSymbolValue) - 2;
    for (i = 1; buffer >= 0; ++i, --buffer)
    {
        binValue[i] = '0';
    }
    
    // add the binary value of the symbol to the A-instruction
    for (int j = 0; binSymbolValue[j] != '\0'; ++i, ++j)
    {
        binValue[i] = binSymbolValue[j];
    }
    //binValue[i] = '\n';
    binValue[i] = '\0';
    
    // free the space of binary value of the symbol
    free(binSymbolValue);
    
    return binValue;
}

/**
 * translates the given C-instruction into binary
 */
char *transCinstruct(char dest[], char comp[], char jmp[])
{
    int i = 0, index = 0;
    
    // allocate space for the binary instruction, and initialize it
    char *binValue = (char *)malloc((INSTRUCT_WIDTH + 2) * sizeof(char));
    for (i = 0; i < 3; ++i)
    {
        binValue[i] = '1';
    }
    
    // get the binary value of 'comp' symbol, if it exists
    if ((index = compIndex(comp)) != -1)
    {
        for (int j = 0; j < (MAX_CVALUE_LENGTH - 1); ++i, ++j)
        {
            binValue[i] = compTable.value[index][j];
        }
    }
    
    // get the binary value of 'dest' symbol, if it exists
    if ((index = destIndex(dest)) != -1)
    {
        for (int j = 0; j < (MAX_DJVALUE_LENGTH - 1); ++i, ++j)
        {
            binValue[i] = djTable.value[index][j];
        }
    }
    
    // get the binary value of 'jmp' symbol, if it exists
    if ((index = jmpIndex(jmp)) != -1)
    {
        for (int j = 0; j < (MAX_DJVALUE_LENGTH - 1); ++i, ++j)
        {
            binValue[i] = djTable.value[index][j];
        }
    }
    
    // append '\n'
    //binValue[i] = '\n';
    binValue[i] = '\0';
    
    return binValue;
}

/**
 * functions below are for testing purposes
 */
/*
int main(void)
{
    initSymbolTable("s_table.dat");
    initCompTable("c_table.dat");
    initDJTable("dj_table.dat");
        
    deleteSymbolTable();
    deleteCompTable();
    deleteDJTable();
}
*/
