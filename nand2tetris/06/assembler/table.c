#include <stdio.h>
#include <string.h>
#include <stdlib.h>

// structure for variables and labels
typedef struct symbolTableStruct
{
    char **symbol;
    int *value;
}symbolTableStruct;

// structure for 'comp' symbols
typedef struct compTableStruct
{
    char **symbol;
    char **value;
}compTableStruct;

// structure for 'dest' and 'jmp' symbols
typedef struct djTableStruct
{
    char **dest;
    char **jmp;
    char **value;
}djTableStruct;

// global symbol table
symbolTableStruct symbolTable;
int symbolTableSize = 23;

// global 'comp' table
compTableStruct compTable;

// global 'dest' and 'jmp' table
djTableStruct djTable;

/** 
 * initialize the symbol table with the pre-defined symbols, with default
 * size as symbolTableSize and symbols as defined in the file "tableFileName"
 */
void initSymbolTable(char tableFileName[])
{
    char line[MAX_SYMBOL_LENGTH + MAX_SVALUE_LENGTH + TAB_LENGTH];
    int currIndex = 0;

    // allocate space for symbols and values
    symbolTable.symbol = (char **)malloc(symbolTableSize * sizeof(char *));
    symbolTable.value = (int *)malloc(symbolTableSize * sizeof(int));

    // open the table containing pre-defined symbols
    FILE *tableFile = fopen(tableFileName, "r");

    // load the pre-defined symbols file
    while (fgets(line, MAX_SYMBOL_LENGTH + MAX_SVALUE_LENGTH + TAB_LENGTH, tableFile) != NULL)
    {
        char symbol[MAX_SYMBOL_LENGTH], charValue[MAX_SVALUE_LENGTH];
        int i = 0, value = 0;

        // parse the symbol
        for (i = 0; line[i] != '\t'; ++i)
        {
            symbol[i] = line[i];
        }
        symbol[i] = '\0';

        // parse the value
        for (int j = i; line[i] != '\n'; ++i)
        {
            charValue[i - j] = line[i];
        }
        value = atoi(charValue);

        // put the values in the table
        symbolTable.symbol[currIndex] = (char *)malloc(strlen(symbol) * sizeof(char));
        strcpy(symbolTable.symbol[currIndex], symbol);
        symbolTable.value[currIndex] = value;
        
        // update current index
        ++currIndex;
    }
    
    // close the file
    fclose(tableFile);

    return;
}

/**
 * check if the given symbol exists in the symbol table.
 * Returns index of symbol if it exists, else -1.
 */
int symbolIndex(char symbol[])
{
    for (int i = 0; i < symbolTableSize; ++i)
    {
        if (strcmp(symbolTable.symbol[i], symbol) == 0)
        {
            return i;
        }
    }
    
    return -1;
}

/**
 * add a new symbol to the symbol table
 */
void addSymbol(char symbol[], int value)
{
    // create a temporary table
    symbolTableStruct tempTable;
    
    // put the old values in the new table
    tempTable = symbolTable;
    
    // increase the table size
    ++symbolTableSize;
    
    // allocate space for symbols and values with new size
    symbolTable.symbol = (char **)malloc(symbolTableSize * sizeof(char *));
    symbolTable.value = (int *)malloc(symbolTableSize * sizeof(int));
    
    // restore the previous values
    for (int i = 0; i < (symbolTableSize - 1); ++i)
    {
        symbolTable.symbol[i] = (char *)malloc(strlen(tempTable.symbol[i]) * sizeof(char));
        strcpy(symbolTable.symbol[i], tempTable.symbol[i]);
        symbolTable.value[i] = tempTable.value[i];
    }
    
    // add the new value
    symbolTable.symbol[symbolTableSize - 1] = (char *)malloc(strlen(symbol) * sizeof(char));
    strcpy(symbolTable.symbol[symbolTableSize - 1], symbol);
    symbolTable.value[symbolTableSize - 1] = value;
    
    // free the temporary table
    for (int i = 0; i < (symbolTableSize - 1); ++i)
    {
        free(tempTable.symbol[i]);
    }
    free(tempTable.symbol);
    free(tempTable.value);
    
    return;
}

