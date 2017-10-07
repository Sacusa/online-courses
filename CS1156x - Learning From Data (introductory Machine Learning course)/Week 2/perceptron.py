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

def generate_train_data (n, w):
    ''' Generates training data of size n, using weight vector w.
    '''
    X = []
    Y = []
    while (n > 0):
        t_x = random.uniform(-1,1)
        t_y = random.uniform(-1,1)
        t_s = sign((w[0] * t_x) + (w[1] * t_y) + w[2])

        # ignore points on the line
        if t_s == 0:
            continue

        X += [[t_x, t_y, 1]]
        Y += [t_s]
        n -= 1
    
    return [X, Y]

number_of_trials = input('Number of trials: ')
train_data_size = input('Size of training data: ')
number_of_iterations = 0

for i in range(number_of_trials):
    # set the random function f
    random.seed(time.clock() * (i + 1))
    f_point_1_x = random.uniform(-1,1)
    f_point_1_y = random.uniform(-1,1)
    f_point_2_x = random.uniform(-1,1)
    f_point_2_y = random.uniform(-1,1)
    m = ((f_point_2_y - f_point_1_y) / (f_point_2_x - f_point_1_x))

    # initialize weights for f
    w_f = [-m, 1, (f_point_1_x * m) - f_point_1_y]

    # generate training data
    train_data = generate_train_data(train_data_size, w_f)

    # construct matrices X and Y
    X = matrix(train_data[0])
    Y = matrix(train_data[1])

    # compute weights for g
    pseudo_inv_X = (X.T * X).I * X.T
    w_g = (pseudo_inv_X * Y.T).tolist()
    w_g = [w_g[0][0], w_g[1][0], w_g[2][0]]

    misclassified_data = []

    # determine the number of iterations
    while (True):
        # determine misclassified points
        for i in range(train_data_size):
            if sign((train_data[0][i][0] * w_g[0]) +
                    (train_data[0][i][1] * w_g[1]) +
                    (train_data[0][i][2] * w_g[2])) != train_data[1][i]:
                misclassified_data += [i]

        # if no misclassified point, we're done!
        if len(misclassified_data) == 0:
            break

        # otherwise, pick a random misclassified point
        rand_point_index = misclassified_data[random.randint(0, len(misclassified_data) - 1)]

        # update weight vector
        w_g[0] += (train_data[1][rand_point_index] * train_data[0][rand_point_index][0])
        w_g[1] += (train_data[1][rand_point_index] * train_data[0][rand_point_index][1])
        w_g[2] += train_data[1][rand_point_index]

        misclassified_data = []
        number_of_iterations += 1

print 'Average number of iterations =', number_of_iterations / (number_of_trials + 0.0)
