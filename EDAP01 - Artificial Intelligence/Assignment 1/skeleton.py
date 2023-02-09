import gym
import random
import requests
import numpy as np
import argparse
import sys
from gym_connect_four import ConnectFourEnv
import time
import copy

env: ConnectFourEnv = gym.make("ConnectFour-v0")

#SERVER_ADRESS = "http://localhost:8000/"
SERVER_ADRESS = "https://vilde.cs.lth.se/edap01-4inarow/"
API_KEY = 'nyckel'
STIL_ID = ["an8218fr-s"]

def call_server(move):
   res = requests.post(SERVER_ADRESS + "move",
                       data={
                           "stil_id": STIL_ID,
                           "move": move, # -1 signals the system to start a new game. any running game is counted as a loss
                           "api_key": API_KEY,
                       })
   # For safety some respose checking is done here
   if res.status_code != 200:
      print("Server gave a bad response, error code={}".format(res.status_code))
      exit()
   if not res.json()['status']:
      print("Server returned a bad status. Return message: ")
      print(res.json()['msg'])
      exit()
   return res

def check_stats():
   res = requests.post(SERVER_ADRESS + "stats",
                       data={
                           "stil_id": STIL_ID,
                           "api_key": API_KEY,
                       })

   stats = res.json()
   return stats

"""
You can make your code work against this simple random agent
before playing against the server.
It returns a move 0-6 or -1 if it could not make a move.
To check your code for better performance, change this code to
use your own algorithm for selecting actions too
"""
def opponents_move(env):
   env.change_player() # change to oppoent
   avmoves = env.available_moves()
   if not avmoves:
      env.change_player() # change back to student before returning
      return -1

   # TODO: Optional? change this to select actions with your policy too
   # that way you get way more interesting games, and you can see if starting
   # is enough to guarrantee a win
   action = random.choice(list(avmoves))

   state, reward, done, _ = env.step(action)
   if done:
      if reward == 1: # reward is always in current players view
         reward = -1
   env.change_player() # change back to student before returning
   return state, reward, done

def evaluation(board, player):
   """
   So this evaluation function basiclly calculates the score of the current move if depth = 0.
   This returns the score depending on the player and the higher the score for the student the
   better the output will be and closer to victory.
   It checks every four pieces of the the horizontal lines, the vertical lines
   and the diagonals (like an X). It calculates the maximal score of this move and compares this in
   the alpha_beta function to achieve the best move. The center variable is determining the score for
   how important it is to have something in the middle (which increases your chance of winning). Why it
   is the number 3 is that I thought that was a good number to multiply the score to get some
   foundation in the score variable but also not to make it dominant.
   
   The score_window function counts the occurrences of the student and opponent pieces in the 
   window and calculates a score for this window.
   """
   score = 0
   center = []
   cols = []
   rows = []
   window = []
   window_rev = []

   # Center
   for element in list(board[:, 3]):
      center.append(int(element))
   center_score = center.count(player)
   score += center_score * 3

   # Vertical
   for col in range(7):
      cols = []
      for element in list(board[:,col]):
         cols.append(int(element))
      for row in range(3):
         window = cols[row:row+4]
         score += score_window(window, player)
   
   # Horizontal
   for row in range(6):
      rows = []
      for element in list(board[row,:]):
         rows.append(int(element))
      for col in range(4):
         window = rows[col:col+4]
         score += score_window(window, player)

   # Positive diagonal and reverse diagonal
   for row in range(3):
      for col in range(4):
         window = []
         for i in range(4):
            window.append(board[row+i][col+i])
            window_rev.append(board[row+3-i][col+i])
         score += score_window(window, player)
         score += score_window(window_rev, player)
         
   return score

def score_window(window, player):
   """
   Played around with scores to get better result, took some time with alot of testing to
   find something that would get 20 win streak
   """
   score = 0
   if player == 1:
      opponent = -1
   else:
      opponent = 1

   if window.count(player) == 4: # Win incoming
      score += 100000
   elif window.count(player) == 3 and window.count(0) == 1: # Now we're talking
      score += 25
   elif window.count(player) == 2 and window.count(0) == 2: # Could be better
      score += 4
      
   if window.count(opponent) == 3 and window.count(0) == 1: # Lowers score if opponent is close
      score -= 4
      
   return score

