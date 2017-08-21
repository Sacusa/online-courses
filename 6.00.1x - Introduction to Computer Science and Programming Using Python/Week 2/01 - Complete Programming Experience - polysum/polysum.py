# Week 2, polysum

import math

def polyArea(n, s):
    '''
    Finds the area of the polygon having
    n - sides, each of
    s - length
    both of which can be int or float.
    Returns the area in float.
    '''
    # Finding the numerator and denominator
    num = 0.25 * n * math.pow(s, 2)
    den = math.tan(math.pi/n)

    # Return the result
    return num/den

def polyPerimeter(n, s):
    '''
    Finds the perimeter of the polygon having
    n - sides, each of
    s - length
    both of which can be int or float.
    Returns the perimeter in float.
    '''
    return n * s

def polysum(n, s):
    '''
    Returns the sum of the area and the square of the perimeter of
    the regular polygon, having
    n - sides, each of
    s - length
    both of which can be int or float.
    Returns the perimeter in float, precise upto 4 decimal places
    '''
    num = polyArea(n, s) + polyPerimeter(n, s)**2
    return round(num, 4)
