#!/usr/bin/python
import time
import numpy as np

def sign (x):
    if (x < 0):
        return -1
    elif (x > 0):
        return 1
    return 0

def extract_data (filename):
    ''' Extract data from the file. Returns it in the format
            [X, Y]
    where X is the list of lists of data points and Y is the list of results.
    '''
    X = []
    Y = []

    input_file = open(filename, 'r')

    for line in input_file:
        line = line.split()
        X += [[float(i) for i in line[:-1]]]
        Y += [float(line[-1])]
    
    return [X, Y]

def transform_data (X):
    ''' Transform data of the form
            [x, y, 1]
        into
            [x, y, x^2, y^2, xy, |x-y|, |x+y|, 1]
    '''
    list_X = X.tolist()
    transformed_X = []

    # transform each vector
    for x in list_X:
        transformed_X += [[x[0], x[1], x[0]*x[0], x[1]*x[1], x[0]*x[1],
                           abs(x[0] - x[1]), abs(x[0] + x[1]), 1]]

    return np.matrix(transformed_X)

train_data_file = "in.dta"
test_data_file = "out.dta"
k = 2  # k value for computing lambda
e_in = 0
e_out = 0

# extract training data
train_data = extract_data(train_data_file)
train_data[0] = transform_data(np.matrix(train_data[0])).tolist()
train_data_size = len(train_data[0])

# generate testing data
test_data = extract_data(test_data_file)
test_data[0] = transform_data(np.matrix(test_data[0])).tolist()
test_data_size = len(test_data[0])

# construct matrices X and Y
X = np.matrix(train_data[0])
Y = np.matrix(train_data[1])

# transform X into (x, y, x^2, y^2, xy, |x-y|, |x+y|, 1)
X = transform_data(X)

# compute the regularization matrix
reg_matrix = 10**k * np.identity(len(train_data[0][0]))

# compute weights for g (using (x, y, x^2, y^2, xy, |x-y|, |x+y|, 1))
pseudo_inv_X = ((X.T * X) + reg_matrix).I * X.T
w_g = (pseudo_inv_X * Y.T).tolist()
w_g = [w_g[0][0], w_g[1][0], w_g[2][0], w_g[3][0],
       w_g[4][0], w_g[5][0], w_g[6][0], w_g[7][0]]

# compute e_in
number_of_errors = 0
for i in range(train_data_size):
    if sign((train_data[0][i][0] * w_g[0]) +
            (train_data[0][i][1] * w_g[1]) +
            (train_data[0][i][2] * w_g[2]) +
            (train_data[0][i][3] * w_g[3]) +
            (train_data[0][i][4] * w_g[4]) +
            (train_data[0][i][5] * w_g[5]) +
            (train_data[0][i][6] * w_g[6]) +
            (train_data[0][i][7] * w_g[7])) != train_data[1][i]:
        number_of_errors += 1
e_in = number_of_errors / (train_data_size + 0.0)

# estimate e_out
number_of_errors = 0
for i in range(test_data_size):
    if sign((test_data[0][i][0] * w_g[0]) +
            (test_data[0][i][1] * w_g[1]) +
            (test_data[0][i][2] * w_g[2]) +
            (test_data[0][i][3] * w_g[3]) +
            (test_data[0][i][4] * w_g[4]) +
            (test_data[0][i][5] * w_g[5]) +
            (test_data[0][i][6] * w_g[6]) +
            (test_data[0][i][7] * w_g[7])) != test_data[1][i]:
        number_of_errors += 1
e_out = number_of_errors / (test_data_size + 0.0)

print 'k =', k
print 'Average value of e_in  =', e_in
print 'Average value of e_out =',e_out
