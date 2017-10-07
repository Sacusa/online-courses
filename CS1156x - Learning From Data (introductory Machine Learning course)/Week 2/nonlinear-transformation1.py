#!/usr/bin/python
import random
import time
from numpy import matrix

def sign (x):
    if (x < 0):
        return -1
    elif (x > 0):
        return 1
    return 0

def generate_train_data (n):
    ''' Generates training data of size n, using the equation
            f = x1^2 + x2^2 - 0.6
    '''
    random.seed(a=None)
    train_data_size = n
    X = []
    Y = []

    # generate n sample cases
    while (train_data_size > 0):
        t_x = random.uniform(-1,1)
        t_y = random.uniform(-1,1)
        t_s = sign((t_x * t_x) + (t_y * t_y) - 0.6)

        # ignore points on the line
        if t_s == 0:
            continue

        X += [[t_x, t_y, 1]]
        Y += [t_s]
        train_data_size -= 1
     
    # flip 10% of the outputs
    for i in range(n):
        if random.random() < 0.1:
            Y[i] = -Y[i]

    return [X, Y]

number_of_trials = input('Number of trials: ')
train_data_size = input('Size of training data: ')
e_in = 0

for i in range(number_of_trials):
    # generate training data
    train_data = generate_train_data(train_data_size)

    # construct matrices X and Y
    X = matrix(train_data[0])
    Y = matrix(train_data[1])

    # compute weights for g (using (x, y, 1))
    pseudo_inv_X = (X.T * X).I * X.T
    w_g = (pseudo_inv_X * Y.T).tolist()
    w_g = [w_g[0][0], w_g[1][0], w_g[2][0]]

    # estimate e_in
    number_of_errors = 0
    for i in range(train_data_size):
        if sign((train_data[0][i][0] * w_g[0]) +
                (train_data[0][i][1] * w_g[1]) +
                (train_data[0][i][2] * w_g[2])) != train_data[1][i]:
            number_of_errors += 1
    e_in += number_of_errors / (train_data_size + 0.0)

e_in /= (number_of_trials + 0.0)

print 'Average value of e_in =', e_in
