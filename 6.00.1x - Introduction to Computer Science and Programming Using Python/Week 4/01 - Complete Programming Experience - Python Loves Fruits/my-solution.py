def nfruits(fruitsQuantity, fruitsEaten):
    '''
    Assumes:
    - fruitsQuantity is a dictionary with the initial quantities of all fruits
    - fruitsEaten is a string that contains all the consumed fruits in their
      respective order

    Returns the maximum quantity of fruit at the end
    '''

    try:
        # Assert the types of arguments
        assert type(fruitsQuantity) == dict, 'Invalid fruitsQuantity'
        assert type(fruitsEaten) == str, 'Invalid fruitsEaten'
        
        # Check if fruitsQuantity or fruitsEaten is empty
        assert len(fruitsQuantity) != 0, 'Empty fruits dictionary'
        assert len(fruitsEaten) != 0, 'Empty fruits string'

        # Iterate to the second last fruit
        for i in range(len(fruitsEaten) - 1):

            eatenFruit = fruitsEaten[i]

            # Reduce the quantity of eaten fruit
            fruitsQuantity[eatenFruit] -= 1

            # Increase the quantity of other fruits
            for fruit in fruitsQuantity.keys():
                if fruit != eatenFruit:
                    fruitsQuantity[fruit] += 1

        # Reduce the quantity of the last eaten fruit
        fruitsQuantity[fruitsEaten[-1]] -= 1

        # Find the maximum quantity
        maxQuantity = 0
        for fruit in fruitsQuantity.keys():
            if fruitsQuantity[fruit] > maxQuantity:
                maxQuantity = fruitsQuantity[fruit]

        return maxQuantity

    except AssertionError, e:
        print('ERROR: ' + str(e))
        return 0


def test_nfruits():
    '''
    Test cases for the function nfruits
    '''
    # Test case 1
    fruitsQuantity = {}
    fruitsEaten = 'ABCABC'
    if nfruits(fruitsQuantity, fruitsEaten) != 0:
        print("Test case 1 failed\n")

    # Test case 2
    fruitsQuantity = {'A': 1, 'B': 1, 'C': 1}
    fruitsEaten = 'ABCABC'
    if nfruits(fruitsQuantity, fruitsEaten) != 3:
        print("Test case 2 failed\n")

    # Test case 3
    fruitsQuantity = {'A': 1, 'B': 2, 'C': 3}
    fruitsEaten = 'ABCABC'
    if nfruits(fruitsQuantity, fruitsEaten) != 5:
        print("Test case 3 failed\n")

    # Test case 4
    fruitsQuantity = {'A': 3, 'B': 1, 'C': 2}
    fruitsEaten = 'ABCABC'
    if nfruits(fruitsQuantity, fruitsEaten) != 4:
        print("Test case 4 failed\n")

    # Test case 5
    fruitsQuantity = {'A': 2, 'B': 3, 'C': 1}
    fruitsEaten = 'ABCABC'
    if nfruits(fruitsQuantity, fruitsEaten) != 4:
        print("Test case 5 failed\n")

    # Test case 6
    fruitsQuantity = {}
    fruitsEaten = ''
    if nfruits(fruitsQuantity, fruitsEaten) != 0:
        print("Test case 6 failed\n")

    # Test case 7
    fruitsQuantity = {'A': 1, 'B': 1, 'C': 1}
    fruitsEaten = ''
    if nfruits(fruitsQuantity, fruitsEaten) != 0:
        print("Test case 7 failed\n")

    # Test case 8
    fruitsQuantity = {'A': 1, 'B': 2, 'C': 3}
    fruitsEaten = ''
    if nfruits(fruitsQuantity, fruitsEaten) != 0:
        print("Test case 8 failed\n")

    # Test case 9
    fruitsQuantity = {}
    fruitsEaten = 'A'
    if nfruits(fruitsQuantity, fruitsEaten) != 0:
        print("Test case 9 failed\n")

    # Test case 10
    fruitsQuantity = {'A': 1, 'B': 1, 'C': 1}
    fruitsEaten = 'B'
    if nfruits(fruitsQuantity, fruitsEaten) != 1:
        print("Test case 10 failed\n")

    # Test case 11
    fruitsQuantity = {'A': 1, 'B': 2, 'C': 3}
    fruitsEaten = 'C'
    if nfruits(fruitsQuantity, fruitsEaten) != 2:
        print("Test case 11 failed\n")
