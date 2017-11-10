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

def transform_data_phi3 (X):
    ''' Transform data of the form
            [x, y, 1]
        into
            [x, y, x^2, 1]
    '''
    list_X = X.tolist()
    transformed_X = []

    # transform each vector
    for x in list_X:
        transformed_X += [[x[0], x[1], x[0]*x[0], 1]]

    return np.matrix(transformed_X)

def transform_data_phi4 (X):
    ''' Transform data of the form
            [x, y, 1]
        into
            [x, y, x^2, y^2, 1]
    '''
    list_X = X.tolist()
    transformed_X = []

    # transform each vector
    for x in list_X:
        transformed_X += [[x[0], x[1], x[0]*x[0], x[1]*x[1], 1]]

    return np.matrix(transformed_X)

def transform_data_phi5 (X):
    ''' Transform data of the form
            [x, y, 1]
        into
            [x, y, x^2, y^2, xy, 1]
    '''
    list_X = X.tolist()
    transformed_X = []

    # transform each vector
    for x in list_X:
        transformed_X += [[x[0], x[1], x[0]*x[0], x[1]*x[1], x[0]*x[1], 1]]

    return np.matrix(transformed_X)

def transform_data_phi6 (X):
    ''' Transform data of the form
            [x, y, 1]
        into
            [x, y, x^2, y^2, xy, |x-y|, 1]
    '''
    list_X = X.tolist()
    transformed_X = []

    # transform each vector
    for x in list_X:
        transformed_X += [[x[0], x[1], x[0]*x[0], x[1]*x[1], x[0]*x[1], abs(x[0] - x[1]), 1]]

    return np.matrix(transformed_X)

def transform_data_phi7 (X):
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

def validation_model (train_data, valid_data, test_data, transform):
    e_val = 0
    e_out = 0

    # transform training, validation and testing data
    if transform == 3:
        train_data[0] = transform_data_phi3(np.matrix(train_data[0])).tolist()
        valid_data[0] = transform_data_phi3(np.matrix(valid_data[0])).tolist()
        test_data[0] = transform_data_phi3(np.matrix(test_data[0])).tolist()
    elif transform == 4:
        train_data[0] = transform_data_phi4(np.matrix(train_data[0])).tolist()
        valid_data[0] = transform_data_phi4(np.matrix(valid_data[0])).tolist()
        test_data[0] = transform_data_phi4(np.matrix(test_data[0])).tolist()
    elif transform == 5:
        train_data[0] = transform_data_phi5(np.matrix(train_data[0])).tolist()
        valid_data[0] = transform_data_phi5(np.matrix(valid_data[0])).tolist()
        test_data[0] = transform_data_phi5(np.matrix(test_data[0])).tolist()
    elif transform == 6:
        train_data[0] = transform_data_phi6(np.matrix(train_data[0])).tolist()
        valid_data[0] = transform_data_phi6(np.matrix(valid_data[0])).tolist()
        test_data[0] = transform_data_phi6(np.matrix(test_data[0])).tolist()
    elif transform == 7:
        train_data[0] = transform_data_phi7(np.matrix(train_data[0])).tolist()
        valid_data[0] = transform_data_phi7(np.matrix(valid_data[0])).tolist()
        test_data[0] = transform_data_phi7(np.matrix(test_data[0])).tolist()
    
    train_data_size = len(train_data[0])
    valid_data_size = len(valid_data[0])
    test_data_size = len(test_data[0])

    # construct matrices X and Y
    X = np.matrix(train_data[0])
    Y = np.matrix(train_data[1])

    # compute weights for g
    pseudo_inv_X = (X.T * X).I * X.T
    w_g = (pseudo_inv_X * Y.T).tolist()
    for i in range(transform+1):
        w_g[i] = w_g[i][0]

    # compute e_val
    number_of_errors = 0
    for i in range(valid_data_size):
        computed_value = 0
        for j in range(transform+1):
            computed_value += valid_data[0][i][j] * w_g[j]
        if sign(computed_value) != valid_data[1][i]:
            number_of_errors += 1
    e_val = number_of_errors / (valid_data_size + 0.0)

    # compute e_out
    number_of_errors = 0
    for i in range(test_data_size):
        computed_value = 0
        for j in range(transform+1):
            computed_value += test_data[0][i][j] * w_g[j]
        if sign(computed_value) != test_data[1][i]:
            number_of_errors += 1
    e_out = number_of_errors / (test_data_size + 0.0)

    print 'Average value of e_val (phi', transform, ') =', e_val
    print 'Average value of e_out (phi', transform, ') =', e_out

# extract training and testing data
train_data_file = "in.dta"
test_data_file = "out.dta"
train_data = extract_data(train_data_file)
test_data = extract_data(test_data_file)
valid_data = train_data[:]

# split training data into training and validation data
valid_data[0] = valid_data[0][-10:]
valid_data[1] = valid_data[1][-10:]
train_data[0] = train_data[0][0:25]
train_data[1] = train_data[1][0:25]

for i in range(3,8):
    validation_model(train_data, valid_data, test_data, i)
