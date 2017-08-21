#include <stdio.h>
#include <cs50.h>

int main(void)
{
    int h = 0, i = 0, j = 0;
    
    do
    {
        printf("Height: ");
        h = GetInt();
    }
    while ((h > 23) || (h < 0));
    
    for(i = 0; i < h;++i)
    {
        for(j = 0; j < (h - i - 1); ++j)
            printf(" ");
        for(; j < (h + 1); ++j)
            printf("#");
        printf("\n");
    }
    
    return 0;
}
