#!/usr/bin/python
import random
random.seed(a=None)

# constants defining the experiment
NUMBER_OF_TRIALS = 100000
NUMBER_OF_COINS = 1000
NUMBER_OF_FLIPS = 10

# variables to record experiment values
frequency_of_heads = range(NUMBER_OF_COINS)
v1 = 0
vrand = 0
vmin = 0

for trial in range(NUMBER_OF_TRIALS):
    for coin in range(NUMBER_OF_COINS):
        frequency_of_heads[coin] = 0
        for flip in range(NUMBER_OF_FLIPS):
            # random number >= 0.5  ==>  heads
            if random.random() >= 0.5:
                frequency_of_heads[coin] += 1
    
    # record values of v1, vrand, vmin
    v1 += frequency_of_heads[0]
    vrand += frequency_of_heads[random.randint(0, len(frequency_of_heads) - 1)]
    vmin += sorted(frequency_of_heads)[0]

print 'Average value of v1 =',    v1 / (NUMBER_OF_TRIALS + 0.0)
print 'Average value of vrand =', vrand / (NUMBER_OF_TRIALS + 0.0)
print 'Average value of vmin =',  vmin / (NUMBER_OF_TRIALS + 0.0)
