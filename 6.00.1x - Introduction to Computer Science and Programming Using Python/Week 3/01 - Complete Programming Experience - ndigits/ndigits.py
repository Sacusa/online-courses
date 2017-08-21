# ndigits recursion

# Find the number of digits in a number using a recursive function

def ndigits(num):
    '''
    Assumes num is an integer

    Returns: the number of digits in num
    '''
    # Make the number absolute
    num = abs(num)
    
    # Base case: return 0 if the number is zero
    if num == 0:
        return 0

    # Recursive case: return the number of digits in (num/10) + 1
    return 1 + ndigits(num/10)

# Test cases
print(str(ndigits(1)))
print(str(ndigits(12)))
print(str(ndigits(123)))
print(str(ndigits(1234)))
print(str(ndigits(12345)))
