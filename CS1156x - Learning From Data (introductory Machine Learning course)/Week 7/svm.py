#!/usr/bin/python
from sklearn import svm
import random
import time

def sign (x):
    if (x < 0):
        return -1
    elif (x > 0):
        return 1
    return 0

def generate_rand_data (n, w):
    ''' Generates training data of size n, using weight vector w.
    '''
    orig_n = n
    X = []
    Y = []

    got_pos = False
    got_neg = False

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

        # make sure we have atleast one +1 and one -1
        if t_s > 0:
            got_pos = True
        elif t_s < 0:
            got_neg = True

        if ((not got_pos) or (not got_neg)) and (n == 0):
            n = orig_n
            X = []
            Y = []
    
    return [X, Y]

def perceptron_learning (train_data, test_data):
    train_data_size = len(train_data[0])
    test_data_size = len(test_data[0])

    # initialize weights for g
    w_g = [0, 0, 0]

    misclassified_data = []

    # learn the function f
    while (True):
        # determine misclassified points
        for i in range(train_data_size):
            if (sign((w_g[0] * train_data[0][i][0]) + (w_g[1] * train_data[0][i][1]) + \
                     (w_g[2] * train_data[0][i][2])) != train_data[1][i]):
                misclassified_data += [i]

        # if no misclassified point, we're done!
        if len(misclassified_data) == 0:
            break

        # otherwise, pick a random misclassified point
        rand_point = misclassified_data[random.randint(0, len(misclassified_data) - 1)]

        # update weight vector
        w_g[0] += (train_data[1][rand_point] * train_data[0][rand_point][0])
        w_g[1] += (train_data[1][rand_point] * train_data[0][rand_point][1])
        w_g[2] += (train_data[1][rand_point] * train_data[0][rand_point][2])

        misclassified_data = []
    
    # calculate probability of disagreement
    prob_disagreement = 0
    for i in range(test_data_size):
        if (sign((w_g[0] * test_data[0][i][0]) + (w_g[1] * test_data[0][i][1]) + \
                 (w_g[2] * test_data[0][i][2])) != test_data[1][i]):
            prob_disagreement += 1
    
    return prob_disagreement / (test_data_size + 0.0)

def support_vector_machine (train_data, test_data):
    classifier = svm.SVC(C=float("inf"), kernel='linear')
    classifier.fit(train_data[0], train_data[1])

    # calculate probability of disagreement
    test_data_size = len(test_data[0])
    test_data_results = classifier.predict(test_data[0])
    prob_disagreement = 0

    for i in range(test_data_size):
        if test_data_results[i] != test_data[1][i]:
            prob_disagreement += 1
    prob_disagreement /= (test_data_size + 0.0)

    # get number of support vectors
    number_support_vectors = classifier.n_support_[0] + classifier.n_support_[1]

    return [prob_disagreement, number_support_vectors]

number_of_trials = 1000
train_data_size = 100
test_data_size = 1000
times_svm_better = 0  # number of times SVM performs better than PLA
number_support_vectors = 0

for i in range(number_of_trials):
    # set the random function f
    random.seed(time.clock() * (i + 1))
    f_point_1_x = random.uniform(-1,1)
    f_point_1_y = random.uniform(-1,1)
    f_point_2_x = random.uniform(-1,1)
    f_point_2_y = random.uniform(-1,1)
    m = ((f_point_2_y - f_point_1_y) / (f_point_2_x - f_point_1_x))
    w_f = [-m, 1, (f_point_1_x * m) - f_point_1_y]

    # generate training and testing data
    train_data = generate_rand_data(train_data_size, w_f)
    test_data = generate_rand_data(test_data_size, w_f)
    
    # determine probability of disagreement for PLA
    prob_disagreement_pla = perceptron_learning(train_data, test_data)

    # determine probability of disagreement and number of support vectors for SVM
    svm_return_value = support_vector_machine(train_data, test_data)
    prob_disagreement_svm = svm_return_value[0]
    number_support_vectors += svm_return_value[1]

    # check if SVM predicted a better model
    if prob_disagreement_svm < prob_disagreement_pla:
        times_svm_better += 100

print 'Percentage of times gSVM > gPLA:', times_svm_better / (number_of_trials+0.0)
print 'Average number of support vectors:', number_support_vectors / (number_of_trials+0.0)
