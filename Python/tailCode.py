'''
Created: 3 September 2019
Author: Max Smith
Goal: Test out various game implementations
'''
# Currently exploring other data structures than used in java implementaiton
# Upon Google searching, I believe a grid may be the best way to represent this data

import os
import logging
import random
import sys

class TailRecurseException:
    # From http://code.activestate.com/recipes/474088/
  def __init__(self, args, kwargs):
    self.args = args
    self.kwargs = kwargs

def tail_call_optimized(g):
  """
  This function decorates a function with tail call
  optimization. It does this by throwing an exception
  if it is it's own grandparent, and catching such
  exceptions to fake the tail call optimization.
  
  This function fails if the decorated
  function recurses in a non-tail context.
  """
  def func(*args, **kwargs):
    f = sys._getframe()
    if f.f_back and f.f_back.f_back \
        and f.f_back.f_back.f_code == f.f_code:
      raise TailRecurseException(args, kwargs)
    else:
      while 1:
        try:
          return g(*args, **kwargs)
        except TailRecurseException, e:
          args = e.args
          kwargs = e.kwargs
  func.__doc__ = g.__doc__
  return func

log_name = "gameTest.log"
# Clear logger each time
if os.path.isfile(log_name):
    os.remove(log_name)
logging.basicConfig(filename=log_name, level=logging.DEBUG)

def log_and_print(message):
    logging.info(message)
    print(message)

# Global variables that will be placeholders for character values
tack = "x"
empty = "o"
off_board = "-"
move_separator = " to "
round_separator = ","
win_count = 0

# Gloval variables that govern game rules
all_row_delta = [1, 0, -1, 0, 1, -1] 
all_col_delta = [0, 1, 0, -1, 1, -1]
unique_start = [1, 2, 4, 5]
# all_row_start = [0, 1, 2, 2]
# all_col_start = [0, 0, 0, 1]
    
'''Create a full board'''
def create_board(rows):
    board = []
    for num in range(rows):
        board.append(next_row(num, rows))
    # logging.info(board)
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
        # logging.info("Tack is inside board range, returning True")
        return(True)
    else:
        # logging.info("Tack is outside board range, returning False")
        return(False)

'''Input an original row, output a new row changing the index value to target'''
def reassign_space(original, index, target):
    my_list = list(original)
    my_list[index] = target
    return ''.join(my_list)

'''Remove a tack from a full board, must make sure it is a valid removal'''
def remove_tacks(board, all_rows, all_cols):
    for i in range(len(all_rows)):
        row, col = all_rows[i], all_cols[i]
        if (inbounds(board, row, col)):
            my_row = board[row]
            my_tack = my_row[col]
            if (my_tack == tack):
                board[row] = reassign_space(board[row], col, empty)
                # logging.info(f"Tack successfully removed, new board is \n {board}")
            else:
                pass
                # logging.info("Tack desired to remove is not a tack")
    return board

'''Encode moves according to a specific sequence, use these to populate list in possible moves'''
# TODO Make dynamic by move size
def move_encoder(start_pos, mid_pos, end_pos):
    return str(start_pos) + move_separator + str(mid_pos) + move_separator + str(end_pos)

'''Convert a grid value to a numeric value (e.g. 0,0 is 1)'''
def grid_to_num(row, col):
    if (col > row):
        pass
        # logging.info("Nice try, column can't be greater than row")
    elif (col <= row):
        index = 0
        for i in range(row + 1):
            index += i
        num = index + col + 1
        # logging.info(f"Converted coordinate ({row},{col}) to {num}")
        return num

'''Convert a numeric value to row and column values'''
def num_to_grid(num):
    # NOTE - Idea, what if I had a general map to lookup row, col values based on index
    # NOTE - ^^^ Is this more efficient?

    # Check edge cases first
    if (num <= 0):
        # logging.info(f"Invalid grid number ({num}), try again")
        return

    row, col, index = 0, 0, 1
    while (index < num):
        # TODO - Finish this method
        # Update row and col appropriately
        if (col < row):
            col += 1
        elif (col == row):
            col = 0
            row += 1
        index += 1

    # logging.info(f"Converted num: {num} to grid: ({row}, {col})")
    return (row, col)

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
        # logging.info(f"char_list for char {character}: {char_list}")
        return char_list
    else:
        # logging.info(f"row_list for char {character}: {row_list}")
        # logging.info(f"col_list for char {character}: {col_list}")
        return (row_list, col_list)

