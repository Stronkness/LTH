-- André Frisk (an8218fr-s) and Axel Tobieson Rova (ax2070to-s)

module Sudoku where

import System.Random (randomRIO)

-- Rows and Cols for maximum 9x9 Sudoku at the moment
rows = "ABCDEFGHI"
cols = "123456789"

-- A generic function which sets the size of the Sudoku board.
-- 4 for 4x4 or 9 for 9x9
sudokuSize :: Int
sudokuSize = 9

-- Reads a txt file and checks every Sudoku if its good or bad,
-- prints out a list of OKs or Invalids which indicates which Sudoku that is valid or not
main = do
  fileContent <- readFile "easy50.txt"
  let filtered = filter (`notElem` "\n=") fileContent
  let all = splitter (sudokuSize*sudokuSize) filtered
  print ([if verifySudoku z then "OK" else "Invalid" | z <- all])

-- LAB 1

-- Returns a list of lists where two lists are as input, uses list comprehension. Writes out every pair [x,y]
-- where x is the start and goes through every element of y, then x+1
cross :: [a] -> [a] -> [[a]]
cross xs ys = [ [x,y] | x<-xs, y<-ys]

-- Replaces every dot in the input to 0, only compatible with String as type specification didn't work for us, what can we do?
replacePointsWithZero :: String -> String
replacePointsWithZero xs = [if x=='.' then '0' else x | x<-xs]

-- Saves all the possible squares for the Sudoku, both 4x4 and 9x9
squares :: [String]
squares = cross (take sudokuSize rows) (take sudokuSize cols)

-- Creates a type which should contain the position of the square for the sudoku and the value of it
type SudokuBoard = [(String, Int)]

-- Takes a board string input, inserts this into the function that changes dots to zeros and then convert Char to Int using digitToInt,
-- map is used to do this on every individual element: map f [a,b,c] == [f a, f b, f c]
parseBoard :: String -> SudokuBoard
parseBoard result = zip squares (map digitToInt(replacePointsWithZero result))

-- Prints out all possible units. First the rows, then the columns and at last the squares (note: this solution is for a 4x4 Sudoku)
unitList :: [[String]]
unitList = [cross [r] (take sudokuSize cols) | r<- (take sudokuSize rows)] ++
            [cross (take sudokuSize rows) [c] | c<- (take sudokuSize cols)] ++
             [cross boxR boxC | boxR <- ["ABC","DEF","GHI"], boxC <- ["123","456","789"]]

-- Filters and print out the cols, rows and boxes which includes the input String
filterUnitList :: String -> [[String]]
filterUnitList x = filter1 (f x) unitList

-- Help function for the first condition in filter (a -> Bool)
f :: String -> [String] -> Bool
f x xs = elem x xs

-- Own implementation of the filter function
filter1 :: (a -> Bool) -> [a] -> [a]
filter1 _ [] = []
filter1 p (x:xs) =
  if p x
    then x : filter1 p xs
    else filter1 p xs

-- Prints out every unit for a square string using filterUnitList, this is applied to every square string on the Sudoku board
units :: [(String, [[String]])]
units = [(x, filterUnitList x) | x<-squares]

-- Takes a list of lists and concatenates into a single list, this using the concat function
foldList :: [[a]] -> [a]
foldList [] = []
foldList xs = foldr (++) [] xs

-- Takes a list as input and deletes every duplicate in the list using recursion, removes from the start of the list, ex.
-- ["A1","B2","B3","A1","B2"] deletes the first two as they are duplicates of the one later in the list, so the output
-- is ["B3","A1","B2"]
removeDuplicates :: Eq a => [a] -> [a]
removeDuplicates [] = []
removeDuplicates (x:xs)
  | elem x xs = removeDuplicates xs
  | otherwise = x : removeDuplicates xs

-- Prints out every square string with a list of units which the square string are involved with (cols, rows and boxes).
-- Also deletes the current square string from the list as removesDuplicates does always keep one element of every element.
-- However the output of the list is correct but not in the correct order, we don't know really why.
peers :: [(String, [String])]
peers = [(x, delete x (removeDuplicates(foldList(filterUnitList x)))) | x<-squares]

-- LAB 2

-- Own version of digitToInt using prelude and hexadecimal numbers (ASCII also)
digitToInt :: Char -> Int
digitToInt x
  | x >= 'a' && x <= 'f' = fromEnum x - fromEnum 'a' + 10
  | x >= 'A' && x <= 'F' = fromEnum x - fromEnum 'A' + 10
  | otherwise = fromEnum x - fromEnum '0'

-- Another version of digitToInt but takes a String
digitsToInt :: String -> [Int]
digitsToInt [] = []
digitsToInt (x:xs)
  | x >= 'a' && x <= 'f' = fromEnum x - fromEnum 'a' + 10 : digitsToInt xs
  | x >= 'A' && x <= 'F' = fromEnum x - fromEnum 'A' + 10 : digitsToInt xs
  | otherwise = fromEnum x - fromEnum '0' : digitsToInt xs

-- Own version of delete (from List) using prelude
delete :: String -> [String] -> [String]
delete _ [] = []
delete s (x:xs)
  | s == x = delete s xs
  | otherwise = x : delete s xs

-- Takes a parameter and a Maybe parameter, if Maybe is a Just then return the value in Just, otherwise the first parameter
fromMaybe :: a -> Maybe a -> a
fromMaybe x Nothing = x
fromMaybe _ (Just y) = y

