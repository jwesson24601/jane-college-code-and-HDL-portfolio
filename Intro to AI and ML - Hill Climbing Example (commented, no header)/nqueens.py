# Author: Jane Wesson
# Title: n Queens

import random # for inital state generation

# This function generates the successor states for the state provided to it,
# by adding one and subtracting one from each element in the state excluding
# the static point.
#
# state - current state of the n queens
# static_x - column of static queen
# static_y - row of static queen
def succ(state, static_x, static_y):
    succ = [] # list of successors
    stateCopy = state[0:] # copy of state
    n = len(state) # board size
    if state[static_x] != static_y: # if static point isn't correct, return empty
        return succ
    
    # go through the state
    for i in range(n):
        if i == static_x: # ignore static point
            continue
        
        if stateCopy[i] > 0: # check if queen can be moved up a row
            stateCopy[i] -= 1
            succ.append(stateCopy)
            stateCopy = state[0:]
            
        if stateCopy[i] < n - 1: # check if queen can be moved down a row
            stateCopy[i] += 1
            succ.append(stateCopy)
            stateCopy = state[0:]
        
    return sorted(succ)

# This function determines the f value for a state by calculating how many
# queens are attacking each other.
#
# state - the current state of the n queens
def f(state):
    f = 0 # f score
    n = len(state) # size of board
    
    # check every queen one by one to see if they are being attacked
    for i in range(n):
        for j in range(n):
            
            if state[i] == state[j] and j != i: # checks if two queens are in same row
                f += 1
                break
            
            # checks diagonals of up and right and down and right
            if i+j < n and j != 0 and (state[i+j] == state[i] + j or state[i+j] == state[i] - j):
                f += 1
                break
            
            # checks diagonals of up and left and down and left
            if i-j >= 0 and j != 0 and (state[i-j] == state[i] - j or state[i-j] == state[i] + j):
                f += 1
                break
    
    return f

# This function selects a single successor of the current state out of the 
# successors returned by succ and then returns that state.
#
# curr - current state of the board
# static_x - column of the static queen on the board
# static_y - row of the static queen on the board
def choose_next(curr, static_x, static_y):
    successors = succ(curr, static_x, static_y) # all successors
    possible = [curr] # list of successors of lowest f score
    
    if len(successors) == 0: # check if there are legitimate successors at all
        return None
    
    lowF = f(curr) # set lowest f to f or current state
    
    # look through all successors
    for i in range(len(successors)):
        # get f score of current successor
        currF = f(successors[i])
        if currF < lowF: # if lower f, make new possible list
            possible = []
            possible.append(successors[i])
            lowF = currF            
        elif currF == lowF: # if f is same, just add to current possible list
            possible.append(successors[i])
        
    # pick state with lowest value if state written as an integer
    possible.sort()        
    return possible[0]

# This function runs a hill climbing algorithm on an n queens board and returns
# the state it finds with the lowest f. It can print out the steps it took to
# get to that state as well.
#
# state - the current state of the board
# static_x - column of the static queen on the board
# static_y - row of the static queen on the board
# printPath - boolean that decides if path to lowest f state is printed or not
def n_queens(state, static_x, static_y, printPath=True):
    currState = state # current state of board
    nextState = [] # next state of the board
    while True:
        currF = f(currState) # get current state f
        
        # print state if printPath
        if printPath:
            print(currState, " - f=", currF, sep="")
        
        # get next state
        nextState = choose_next(currState, static_x, static_y)
        # if no next state or next state is current state, exit immidiately
        if nextState == None or nextState == currState:
            break
        # else if f score of next state is the same but next state != current state
        elif f(nextState) == currF:
            currState = nextState # set current state to last state
            if printPath: # print the last state if printPath
                print(currState, " - f=", f(currState), sep="")
            break
        
        currState = nextState
    
    return currState # return the last state
        
# This function runs a hill climbing algorithm k times with random starting
# states in order to find a solution to an n queens problem of board size n. If
# it does not find a solution, it prints all the states it found with the least
# number of queens attacking each other.
#
# n - size of the board
# k - number of times to restart the hill climbing algorithm
# static_x - column of the static queen on the board
# static_y - row of the static queen on the board
def n_queens_restart(n, k, static_x, static_y):
    random.seed(1) # set seed to same value for predictable outcomes
    goalStates = [] # current list of states that are all at same f score
    lowF = -1 # set lowF to impossible value to indicate it hasn't been set yet
    
    # outer loop that runs algorithm over and over again
    for i in range(k):
        state = [] # initial state
        # populate state with random values excluding static queen
        for j in range(n):
            if j == static_x:
                state.append(static_y)
                continue
            state.append(random.randint(0,n-1))
        
        # determine lowest f state that can be reached from initial state
        goalState = n_queens(state, static_x, static_y, False)
        goalF = f(goalState) # current f score of lowest f state
        if goalF == 0: # solution found case
            print(goalState, " - f=", goalF, sep="")
            return
        elif goalF < lowF or lowF == -1: # new lowest f score state case
            goalStates = []
            goalStates.append(goalState)
            lowF = goalF
        elif goalF == lowF: # equally low f score state case
            goalStates.append(goalState)
    
    # if no solution found, sort list and print all lowest f states
    goalStates.sort()
    
    for i in range(len(goalStates)):
        print(goalStates[i], " - f=", lowF, sep="")
        
    return