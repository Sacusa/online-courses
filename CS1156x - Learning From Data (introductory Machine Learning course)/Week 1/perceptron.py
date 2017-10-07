#!/usr/bin/python
import random
import time

def sign (x):
    if (x < 0):
        return -1
    elif (x > 0):
        return 1
    return 0

def generate_train_data (n, w):
    ''' Generates training data of size n, using weight vector w.
    '''
    train_data = []
    while (n > 0):
        t_x = random.uniform(-1,1)
        t_y = random.uniform(-1,1)
        t_s = sign((w[0] * t_x) + (w[1] * t_y) + w[2])

        # ignore points on the line
        if t_s == 0:
            continue

        train_data += [[t_x, t_y, t_s]]
        n -= 1
    return train_data

number_of_trials = input('Number of trials: ')
train_data_size = input('Size of training data: ')
number_of_iterations = 0
prob_disagreement = 0

for i in range(number_of_trials):
    # set the random function f
    random.seed(time.clock() * (i + 1))
    f_point_1_x = random.uniform(-1,1)
    f_point_1_y = random.uniform(-1,1)
    f_point_2_x = random.uniform(-1,1)
    f_point_2_y = random.uniform(-1,1)
    m = ((f_point_2_y - f_point_1_y) / (f_point_2_x - f_point_1_x))

    # initialize weights for f and g
    w_f = [-m, 1, (f_point_1_x * m) - f_point_1_y]
    w_g = [0, 0, 0]

    # instantiate training data
    train_data = generate_train_data(train_data_size, w_f)
    misclassified_data = []

    # determine the number of iterations
    while (True):
        # determine misclassified points
        for point in train_data:
            if (sign((w_g[0] * point[0]) + (w_g[1] * point[1]) + w_g[2]) != point[2]):
                misclassified_data += [point]

        # if no misclassified point, we're done!
        if len(misclassified_data) == 0:
            break

        # otherwise, pick a random misclassified point
        rand_point = misclassified_data[random.randint(0, len(misclassified_data) - 1)]

        # update weight vector
        w_g[0] += (rand_point[2] * rand_point[0])
        w_g[1] += (rand_point[2] * rand_point[1])
        w_g[2] += rand_point[2]

        misclassified_data = []
        number_of_iterations += 1
    
    # determine the disagreement
    prob_data = generate_train_data(1000, w_f)
    specific_prob = 0
    for point in prob_data:
        if (sign((w_g[0] * point[0]) + (w_g[1] * point[1]) + w_g[2]) != point[2]):
            specific_prob += 1
    prob_disagreement += specific_prob / 1000.0

print 'Average number of iterations:', number_of_iterations/(number_of_trials + 0.0)
print 'Average probability of disagreement:', prob_disagreement/(number_of_trials + 0.0)