/**
 * clear the symbol table from the memory
 */
void deleteSymbolTable()
{
    for (int i = 0; i < symbolTableSize; ++i)
    {
        free(symbolTable.symbol[i]);
    }
    free(symbolTable.symbol);
    free(symbolTable.value);
}

/**
 * initializes the 'comp' symbol table with the pre-defined symbols, loaded
 * from the file 'tableFileName'
 */
void initCompTable(char tableFileName[])
{
    char line[MAX_COMP_LENGTH + MAX_CVALUE_LENGTH + TAB_LENGTH];
    int currIndex = 0;

    // allocate space for 'comp' symbols and values
    compTable.symbol = (char **)malloc(MAX_COMP_COUNT * sizeof(char *));
    compTable.value = (char **)malloc(MAX_COMP_COUNT * sizeof(char *));

    // open the table containing pre-defined 'comp' symbols
    FILE *tableFile = fopen(tableFileName, "r");

    // load the pre-defined 'comp' symbols file
    while (fgets(line, MAX_COMP_LENGTH + MAX_CVALUE_LENGTH + TAB_LENGTH, tableFile) != NULL)
    {
        char symbol[MAX_COMP_LENGTH], value[MAX_CVALUE_LENGTH];
        int i = 0, j = 0;

        // parse the symbol
        for (i = 0, j = 0; line[i] != '\t'; ++i, ++j)
        {
            symbol[j] = line[i];
        }
        symbol[j] = '\0';

        // parse the value  
        for (j = 0, ++i; line[i] != '\n'; ++i, ++j)
        {
            value[j] = line[i];
        }
        value[j] = '\0';

        // put the values in the table
        compTable.symbol[currIndex] = (char *)malloc(strlen(symbol) * sizeof(char));
        compTable.value[currIndex] = (char *)malloc(strlen(value) * sizeof(char));
        strcpy(compTable.symbol[currIndex], symbol);
        strcpy(compTable.value[currIndex], value);
        
        // update current index
        ++currIndex;
    }
    
    // close the file
    fclose(tableFile);

    return;
}

/**
 * check if the given 'comp' symbol exists in the 'comp' symbol table.
 * Returns index of symbol if it exists, else -1.
 */
int compIndex(char symbol[])
{
    for (int i = 0; i < MAX_COMP_COUNT; ++i)
    {
        if (strcmp(compTable.symbol[i], symbol) == 0)
        {
            return i;
        }
    }
    
    return -1;
}

/**
 * clear the 'comp' symbol table from the memory
 */
void deleteCompTable()
{
    for (int i = 0; i < MAX_COMP_COUNT; ++i)
    {
        free(compTable.symbol[i]);
        free(compTable.value[i]);
    }
    free(compTable.symbol);
    free(compTable.value);
}

/**
 * initializes the 'dest' and 'jmp' symbols table with the pre-defined
 * symbols, loaded from the file 'tableFileName'
 */
