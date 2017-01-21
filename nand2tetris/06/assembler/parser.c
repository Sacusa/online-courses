#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

/**
 * returns the type of instruction the given line corresponds to.
 * 0 => A-instruction
 * 1 => C-instruction
 * 2 => Label
 * 3 => Blank line
 */
int instructType(char line[])
{
    int i = 0;
    
    // skip over the whitespaces
    if (line[i] == ' ')
    {
        for (; line[i] == ' '; ++i);
    }
    
    // check if it is an A-instruction
    if (line[i] == '@')
    {
        return 0;
    }
    
    // check if it is a C-instruction
    else if (((line[i] >= 'A') && (line[i] <= 'Z')) || ((line[i] >= '0') && (line[i] <= '9')))
    {
        return 1;
    }
    
    // check if it is a label
    else if (line[i] == '(')
    {
        return 2;
    }    
    
    // it is a blank line or a comment
    else
    {
        return 3;
    }
}

/**
 * parse A-instruction in the given line and returns the symbol
 */
char *parseAinstruct(char line[])
{
    int i = 0, symbolLength = 0;
    
    // skip over whitespaces
    for (i = 0; line[i] != '@'; ++i);
    
    // get the length of symbol
    ++i;
    for (symbolLength = i; isalnum(line[symbolLength]) || ispunct(line[symbolLength]); ++symbolLength)
    {
        // check for comment
        if ((line[symbolLength] == '/') && (line[symbolLength + 1] == '/'))
        {
            break;
        }
    }
    symbolLength -= i;
    
    // allocate space for symbol
    char *symbol = (char *)malloc((symbolLength + 1) * sizeof(char));
    
    // parse the symbol
    for (int j = 0; j < symbolLength; ++i, ++j)
    {
        symbol[j] = line[i];
    }
    symbol[symbolLength] = '\0';
    
    return symbol;
}

/**
 * check if a 'dest' symbol is present in a given C-instruction.
 * 0 => Does not exist
 * 1 => Exists
 */
int destExists(char line[])
{
    for (int i = 0; line[i] != '\n'; ++i)
    {
        if (line[i] == '=')
        {
            return 1;
        }
    }
    
    return 0;
}

/**
 * check if a 'jmp' symbol is present in a given C-instruction.
 * 0 => Does not exist
 * 1 => Exists
 */
int jmpExists(char line[])
{
    for (int i = 0; line[i] != '\n'; ++i)
    {
        if (line[i] == ';')
        {
            return 1;
        }
    }
    
    return 0;
}

/**
 * parse C-instruction given in the line, and break into 'dest', 'comp' and
 * 'jmp', who all must be atleast 5 bytes.
 */
void parseCinstruct(char line[], char dest[], char comp[], char jmp[])
{
    int i = 0, j = 0;
    
    // skip over the whitespaces
    if (line[i] == ' ')
    {
        for (; line[i] == ' '; ++i);
    }
            
    // parse the 'dest' symbol, if it is present
    if (destExists(line))
    {        
        // parse the 'dest' symbol
        for (j = 0; (line[i] != ' ') && (line[i] != '=') && (j < (MAX_DJ_LENGTH - 1)); ++i, ++j)
        {
            dest[j] = line[i];
        }        
        dest[j] = '\0';
        
        // reach the start of 'comp' symbol
        if (line[i] == '=')
        {
            ++i;
            if (line[i] == ' ')
            {
                for (; line[i] == ' '; ++i);
            }
        }
        else
        {
            for (; line[i] != '='; ++i);
            ++i;
            if (line[i] == ' ')
            {
                for (; line[i] == ' '; ++i);
            }            
        }
    }
    
    // else store 'null' in the 'dest' variable
    else
    {
        strcpy(dest, "null\0");
    }
    
    // parse the 'comp' symbol
    for (j = 0; (line[i] != ';') && (line[i + 1] != '\n') && (j < (MAX_COMP_LENGTH - 1)); ++i, ++j)
    {
        // handle whitespaces in between
        if (line[i] == ' ')
        {
            --j;
            continue;
        }
        
        // check for comment
        if ((line[i] == '/') && (line[i + 1] == '/'))
        {
            break;
        }
        comp[j] = line[i];
    }
    comp[j] = '\0';
    
    // parse the 'jmp' symbol, if it is present
    if (jmpExists(line) && (line[i] != '\n'))
    {
        // skip over any whitespaces
        ++i;
        if (line[i] == ' ')
        {
            for (; (line[i] == ' ') && (line[i] != '\n'); ++i);
        }
        
        // parse the 'jmp' symbol
        for (j = 0; (line[i] != ' ') && (line[i + 1] != '\n') && (j < (MAX_DJ_LENGTH - 1)); ++i, ++j)
        {
            // check for comment
            if ((line[i] == '/') && (line[i + 1] == '/'))
            {
                break;
            }
            
            jmp[j] = line[i];
        }
        jmp[j] = '\0';
    }
    
    // else store 'null' in the 'jmp' variable
    else
    {
        strcpy(jmp, "null\0");
    }
    
    return;
}

/**
 * parses an instruction containing a label, and returns it.
 */
char *parseLabel(char line[])
{
    int i = 0, labelLength = 0;
    
    // skip over any whitespaces, and reach the start of the label
    if (line[i] != '(')
    {
        for (; line[i] != '('; ++i);
    }
    ++i;
    if (line[i] == ' ')
    {
        for (; line[i] == ' '; ++i);
    }
    
    // get the label length
    for (labelLength = i; (line[labelLength] != ' ') && (line[labelLength] != ')'); ++labelLength);
    labelLength -= i;
    
    // allocate space for the label
    char *label = (char *)malloc((labelLength + 1) * sizeof(char));
    
    // parse the label
    for (int j = 0; (line[i] != ' ') && (line[i] != ')'); ++i, ++j)
    {
        label[j] = line[i];
    }
    label[labelLength] = '\0';
    
    return label;
}

/**
 * functions below are for testing purposes
 */
/*
int main(void)
{
    // test for A-instruction
    printf("%d\n", instructType("  @sud\n"));
    char *str = parseAinstruct("  @sud\n");
    printf("%s\n%d\t%d\n", str, strlen("sud"), strlen(str));
    free(str);
    
    // test for C-instruction
    printf("\n%d\n", instructType("  AMD  =  D  +  1   ;  JGT  \n"));
    char dest[5], comp[5], jmp[5];
    parseCinstruct("  AMD  =  D  +  1   ;  JGT  \n", dest, comp, jmp);
    printf("%s\n%d\t%d\n", dest, strlen("AMD"), strlen(dest));
    printf("%s\n%d\t%d\n", comp, strlen("D+1"), strlen(comp));
    printf("%s\n%d\t%d\n", jmp, strlen("JGT"), strlen(jmp));
    
    // test for labels
    printf("\n%d\n", instructType("  (  label  )  \n"));
    char *label = parseLabel("  (  label  )  \n");
    printf("%s\n", label);
    free(label);
    
    // test for blank line
    printf("\n%d\n", instructType("      \n"));
    
    return 0;
}
*/
