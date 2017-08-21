# Problem Set 2, problem 3

# For power function
import math

# Use new_balance to find the lowest monthly payment
monthlyInterestRate = annualInterestRate/12.0
new_balance = balance

# Define variables for bisection search, and set initial value for monthly payment
high = (balance * pow(1 + monthlyInterestRate, 12)) / 12.0
low = balance / 12.0
monthlyPayment = (high + low) / 2.0

# Find monthly payment, until new_balance comes between 0 and 0.01
while new_balance < 0 or new_balance > 0.01:
    new_balance = balance
    monthlyPayment = (high + low) / 2.0

    # Use monthlyPayment to find new value of new_balance
    for month in range(1, 13):    
        new_balance = new_balance - monthlyPayment
        new_balance = new_balance + (annualInterestRate/12.0) * new_balance

    # Update high and low for bisection search
    if new_balance < 0:
        high = monthlyPayment
    elif new_balance > 0:
        low = monthlyPayment

# Print the result
print('Lowest Payment: ' + str(round(monthlyPayment, 2)))
