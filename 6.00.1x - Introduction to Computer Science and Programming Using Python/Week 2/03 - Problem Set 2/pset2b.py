# Problem Set 2, problem 2

# Use new_balance to find the lowest monthly payment
monthlyPayment = 0
new_balance = balance

# Increment minimum monthly payment, until new_balance becomes 0 or less
while new_balance > 0:
    new_balance = balance
    monthlyPayment += 10
    
    for month in range(1, 13):    
        new_balance = new_balance - monthlyPayment
        new_balance = new_balance + (annualInterestRate/12.0) * new_balance

# Print the result
print('Lowest Payment: ' + str(monthlyPayment))
