from ps4a import *
import time


#
#
# Problem #6: Computer chooses a word
#
#
def compChooseWord(hand, wordList, n):
    """
    Given a hand and a wordList, find the word that gives 
    the maximum value score, and return it.

    This word should be calculated by considering all the words
    in the wordList.

    If no words in the wordList can be made from the hand, return None.

    hand: dictionary (string -> int)
    wordList: list (string)
    n: integer (HAND_SIZE; i.e., hand size required for additional points)

    returns: string or None
    """
    # Create a new variable to store the maximum score seen so far (initially 0)
    bestScore = 0

    # Create a new variable to store the best word seen so far (initially None)
    bestWord = None

    # For each word in the wordList
    for word in wordList:

        # If you can construct the word from your hand
        if isValidCompWord(word, hand):

            # Find out how much making that word is worth
            score = getWordScore(word, n)

            # If the score for that word is higher than your best score
            if score > bestScore:

                # Update your best score, and best word accordingly
                bestScore = score
                bestWord = word

    # return the best word you found.
    return bestWord


def isValidCompWord(word, hand):
    """
    Similiar to isValidWord(),
    but does not check if the word is in the wordList
    
    Returns True if word is entirely composed of letters in the hand.
    Otherwise, returns False.

    Does not mutate hand or wordList.
   
    word: string
    hand: dictionary (string -> int)
    """
    # Create a copy of the hand
    handCopy = hand.copy()
    
    # Check if each letter is in hand in required quantity
    for letter in word:
        if handCopy.get(letter, 0) > 0:
            handCopy[letter] -= 1
        else:
            return False

    return True


#
# Problem #7: Computer plays a hand
#
def compPlayHand(hand, wordList, n):
    """
    Allows the computer to play the given hand, following the same procedure
    as playHand, except instead of the user choosing a word, the computer 
    chooses it.

    1) The hand is displayed.
    2) The computer chooses a word.
    3) After every valid word: the word and the score for that word is 
    displayed, the remaining letters in the hand are displayed, and the 
    computer chooses another word.
    4)  The sum of the word scores is displayed when the hand finishes.
    5)  The hand finishes when the computer has exhausted its possible
    choices (i.e. compChooseWord returns None).
 
    hand: dictionary (string -> int)
    wordList: list (string)
    n: integer (HAND_SIZE; i.e., hand size required for additional points)
    """
    # Keep track of the total score
    totalScore = 0

    # Infinite loop for the game
    while True:

        # Display the hand, if not empty
        if calculateHandlen(hand) > 0:
            print('Current Hand:'),
            displayHand(hand)

        # Choose a new word
        word = compChooseWord(hand, wordList, n)

        # Check if the game is over i.e. the word is None
        if word == None:
            break
        
        # Display how many points the word earned, and the updated total score, in one line followed by a blank line
        score = getWordScore(word, n)
        totalScore += score
        print('"' + word + '" earned ' + str(score) + ' points. Total: ' + str(totalScore))
        print
                
        # Update the hand 
        hand = updateHand(hand, word)        

    # Game is over, so display the total score
    print('Total score: ' + str(totalScore) + ' points.')


#
# Problem #8: Playing a game
#
#
def playGame(wordList):
    """
    Allow the user to play an arbitrary number of hands.
 
    1) Asks the user to input 'n' or 'r' or 'e'.
        * If the user inputs 'e', immediately exit the game.
        * If the user inputs anything that's not 'n', 'r', or 'e', keep asking them again.

    2) Asks the user to input a 'u' or a 'c'.
        * If the user inputs anything that's not 'c' or 'u', keep asking them again.

    3) Switch functionality based on the above choices:
        * If the user inputted 'n', play a new (random) hand.
        * Else, if the user inputted 'r', play the last hand again.
      
        * If the user inputted 'u', let the user play the game
          with the selected hand, using playHand.
        * If the user inputted 'c', let the computer play the 
          game with the selected hand, using compPlayHand.

    4) After the computer or user has played the hand, repeat from step 1

    wordList: list (string)
    """
    hand = {}
    prevHand = hand
    gameChoice = ''
    userChoice = ''

    # Run the game while user does not press 'e' for exit
    while gameChoice != 'e':

        # Take user input about playing a game i.e. game choice
        gameChoice = str(raw_input(('Enter n to deal a new hand, r to replay the last hand, or e to end game: ')))
        print

        # Start a new game
        if gameChoice == 'n':
            hand = dealHand(HAND_SIZE)
            prevHand = hand

            # Infinite loop for userChoice error handling
            while True:

                # Take the user input about who plays the game i.e. user choice
                userChoice = str(raw_input('Enter u to have yourself play, c to have the computer play: '))
                print

                # The user plays the game
                if userChoice == 'u':
                    playHand(hand, wordList, HAND_SIZE)
                    print
                    break

                # The computer plays the game        
                elif userChoice == 'c':
                    compPlayHand(hand, wordList, HAND_SIZE)
                    print
                    break

                # Invalid input
                else:
                    print('Invalid input, please try again.')
                    print
                    continue

        # Replay the previous game
        elif gameChoice == 'r':

            # Check if it is the first game
            if prevHand == {}:
                print('You have not played a hand yet. Please play a new hand first!')
                print

            # Otherwise start the game with the previous hand
            else:
                
                # Infinite loop for userChoice error handling
                while True:

                    # Take the user input about who plays the game i.e. user choice
                    userChoice = str(raw_input('Enter u to have yourself play, c to have the computer play: '))
                    print

                    # The user plays the game with the previous hand
                    if userChoice == 'u':
                        playHand(prevHand, wordList, HAND_SIZE)
                        print
                        break

                    # The computer plays the game with the previous hand
                    elif userChoice == 'c':
                        compPlayHand(prevHand, wordList, HAND_SIZE)
                        print
                        break

                    # Invalid input
                    else:
                        print('Invalid input, please try again.')
                        print
                        continue

        # Exit the game
        elif gameChoice == 'e':
            continue

        # Invalid input
        else:
            print('Invalid input, please try again.')


#
# Build data structures used for entire session and play game
#
if __name__ == '__main__':
    wordList = loadWords()
    playGame(wordList)