def alpha_beta(env, depth, alpha, beta, isMaximizingPlayer, time_1): # With minimax functionality
   curr_time = time.time() # Used as security measure with marginal to return an answer if time is exceeded of current move with marginal to do the evaluation call
   if depth == 0 or env.is_win_state() or curr_time-time_1 > 4.6:
      if isMaximizingPlayer:
         player = 1
      else:
         player = -1
      return evaluation(env.board, player) # No more move/depths, decide the correct move to do
   
   """
   Sorts the available moves to start from the middle and go idx+i and idx-i basiclly.
   This is because it is better to start from the middle and go to adjacent positions
   as if you have pieces in the middle of the board it gives better probability to win
   
   Real ugly code
   """
   moves = list(env.available_moves())
   middle = moves[len(moves) // 2]
   idx = moves.index(middle)
   temp = []
   temp.append(middle)
   temp_2 = moves[0:idx]
   temp_3 = moves[idx:len(moves)]
   temp_2 = sorted(temp_2, reverse=True)
   for i in range(max(len(temp_2), len(temp_3))):
      if i < len(temp_2) and temp_2[i] not in temp:
         temp.append(temp_2[i])
      if i < len(temp_3) and temp_3[i] not in temp:
         temp.append(temp_3[i])
   moves = temp
      
   make_move = None
   if isMaximizingPlayer: # Student
      value = -np.inf
      for move in moves:
         # We want to copy the current env of iteration to gain the insights, this is done by deepcopy, also resets to original env.board to decide best move
         state = copy.deepcopy(env)
         state.step(move) # Make step of move in the copied env
         value_alg = max(value, alpha_beta(state, depth-1, alpha, beta, False, time_1))
         if value_alg > value:
            value = value_alg
            make_move = move
         alpha = max(alpha, value)
         if alpha >= beta:
            break
      return make_move
   else: # Opponent
      value = np.inf
      for move in moves:
         state = copy.deepcopy(env)
         state.step(move)
         value_alg = min(value, alpha_beta(state, depth-1, alpha, beta, True, time_1))
         if value_alg < value:
            value = value_alg
            make_move = move
         beta = min(beta, value)
         if alpha >= beta:
            break
      return make_move

def play_game(vs_server = False):
   """
   The reward for a game is as follows. You get a
   botaction = random.choice(list(avmoves)) reward from the
   server after each move, but it is 0 while the game is running
   loss = -1
   win = +1
   draw = +0.5
   error = -10 (you get this if you try to play in a full column)
   Currently the player always makes the first move
   """
   wins = 0
   ties = 0
   loss = 0
   for _ in range(20):

      # default state
      state = np.zeros((6, 7), dtype=int)

      # setup new game
      if vs_server:
         # Start a new game
         res = call_server(-1) # -1 signals the system to start a new game. any running game is counted as a loss

         # This should tell you if you or the bot starts
         print(res.json()['msg'])
         botmove = res.json()['botmove']
         state = np.array(res.json()['state'])
         # reset env to state from the server (if you want to use it to keep track)
         env.reset(board=state)
      else:
         # reset game to starting state
         env.reset(board=None)
         # determine first player
         student_gets_move = random.choice([True, False])
         if student_gets_move:
            print('You start!')
            print()
         else:
            print('Bot starts!')
            print()

      # Print current gamestate
      print("Current state (1 are student discs, -1 are servers, 0 is empty): ")
      print(state)
      print()

      done = False
      while not done:
         # Select your move
         time_1 = time.time()
         stmove = alpha_beta(env, 7, -np.inf, np.inf, True, time_1)
         time_2 = time.time()

         # move(env)

         # make both student and bot/server moves
         if vs_server:
            # Send your move to server and get response
            res = call_server(stmove)
            print(res.json()['msg'])

            # Extract response values
            result = res.json()['result']
            botmove = res.json()['botmove']
            state = np.array(res.json()['state'])
            # reset env to state from the server (if you want to use it to keep track)
            env.reset(board=state)
         else:
            if student_gets_move:
               # Execute your move
               avmoves = env.available_moves()
               if stmove not in avmoves:
                  print("You tied to make an illegal move! You have lost the game.")
                  break
               state, result, done, _ = env.step(stmove)

            student_gets_move = True # student only skips move first turn if bot starts

            # print or render state here if you like

            # select and make a move for the opponent, returned reward from students view
            if not done:
               state, result, done = opponents_move(env)

         # Check if the game is over
         if result != 0:
            done = True
            if not vs_server:
               print("Game over. ", end="")
            if result == 1:
               print("You won!")
               wins += 1
            elif result == 0.5:
               print("It's a draw!")
               ties += 1
            elif result == -1:
               print("You lost!")
               loss += 1
            elif result == -10:
               print("You made an illegal move and have lost!")
               loss += 1
            else:
               print("Unexpected result result={}".format(result))
            if not vs_server:
               print("Final state (1 are student discs, -1 are servers, 0 is empty): ")
         else:
            print("Current state (1 are student discs, -1 are servers, 0 is empty): ")

         # Print current gamestate
         print(state)
         print("Move duration: ", time_2 - time_1)
         print()
      
      print("Game ended. Current score: ", wins, ties, loss)
      
   print(wins, ties, loss)

def main():
   # Parse command line arguments
   parser = argparse.ArgumentParser()
   group = parser.add_mutually_exclusive_group()
   group.add_argument("-l", "--local", help = "Play locally", action="store_true")
   group.add_argument("-o", "--online", help = "Play online vs server", action="store_true")
   parser.add_argument("-s", "--stats", help = "Show your current online stats", action="store_true")
   args = parser.parse_args()

   # Print usage info if no arguments are given
   if len(sys.argv)==1:
      parser.print_help(sys.stderr)
      sys.exit(1)

   if args.local:
      play_game(vs_server = False)
   elif args.online:
      play_game(vs_server = True)

   if args.stats:
      stats = check_stats()
      print(stats)

   # TODO: Run program with "--online" when you are ready to play against the server
   # the results of your games there will be logged
   # you can check your stats bu running the program with "--stats"

if __name__ == "__main__":
    main()