-- Gives the peers of the specified input using the fromMaybe function
getPeers :: String -> [String]
getPeers x = fromMaybe [] (lookup x peers)

-- Removes all Nothing in a Maybe list and print outs the element values of Just in a list
justifyList :: [Maybe a] -> [a]
justifyList [] = []
justifyList (Nothing:xs) = justifyList xs
justifyList (Just x : xs) = x : justifyList xs

-- Works like lookup but takes a list as input and searches inside a list of tuples if they exist,
-- if they do we print out the index b to a list which will become a [Maybe b] list because of map,
-- this list is inserted to justifyList to get rid of Just and Nothing. Flip is used in lookup
lookups :: Eq a => [a] -> [(a,b)] -> [b]
lookups [] _ = []
lookups _ [] = []
lookups x y = justifyList(map(flip lookup y) x)

-- Checks if a single square tuple is valid in a Sudoku Board
-- Returns true if the index is 0 (available) or if the square tuple is not in the peers
validSquare :: (String, Int) -> SudokuBoard -> Bool
validSquare (_, 0) _ = True
validSquare x y = notElem (snd x) (lookups(getPeers (fst x)) y)

-- Checks the entire Sudoku board (every square) if its valid
-- Uses validSquare for this, true if its valid, false if not
-- Checks every square and if even one is invalid the whole board is invalid
validBoard :: SudokuBoard -> Bool
validBoard [] = True
validBoard (x:xs)
 | True == validSquare x xs = validBoard xs
 | otherwise = False

-- Takes a input string of Sudoku numbers and checks if this board is valid
verifySudoku :: String -> Bool
verifySudoku x = validBoard(parseBoard x) && validUnits(x)

-- Takes two lists as input and removes occurrences of elements in the second list from the first list
reduceList :: Eq a => [a] -> [a] -> [a]
reduceList x y = [x | x <- x, notElem x y]

-- Checks if the square input is valid or not, return a tuple with a list of int of the values
-- that are valid for this inout, if there is no valid input it returns a empty list
validSquareNumbers :: (String, Int) -> SudokuBoard -> (String, [Int])
validSquareNumbers x y
  | snd x == 0 = (fst x, reduceList [1..9] (lookups(getPeers (fst x)) y))
  | validSquare x y = (fst x, [snd x])
  | otherwise = (fst x, [])

-- Checks the board with validSquareNumbers if it is a valid board or not using map,
-- prints out every possible unit a square can have
validBoardNumbers :: SudokuBoard -> [(String, [Int])]
validBoardNumbers board = map (\x -> validSquareNumbers x board) board

-- Checks if the unit is valid in the board, the first input is the units and the second
-- input comes from when the following call is being made: validBoardNumbers parseBoard "currentGame"
validUnit :: [String] -> [(String, [Int])] -> Bool
validUnit x y = and [elem z (concat(lookups x y)) | z <- map digitToInt (take sudokuSize cols)]

-- Checks if the variable unitList is valid for a certain Sudoku board
validUnits :: String -> Bool
validUnits x = and [validUnit z (validBoardNumbers(parseBoard x)) | z <- unitList]

-- Lab 3

-- A simple function used to give a value between the two range values given by IO,
-- uses read to convert the IO String to a IO Int ( String -> a  == IO String -> IO a)
simpleIO :: IO Int
simpleIO = do
  first <- getLine
  second <- getLine
  randomRIO ((read first),(read second))

-- Prints out the current board as a 4x4 or 9x9 Sudoku board
-- Uses a helper function to split the string into 9 pieces
prettyPrint :: String -> IO ()
prettyPrint board =
  -- 4x4 Sudoku
    if(((length board) `mod` 4) == 0) then do
      putStrLn "Current Sudoku board: \n------"
      mapM (print.digitsToInt) (splitter 4 board)
      putStrLn "------ \n \nFollowing squares marked with BAD is a illegal square for the Sudoku board:"
      mapM_ (print.validPrint) (splitter 9 board)
    else
    -- 9x9 Sudoku
      if(((length board) `mod` 9) == 0) then do
        putStrLn "Current Sudoku board: \n-------------------"
        mapM (print.digitsToInt) (splitter 9 board)
        putStrLn "------------------- \n \nFollowing squares that are marked with 1 is True and 0 is False for the Sudoku board:"
        mapM_ (print.validPrint) (splitter 9 board)
    else do
      putStrLn "Invalid Sudoku board!"

-- Helper function for prettyPrint which splits the Sudoku board into
-- 4 or 9 lists and insert them in a list of lists, which will then be
-- printed out by the prettyPrint function. The int is the Sudoku size.
-- Is also used in main to split a long Sudoku string into different games.
splitter :: Int -> String -> [String]
splitter _ [] = []
splitter n board = (take n board) : (splitter n (drop n board))

-- Helper function for prettyPrint which prints out the BAD squares using
-- toString and validUnitsPrinter
validPrint :: String -> [Int]
validPrint board = map toString (validUnitsPrinter board)

-- Changes True to 1 and False to 0 for easier printing out in the terminal
toString :: Bool -> Int
toString x = if x then 1 else 0

-- Checks every unit if its true or false and returns a list of the conditions
validUnitsPrinter :: String -> [Bool]
validUnitsPrinter x =  [if(snd z > 0) then (elem (snd z) (snd (validSquareNumbers z (parseBoard x)))) else True | z <- parseBoard x]

