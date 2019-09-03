'''
Created: 3 September 2019
Author: Max Smith
Goal: Port Java implementaiton of Cracker Barrel game over to python for AI
'''

'''Create the game with functions'''
class Game:
    def __init__(self, game_rows):
        self.game_rows = game_rows
        self.player = Player(self)
        # self.board = create_board(self, self.game_rows)
        # self.game_display = visualize_board(self, self.board)
        # NOTE - append score for each tack jumped
        # NOTE - reward for higher scores
        self.score = 0

    def create_board(self, game_rows):
        pass

    def visualize_board(self, board):
        pass


'''Create a player that navigates the game, essentially a board'''
class Player(object):
    pass

    
