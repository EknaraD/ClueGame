CSCI 306 Clue Board Layout

Team members:
Aaron Nguyễn
Eknara Dassanayake

Files:
ClueSetup.txt - room legend (9 rooms, walkway, unused)
ClueLayout.xlsx - color coded board with row/column numbers
ClueLayout.csv - board export without row/column numbers

Board size: 24 rows x 26 columns

FIX:
testTargetsOccupied() was wrong with its output because if we start at (0,0) and there is an occupied cell at (0,1) t
there are still 2 possible target cells with path length of 2 
(0,0) -> (1,0) -> (2,0)
           |
           --> (1,1) 
            