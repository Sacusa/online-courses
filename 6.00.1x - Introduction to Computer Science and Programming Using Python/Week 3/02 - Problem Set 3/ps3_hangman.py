# 6.00 Problem Set 3
# 
# Hangman game
#

# -----------------------------------
# Helper code
# You don't need to understand this helper code,
# but you will have to know how to use the functions
# (so be sure to read the docstrings!)

import random
import string

WORDLIST_FILENAME = "words.txt"

def loadWords():
    """
    Returns a list of valid words. Words are strings of lowercase letters.
    
    Depending on the size of the word list, this function may
    take a while to finish.
    """
    print "Loading word list from file..."
    # inFile: file
    inFile = open(WORDLIST_FILENAME, 'r', 0)
    # line: string
    line = inFile.readline()
    # wordlist: list of strings
    wordlist = string.split(line)
    print "  ", len(wordlist), "words loaded."
    return wordlist

def chooseWord(wordlist):
    """
    wordlist (list): list of words (strings)

    Returns a word from wordlist at random
    """
    return random.choice(wordlist)

# end of helper code
# -----------------------------------

# Load the list of words into the variable wordlist
# so that it can be accessed from anywhere in the program
wordlist = loadWords()

def isWordGuessed(secretWord, lettersGuessed):
    '''
    secretWord: string, the word the user is guessing
    lettersGuessed: list, what letters have been guessed so far
    returns: boolean, True if all the letters of secretWord are in lettersGuessed;
      False otherwise
    '''
    # Iterate over the letters in secretWord
    for letter in secretWord:

        # Continue iterating if the letter is in lettersGuessed
        if letter in lettersGuessed:
            continue

        # Otherwise return False
        else:
            return False

    # If reached the end of loop, all letters are guessed
    return True


def getGuessedWord(secretWord, lettersGuessed):
    '''
    secretWord: string, the word the user is guessing
    lettersGuessed: list, what letters have been guessed so far
    returns: string, comprised of letters and underscores that represents
      what letters in secretWord have been guessed so far.
    '''
    # Empty string for the guessed word
    guessedWord = ''

    # Empty list for letters in the guessed word
    guessedLetters = []
    
    # Iterate over the letters in secretWord
    for letter in secretWord:

        # Insert the letter in guessedWord if it is in lettersGuessed
        if letter in lettersGuessed:
            guessedLetters.append(letter)

        # Otherwise insert '_' in guessedWord
        else:
            guessedLetters.append('_ ')

    # Construct guessedWord from guessedLetters
    guessedWord = ''.join(guessedLetters)

    # Return the guessed word
    return guessedWord


def getAvailableLetters(lettersGuessed):
    '''
    lettersGuessed: list, what letters have been guessed so far
    returns: string, comprised of letters that represents what letters have not
      yet been guessed.
    '''
    # Store all lowercase letters in availableLetters
    availableLetters = string.ascii_lowercase

    # Iterate over lettersGuessed
    for letter in lettersGuessed:

        # Replace all the guessed letters with ''
        availableLetters = availableLetters.replace(letter, '')

    # Return the string availableLetters
    return availableLetters


def hangman(secretWord):
    '''
    secretWord: string, the secret word to guess.

    Starts up an interactive game of Hangman.

    * At the start of the game, let the user know how many 
      letters the secretWord contains.

    * Ask the user to supply one guess (i.e. letter) per round.

    * The user should receive feedback immediately after each guess 
      about whether their guess appears in the computers word.

    * After each round, you should also display to the user the 
      partially guessed word so far, as well as letters that the 
      user has not yet guessed.

    Follows the other limitations detailed in the problem write-up.
    '''
    # Welcome screen
    print('Welcome to the game, Hangman!')
    print('I am thinking of a word that is ' + str(len(secretWord)) + ' letters long')

    # List of letters guessed so far
    lettersGuessed = []

    # The number of incorrect guesses made
    mistakesMade = 0

    # The list of available letters
    availableLetters = getAvailableLetters(lettersGuessed)

    # Run the game while the player can make mistakes
    while mistakesMade < 8:

        # The list of available letters
        availableLetters = getAvailableLetters(lettersGuessed)

        # Initial value for guessedWord
        guessedWord = getGuessedWord(secretWord, lettersGuessed)

        # Display the interface
        print('---------------')
        print('You have ' + str(8 - mistakesMade) + ' mistakes left.')
        print('Available letters: ' + availableLetters)

        # Get user's input
        userInput = str(raw_input('Please guess a letter: '))
        userInput = userInput.lower()

        # Check if the guessed character is in availableLetters
        if userInput in availableLetters:
            
            lettersGuessed.append(userInput)
            guessedWord = getGuessedWord(secretWord, lettersGuessed)
            
            # Check if the guess is correct
            if userInput in secretWord:
                print('Good guess: ' + guessedWord)

            # Check if the guess is incorrect
            else:
                print('Oops! That letter is not in my word: ' + guessedWord)
                mistakesMade += 1

        # Check if the guessed character is a letter
        elif userInput in string.ascii_lowercase:

            lettersGuessed.append(userInput)
            guessedWord = getGuessedWord(secretWord, lettersGuessed)
            
            print('Oops! You\'ve already guessed that letter: ' + guessedWord)

        # The guessed character is not a letter
        else:
            
            print('Oops! That is not a valid letter: ' + guessedWord)

        # Stop the game if the word is guessed
        if isWordGuessed(secretWord, lettersGuessed):
            break

    # Check if the user has won
    if mistakesMade < 8:
        print('---------------')
        print('Congratulations, you won!')

    # The user ran out of guesses
    else:
        print('---------------')
        print('Sorry, you ran out of guesses. The word was ' + secretWord + '.')




# When you've completed your hangman function, uncomment these two lines
# and run this file to test! (hint: you might want to pick your own
# secretWord while you're testing)

secretWord = chooseWord(wordlist).lower()
hangman(secretWord)