'''Return a list of possible_moves according to a given board'''
def possible_moves(board):
    # NOTE - Idea, make possible moves a dictionary with encodings and row, col values
    # NOTE - Lots of parallelism seems inefficient
    # TODO - Clear this up, num to grid converter will solve short term problems
    possible_moves = []

    # Step 1: Get a list of where all tacks and empty spots are
    # tack_list = char_locations(board, character=tack, grid=True)
    tack_rows, tack_cols = char_locations(board, character=tack, grid=False)
    # empty_list = char_locations(board, character=empty, grid=True)
    empty_rows, empty_cols = char_locations(board, character=empty, grid=False)
    
    # Step 2a: Take each tack and "try" to move in all directions (using global rules above)
    for i in range(len(tack_rows)):
        my_row, my_col = tack_rows[i], tack_cols[i]
        for k in range(len(all_row_delta)):
            row_delta, col_delta = all_row_delta[k], all_col_delta[k]
            mid_row, mid_col = my_row + 1 * row_delta, my_col + 1 * col_delta
            end_row, end_col = my_row + 2 * row_delta, my_col + 2 * col_delta
            # Step 2b: Verify that neighbor is tack and, jump is empty
            if (mid_row in tack_rows and mid_col in tack_cols):
                if (end_row in empty_rows and end_col in empty_cols):
                    # Append possible moves appropriately
                    start_pos = grid_to_num(my_row, my_col)
                    mid_pos = grid_to_num(mid_row, mid_col)
                    end_pos = grid_to_num(end_row, end_col)

                    # NOTE - Verify logic here, sometimes final position is 'None'
                    if (not (end_pos == None)):
                        encoded_move = move_encoder(start_pos, mid_pos, end_pos)
                        possible_moves.append(encoded_move)

    # Step 3: Return all possible moves
    # logging.info(f"possible_moves: {possible_moves}")
    return possible_moves

'''Update the board with the move'''
def make_move(board, encoded_move):
    # Step 1: Split encoded move into parts
    # logging.info(f"Splitting encoded move: {encoded_move}")
    start_str, mid_str, end_str = encoded_move.split(move_separator)
    start_num, mid_num, end_num = int(start_str), int(mid_str), int(end_str)
    start_row, start_col = num_to_grid(start_num)
    mid_row, mid_col = num_to_grid(mid_num)
    end_row, end_col = num_to_grid(end_num)
    # Step 2: Update the board accordingly
    board[start_row] = reassign_space(original=board[start_row], index=start_col, target=empty)
    board[mid_row] = reassign_space(original=board[mid_row], index=mid_col, target=empty)
    board[end_row] = reassign_space(original=board[end_row], index=end_col, target=tack)
    
    # Step 3: Return the updated board
    # log_and_print("We made it, time to return board, I hate indentation errors")
    return board

'''Recursively play the game - continue playing until you win (once)'''
# @tail_call_optimized
def recursive_play(board, moves_list, move_history):
    # Check how many moves are left
    tacks_left = len(char_locations(board, character=tack, grid=True))
    if (tacks_left == 1):
        # TODO - Remove final move separator
        return move_history
    elif (len(moves_list) == 0):
        # NOTE - No more moves, you lose
        pass
    elif (len(moves_list) > 0):
        # Scan through all moves and make them recursively
        for move in moves_list:
            # logging.info(f"move: {move}")
            next_board = make_move(board, move)
            next_moves = possible_moves(next_board)
            next_history = move_history + round_separator + move
            recursive_play(board=next_board, moves_list=next_moves, move_history=next_history)

def run():
    board = create_board(rows=5)
    row, col = num_to_grid(1)
    board = remove_tacks(board, all_rows=[row], all_cols=[col])
    # Must get around recursion error depth, try to raise limit
    new_depth = 2000
    cur_depth = sys.getrecursionlimit()
    sys.setrecursionlimit(new_depth)
    logging.debug(f"recursion limit raised from {cur_depth} to {new_depth}")
    winning_moves = recursive_play(board, possible_moves(board), "")
    log_and_print(f"FIRST WINNING SEQUENCE: \n{winning_moves}")

# Testing functions as they are written
run()