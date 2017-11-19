#!/usr/bin/python
from sklearn import svm
from sklearn.model_selection import cross_val_score

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

CV = 10
DEGREE = 2
NUMBER_OF_TRIALS = 100
train_data = extract_data('features.train')
train_data_size = len(train_data[0])

digit_train_data = [[], []]

# convert all 1's in Y to +1 and 5's to -1
for digit in range(train_data_size):
    if train_data[1][digit] == 1:
        digit_train_data[0] += [train_data[0][digit]]
        digit_train_data[1] += [1]
    elif train_data[1][digit] == 5:
        digit_train_data[0] += [train_data[0][digit]]
        digit_train_data[1] += [-1]


frequency = {}
average_e_cv = {}
for C in [0.0001, 0.001, 0.01, 0.1, 1]:
    frequency[str(C)] = 0
    average_e_cv[str(C)] = 0

for trial in range(NUMBER_OF_TRIALS):
    lowest_e_cv = 2
    lowest_c = 0
    for C in [0.0001, 0.001, 0.01, 0.1, 1]:
        # compute cross validation score
        classifier = svm.SVC(C=C, kernel='poly', degree=DEGREE)
        cross_val_scores = cross_val_score(classifier, digit_train_data[0], digit_train_data[1], cv=CV)
        
        # compute Ecv
        e_cv = 1 - cross_val_scores.mean()
        average_e_cv[str(C)] += e_cv

        # get the lowest Ecv, and the corresponding C
        if (e_cv < lowest_e_cv) or ((e_cv == lowest_e_cv) and (C < lowest_c)):
            lowest_e_cv = e_cv
            lowest_c = C
        
    frequency[str(lowest_c)] += 1

# print the results
for C in [0.0001, 0.001, 0.01, 0.1, 1]:
    average_e_cv[str(C)] /= NUMBER_OF_TRIALS
    print 'C =', C
    print '  Frequency =', frequency[str(C)]
    print '  Average Ecv =', average_e_cv[str(C)]
