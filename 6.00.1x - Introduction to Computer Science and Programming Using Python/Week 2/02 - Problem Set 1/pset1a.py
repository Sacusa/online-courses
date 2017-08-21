# Problem Set 1, problem 1

# Global variables
vowels = 'aeiou'
vowelCount = 0

# Counting the number of vowels in string s (provided by the grader)
for letter in s:
    if letter in vowels:
        vowelCount += 1

# Print the result
print('Number of vowels: ' + str(vowelCount))
