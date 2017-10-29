#!/usr/bin/python
import random
import time
import math

def sign (x):
    if (x < 0):
        return -1
    elif (x > 0):
        return 1
    return 0

def generate_rand_data (n, w):
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

def stop(w_t_prev, w_t, delta):
    ''' Returns True if ||w_t_prev - w_t|| < delta. False, otherwise.
    Requires w_t_prev and w_t to be lists of equal length.
    '''
    mag = 0
    for i in range(len(w_t)):
        mag += (w_t_prev[i] - w_t[i]) ** 2
    
    return math.sqrt(mag) < delta

def get_gradient(x, y, w):
    ''' Returns the gradient corresponding to the weights w.
    Requires x to be a list of points, w to be a list of corresponding weights,
    and y to be f(x), where f is the unknown target function.
    '''
    # wx is the product of transpose(w) and x
    wx = 0
    for i in range(len(x)):
        wx += (w[i] * x[i])
    
    # k is the constant term
    k = y / (1 + (math.e ** (y * wx)))

    # compute k * x
    error = []
    for i in range(len(x)):
        error += [k * x[i]]
    
    return error

def get_cross_entropy(x, y, w):
    ''' Returns the cross entropy error.
    Requires x to be a list of points, w to be a list of corresponding weights,
    and y to be f(x), where f is the unknown target function.
    '''
    # wx is the product of transpose(w) and x
    wx = 0
    for i in range(len(x)):
        wx += (w[i] * x[i])
    
    return math.log(1 + (math.e ** ((-y) * wx)))

number_of_trials = 100  # number of times to repeat the experiment
train_data_size = 100   # number of points to train the model on
test_data_size = 100    # number of points to evaluate the model
delta = 0.01            # minimum change in two weight vectors to continue
learning_rate = 0.01    # the learning rate eta

e_out = 0
t = 0

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
    train_data = generate_rand_data(train_data_size, w_f)

    # initialize weights for g
    w_g = [random.random(), random.random(), random.random()]
    w_g_next = [w_g[0] + delta, w_g[1] + delta, w_g[2] + delta]

    # run logistic regression while ||w(t-1) - w(t)|| > delta
    while not stop(w_g, w_g_next, delta):
        t += 1
        w_g = w_g_next[:]

        # calculate gradient of in sample error
        gradient = [0, 0, 0]
        for i in range(train_data_size):
            # calculate local gradient
            local_gradient = get_gradient(train_data[0][i], train_data[1][i], w_g)

            # add to approximate gradient
            gradient[0] += local_gradient[0]
            gradient[1] += local_gradient[1]
            gradient[2] += local_gradient[2]
        
        # average local gradients
        gradient[0] = gradient[0] * (-1/train_data_size)
        gradient[1] = gradient[1] * (-1/train_data_size)
        gradient[2] = gradient[2] * (-1/train_data_size)

        # update weight vector
        w_g_next[0] = w_g[0] - (learning_rate * gradient[0])
        w_g_next[1] = w_g[1] - (learning_rate * gradient[1])
        w_g_next[2] = w_g[2] - (learning_rate * gradient[2])
    
    # generate testing data
    test_data = generate_rand_data(test_data_size, w_f)

    # calculate average cross entropy error
    e_out_local = 0
    for i in range(test_data_size):
        e_out_local += get_cross_entropy(test_data[0][i], test_data[1][i], w_g)
    e_out += (e_out_local / (test_data_size + 0.0))

# print the results
print 'Average Eout:', e_out / (number_of_trials + 0.0)
print 'Average number of epochs:', t / (number_of_trials + 0.0)
