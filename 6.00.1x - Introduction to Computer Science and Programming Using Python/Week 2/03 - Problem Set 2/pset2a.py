# Problem Set 2, problem 1

totalPaid = 0

for month in range(1, 13):
    monthPayment = balance * monthlyPaymentRate
    
    balance = balance - monthPayment
    balance = balance + (annualInterestRate/12.0) * balance

    totalPaid += monthPayment

    print('Month: ' + str(month))
    print('Minimum monthly payment: ' + str(round(monthPayment, 2)))
    print('Remaining balance: ' + str(round(balance, 2)))

print('Total paid: ' + str(round(totalPaid, 2)))
print('Remaining balance: ' + str(round(balance, 2)))
