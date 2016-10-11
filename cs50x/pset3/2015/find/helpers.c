/**
 * helpers.c
 *
 * Computer Science 50
 * Problem Set 3
 *
 * Helper functions for Problem Set 3.
 */

#include <stdio.h>

#include "helpers.h"

/**
 * Returns true if value is in array of n values, else false.
 */
bool search(int value, int values[], int n)
{
    // TODO: implement a searching algorithm
    int min = 0, max = n, mid = 0;
    
    while((max - mid) != 1)
    {
        mid = (max + min) / 2;
        
        if (value == values[mid])
        {
            return true;
        }
        else if (value > values[mid])
        {
            min = mid;
        }
        else if (value < values[mid])
        {
            max = mid;
        }
    }
    if ((mid == min) && (value == values[max]))
    {
        return true;
    }
    
    return false;
}

/**
 * Sorts array of n values.
 */
void sort(int values[], int n)
{
    // TODO: implement an O(n^2) sorting algorithm
    int min = 0, i = 0, j = 0;
    
    for(i = 0; i < (n - 1); ++i)
    {
        min = i;
        for (j = i + 1; j < n; ++j)
        {
            if (values[j] < values[min])
            {
                min = j;
            }
        }
        j = values[i];
        values[i] = values[min];
        values[min] = j;
    }
    
    return;
}
