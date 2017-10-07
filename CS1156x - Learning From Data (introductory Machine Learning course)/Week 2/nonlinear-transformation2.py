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

def transform_data (X):
    list_X = X.tolist()
    transformed_X = []

    # transform each vector
    for x in list_X:
        transformed_X += [[x[0], x[1], x[0]*x[1], x[0]*x[0], x[1]*x[1], 1]]

    return matrix(transformed_X)

number_of_trials = input('Number of trials: ')
train_data_size = input('Size of training data: ')
test_data_size = input('Size of testing data: ')
e_out = 0

# generate training data
train_data = generate_train_data(train_data_size)

# construct matrices X and Y
X = matrix(train_data[0])
Y = matrix(train_data[1])

# transform X into (x, y, xy, x^2, y^2, 1)
X = transform_data(X)

# compute weights for g (using (x, y, xy, x^2, y^2, 1))
pseudo_inv_X = (X.T * X).I * X.T
w_g = (pseudo_inv_X * Y.T).tolist()
w_g = [w_g[0][0], w_g[1][0], w_g[2][0], w_g[3][0], w_g[4][0], w_g[5][0]]

# trials for estimation of e_out
for trial in range(number_of_trials):
    # generate testing data
    test_data = generate_train_data(test_data_size)
    test_data[0] = transform_data(matrix(test_data[0])).tolist()
    
    # estimate e_out
    number_of_errors = 0
    for i in range(test_data_size):
        if sign((test_data[0][i][0] * w_g[0]) +
                (test_data[0][i][1] * w_g[1]) +
                (test_data[0][i][2] * w_g[2]) +
                (test_data[0][i][3] * w_g[3]) +
                (test_data[0][i][4] * w_g[4]) +
                (test_data[0][i][5] * w_g[5])) != test_data[1][i]:
            number_of_errors += 1
    e_out += number_of_errors / (test_data_size + 0.0)
e_out /= (number_of_trials + 0.0)

print 'Weight vector (x1, x2, x1*x2, x1^2, x2^2, 1) =', w_g
print 'Average value of e_out =', e_out
