'''
Created: 3 September 2019
Author: Max Smith
Goal: Test out various game implementations
'''
# Currently exploring other data structures than used in java implementaiton
# Upon Google searching, I believe a grid may be the best way to represent this data

import os
import logging

log_name = "gameTest.log"
# Clear logger each time
if os.path.isfile(log_name):
    os.remove(log_name)
logging.basicConfig(filename=log_name, level=logging.INFO)

def log_and_print(message):
    logging.info(message)
    print(message)

# Global variables that will be placeholders for character values
tack = "x"
empty = "o"
off_board = "-"
move_separator = " to "

# Gloval variables that govern game rules
row_delta = [1, 0, -1, 0, 1, -1] 
col_delta = [0, 1, 0, -1, 1, -1]
row_start = [0, 1, 2, 2]
col_start = [0, 0, 0, 1]
    
'''Create a full board'''
def create_board(rows):
    board = []
    for num in range(rows):
        board.append(next_row(num, rows))
    log_and_print(board)
    return board

'''Return a row that has been appropriately formatted'''
def next_row(num, rows):
    my_row = ""
    for i in range(rows):
        if i <= num:
            my_row += tack
        else:
            my_row += off_board
    return my_row

'''Verify that tack selected is inbounds on the board'''
def inbounds(board, row, col):
    if row >= 0 and row < len(board) and col >= 0 and col < len(board):
        log_and_print("Tack is inside board range, returning True")
        return(True)
    else:
        log_and_print("Tack is outside board range, returning False")
        return(False)

'''Input an original row, output a new row changing the index value to target'''
def reassign_space(original, index, target):
    my_list = list(original)
    my_list[index] = target
    return ''.join(my_list)

'''Remove a tack from a full board, must make sure it is a valid removal'''
def remove_tack(board, row, col):
    if (inbounds(board, row, col)):
        my_row = board[row]
        my_tack = my_row[col]
        if (my_tack == tack):
            board[row] = reassign_space(board[row], col, empty)
            log_and_print(f"Tack successfully removed, new board is \n {board}")
            return board
        else:
            log_and_print("Tack desired to remove is not a tack")
            return board

'''Encode moves according to a specific sequence, use these to populate list in possible moves'''
def move_encoder(root, target):
    return str(root) + move_separator + str(target)

'''Convert a grid value to a numeric value (e.g. 0,0 is 1)'''
def grid_to_num(row, col):
    if (col > row):
        log_and_print("Nice try, column can't be greater than row")
    elif (col <= row):
        index = 0
        for i in range(row + 1):
            index += i
        num = index + col + 1
        log_and_print(f"Converted coordinate ({row},{col}) to {num}")
        return num


'''From a given board, return numeric locations of all character values'''
def char_locations(board, character, grid):
    char_list, row_list, col_list = [], [], []
    row = 0
    for s in board:
        col = 0
        my_list = list(s)
        for k in my_list:
            if (k == character):
                if (grid == True):
                    char_list.append(grid_to_num(row, col))
                else:
                    row_list.append(row)
                    col_list.append(col)
            col += 1
        row += 1
    if (grid == True):
        log_and_print(f"char_list for char {character}: {char_list}")
        return char_list
    else:
        log_and_print(f"row_list for char {character}: {row_list}")
        log_and_print(f"col_list for char {character}: {col_list}")
        return (row_list, col_list)

'''Verify tack move is valid'''
def valid_move():
    pass

'''Return a list of possible_moves according to a given board'''
def possible_moves(board):
    # NOTE - Any tack might be able to "move"
    # NOTE - All moves must jump over another tack
    # NOTE - All moves must land in an empty (inbounds) space
    tack_list = char_locations(board, character=tack, grid=True)
    empty_list = char_locations(board, character=empty, grid=True)

    tack_rows, tack_cols = char_locations(board, character=tack, grid=False)
    empty_rows, empty_cols = char_locations(board, character=empty, grid=False)
    # Step 2: Take each tack and "try" to move in all directions


# Testing functions as they are written
board = create_board(rows=5)
board = remove_tack(board, row=0, col=0)

possible_moves(board)