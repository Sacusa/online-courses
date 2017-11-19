#!/usr/bin/python
from sklearn import svm

def extract_data (filename):
    ''' Extract data from the file. Returns it in the format
            [X, Y]
    where X is the list of lists of data points and Y is the list of results.
    '''
    X = []
    Y = []

    input_file = open(filename, 'r')

    for line in input_file:
        line = line.strip().split()
        X += [[float(i) for i in line[1:]]]
        Y += [int(float(line[0]))]
    
    return [X, Y]

C = 0.01
DEGREE = 2
train_data = extract_data('features.train')
test_data = extract_data('features.test')
train_data_size = len(train_data[0])
test_data_size = len(test_data[0])

print '======================'
print 'One vs All Classifiers'
print '======================'

for train_digit in range(10):
    # convert all i's in Y to +1 and not i's to -1
    digit_train_data = [train_data[0][:], train_data[1][:]]
    for digit in range(train_data_size):
        if digit_train_data[1][digit] == train_digit:
            digit_train_data[1][digit] = 1
        else:
            digit_train_data[1][digit] = -1

    # train SVM classifier
    classifier = svm.SVC(C=C, kernel='poly', degree=DEGREE)
    classifier.fit(digit_train_data[0], digit_train_data[1])

    # print the results
    print ' ', train_digit, 'vs All'
    print '    Ein =', 1 - classifier.score(digit_train_data[0], digit_train_data[1])
    print '    Number of Support Vectors =', classifier.n_support_[0] + classifier.n_support_[1]

print ''
print '=================='
print '1 vs 5 Classifiers'
print '=================='

digit_train_data = [[], []]
digit_test_data = [[], []]

# in training data, convert all 1's in Y to +1 and 5's to -1
for digit in range(train_data_size):
    if train_data[1][digit] == 1:
        digit_train_data[0] += [train_data[0][digit]]
        digit_train_data[1] += [1]
    elif train_data[1][digit] == 5:
        digit_train_data[0] += [train_data[0][digit]]
        digit_train_data[1] += [-1]

# in testing data, convert all 1's in Y to +1 and 5's to -1
for digit in range(test_data_size):
    if test_data[1][digit] == 1:
        digit_test_data[0] += [test_data[0][digit]]
        digit_test_data[1] += [1]
    elif test_data[1][digit] == 5:
        digit_test_data[0] += [test_data[0][digit]]
        digit_test_data[1] += [-1]

for DEGREE in [2, 5]:
    print '  Q =', DEGREE
    for C in [0.0001, 0.001, 0.01, 0.1, 1]:
        # train SVM classifier
        classifier = svm.SVC(C=C, kernel='poly', degree=DEGREE)
        classifier.fit(digit_train_data[0], digit_train_data[1])

        # print the results
        print '    C =', C
        print '      Ein =', 1 - classifier.score(digit_train_data[0], digit_train_data[1])
        print '      Eout =', 1 - classifier.score(digit_test_data[0], digit_test_data[1])
        print '      Number of Support Vectors =', classifier.n_support_[0] + classifier.n_support_[1]
