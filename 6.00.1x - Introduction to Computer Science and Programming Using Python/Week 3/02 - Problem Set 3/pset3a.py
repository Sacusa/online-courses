# Problem Set 3, problem 1

def radiationExposure(start, stop, step):
    '''
    Computes and returns the amount of radiation exposed
    to between the start and stop times. Calls the 
    function f (defined for you in the grading script)
    to obtain the value of the function at any point.
 
    start: integer, the time at which exposure begins
    stop: integer, the time at which exposure ends
    step: float, the width of each rectangle. You can assume that
      the step size will always partition the space evenly.

    returns: float, the amount of radiation exposed to 
      between start and stop times.
    '''
    # Variable for the total amount of radiation
    totalRadiation = 0

    # Variable for holding the current time
    time = start

    # Iterate over the time intervals and add the radiation to the total radiation
    while time < stop:
        totalRadiation += step * f(time)
        time += step

    # Return the computed value
    return totalRadiation
