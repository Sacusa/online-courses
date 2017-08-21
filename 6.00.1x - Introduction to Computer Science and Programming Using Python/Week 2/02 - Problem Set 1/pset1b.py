# Problem Set 1, problem 2

# Global variables
s_len = len(s)
substring = 'bob'
subCount = 0
prevIndex = 0

# Counting the number of occurences of substring sub
# in string s (provided by the grader)
prevIndex = s.find(substring, prevIndex, s_len)
while prevIndex != -1:
    subCount += 1
    prevIndex = s.find(substring, prevIndex + 1, s_len)

# Print the result
print('Number of times ' + substring + ' occurs is: ' + str(subCount))
