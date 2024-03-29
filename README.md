# Cracker-Barrel
Creating the cracker barrel game and tools to find optimal solution

## Goals
The main goal of this repository is beat the Cracker Barrel peg game. 

### Steps
This will be accomplished through gameplay backend construction and optimization, then using AI to learn the best strategy, then develop a GUI to make it pretty for the user.

1. Create the game
    1. Naive computer, inefficient, benchmark for future optimiztaion
2. Optimize game algorithms (big oh baby)
    1. Use benchmark and step by step optimizationa to gauge efficiency increases
3. Implement Q-Learning (or other alogirthms) to learn best strategy to beat game
    1. Clean up Python code (objects and classes)
4. Create a GUI to play the game for a user
    1. Make it pretty
    2. Integrate learned rules to aid as "hints"
    3. iOS App?
    
#### Recursive Solutions From Starting Positions
Starting Position              | Win Chance (%)  | Run Time (ms)
------------------------------ | ----------------| --------------
Corner point                   | 5.23%           | 3114 ms
One offset                     | 5.05%           | 1231 ms
Center of edge                 | 7.42%           | 5890 ms
Center of board                | 1.12%           |  689 ms
  
#### Optimization Log
Algorithm Description          | Optimization (%) | Run Time (sec)
------------------------------ | -----------------| --------------
Lookup Table for allowed moves | x%               | x seconds
Reuse sequence of moves        | x%               | x seconds

## Authors
* Max Smith (max.smith@duke.edu)
