# Problem Set 1, problem 3

def item_order(order):
    '''
    The string order should contain only words for the items the customer can order separated by one space.
    The function returns a string that counts the number of each item and consolidates them in the following order:
    salad:[# salad] hamburger:[# hambruger] water:[# water]
    '''
    # Variables for items
    salad = 0
    hamburger = 0
    water = 0
    
    # Store the items in the order in item_list
    item_list = order.rsplit()

    # Count the number of items
    for item in item_list:
        if item == 'salad':
            salad += 1
        elif item == 'hamburger':
            hamburger += 1
        elif item == 'water':
            water += 1

    # Return the result in the required format
    return ('salad:' + str(salad) + ' hamburger:' + str(hamburger) + ' water:' + str(water))