void initDJTable(char tableFileName[])
{
    char line[(2 * (MAX_DJ_LENGTH + TAB_LENGTH)) + MAX_DJVALUE_LENGTH];
    int currIndex = 0;

    // allocate space for 'dest' and 'jmp' symbols and values
    djTable.dest = (char **)malloc(MAX_DJ_COUNT * sizeof(char *));
    djTable.jmp = (char **)malloc(MAX_DJ_COUNT * sizeof(char *));
    djTable.value = (char **)malloc(MAX_DJ_COUNT * sizeof(char *));

    // open the table containing pre-defined 'dest' and 'jmp' symbols
    FILE *tableFile = fopen(tableFileName, "r");

    // load the pre-defined 'dest' and 'jmp' symbols file
    while (fgets(line, (2 * (MAX_DJ_LENGTH + TAB_LENGTH)) + MAX_DJVALUE_LENGTH, tableFile) != NULL)
    {
        char dest[MAX_DJ_LENGTH], jmp[MAX_DJ_LENGTH], value[MAX_DJVALUE_LENGTH];
        int i = 0, j = 0;

        // parse the 'dest' symbol
        for (i = 0, j = 0; line[i] != '\t'; ++i, ++j)
        {
            dest[j] = line[i];
        }
        dest[j] = '\0';
        
        // parse the 'jmp' symbol
        for (j = 0, ++i; line[i] != '\t'; ++i, ++j)
        {
            jmp[j] = line[i];
        }
        jmp[j] = '\0';

        // parse the value
        for (j = 0, ++i; line[i] != '\n'; ++i, ++j)
        {
            value[j] = line[i];
        }
        value[j] = '\0';

        // put the values in the table
        djTable.dest[currIndex] = (char *)malloc(strlen(dest) * sizeof(char));
        djTable.jmp[currIndex] = (char *)malloc(strlen(jmp) * sizeof(char));
        djTable.value[currIndex] = (char *)malloc(strlen(value) * sizeof(char));
        strcpy(djTable.dest[currIndex], dest);
        strcpy(djTable.jmp[currIndex], jmp);
        strcpy(djTable.value[currIndex], value);
        
        // update current index
        ++currIndex;
    }
    
    // close the file
    fclose(tableFile);

    return;
}

/**
 * check if the given 'dest' symbol exists in the 'dest' and 'jmp' symbols
 * table. Returns index of symbol if it exists, else -1.
 */
int destIndex(char dest[])
{
    for (int i = 0; i < MAX_DJ_COUNT; ++i)
    {
        if (strcmp(djTable.dest[i], dest) == 0)
        {
            return i;
        }
    }
    
    return -1;
}

/**
 * check if the given 'jmp' symbol exists in the 'dest' and 'jmp' symbols
 * table. Returns index of symbol if it exists, else -1.
 */
int jmpIndex(char jmp[])
{
    for (int i = 0; i < MAX_DJ_COUNT; ++i)
    {
        if (strcmp(djTable.jmp[i], jmp) == 0)
        {
            return i;
        }
    }
    
    return -1;
}

/**
 * clear the 'dest' and 'jmp' symbols table from the memory
 */
void deleteDJTable()
{
    for (int i = 0; i < MAX_DJ_COUNT; ++i)
    {
        free(djTable.dest[i]);
        free(djTable.jmp[i]);
        free(djTable.value[i]);
    }
    free(djTable.dest);
    free(djTable.jmp);
    free(djTable.value);
}

/**
 * functions below are for testing purposes
 */
/*
void displaySymbolTable()
{
    for (int i = 0; i < symbolTableSize; ++i)
    {
        printf("%s\t%d\n", symbolTable.symbol[i], symbolTable.value[i]);
    }
}

void displayCompTable(void)
{
    for (int i = 0; i < MAX_COMP_COUNT; ++i)
    {
        printf("%s\t%s\n", compTable.symbol[i], compTable.value[i]);
    }
}

void displayDJTable(void)
{
    for (int i = 0; i < MAX_DJ_COUNT; ++i)
    {
        printf("%s\t%s\t%s\n", djTable.dest[i], djTable.jmp[i], djTable.value[i]);
    }
}

int main(void)
{
    initSymbolTable("s_table.dat");
    displaySymbolTable();
    
    printf("\n");
    
    addSymbol("SUD", 96);
    displaySymbolTable();
    
    printf("\n");
    
    printf("%d\n", symbolIndex("SUD"));
    
    deleteSymbolTable();
    
    initCompTable("c_table.dat");
    initDJTable("dj_table.dat");
    
    displayCompTable();
    printf("\n");
    displayDJTable();
    printf("\n");
    
    deleteCompTable();
    deleteDJTable();

    return 0;
}
*/
