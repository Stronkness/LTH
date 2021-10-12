-- André Frisk (an8218fr-s) and Axel Tobieson Rova (ax2070to-s)

module Sudoku where
import Data.Char
import Data.List

-- Rows and Cols for 4x4 Board
rows4x4 = "ABCD"
cols4x4 = "1234"

-- Rows and Cols for 9x9 Board
rows9x9 = "ABCDEFGHI"
cols9x9 = "123456789"

containsElem :: Eq a => a -> [a] -> Bool
containsElem _ [] = False
containsElem elem (x:xs)
  | elem == x = True
  | otherwise = containsElem elem xs

-- Returns a list of lists where two lists are as input, uses list comprehension. Writes out every pair [x,y]
-- where x is the start and goes through every element of y, then x+1
cross :: [a] -> [a] -> [[a]]
cross xs ys = [ [x,y] | x<-xs, y<-ys]

-- Replaces every dot in the input to 0, only compatible with String as type specification didn't work for us, what can we do?
replacePointsWithZero :: String -> String
replacePointsWithZero xs = [if(x=='.') then '0' else x | x<-xs]

-- Saves all the possible squares for the Sudoku, both 4x4 and 9x9
squares4x4 :: [String]
squares4x4 = cross rows4x4 cols4x4
squares9x9 :: [String]
squares9x9 = cross rows9x9 cols9x9

-- Creates a type which should contain the position of the square for the sudoku and the value of it
type SudokuBoard = [(String, Int)]

-- Takes a board string input, inserts this into the function that changes dots to zeros and then convert Char to Int using digitToInt,
-- map is used to do this on every individual element: map f [a,b,c] == [f a, f b, f c]
parseBoard :: String -> SudokuBoard
parseBoard result = zip squares4x4 (map digitToInt(replacePointsWithZero result))

-- Prints out all possible units. First the rows, then the columns and at last the squares (note: this solution is for a 4x4 Sudoku)
unitList :: [[String]]
unitList = [cross [r] cols4x4 | r<-rows4x4] ++ [cross rows4x4 [c] | c<-cols4x4] ++ [cross boxR boxC | boxR <- ["AB","CD"], boxC <- ["12","34"]]

-- Filters and print out the cols, rows and boxes which includes the input String
filterUnitList :: String -> [[String]]
filterUnitList x = filter1 (f x) unitList

-- Help function for the first condition in filter (a -> Bool)
f :: String -> [String] -> Bool
f x xs = containsElem x xs

-- Own implementation of the filter function
filter1 :: (a -> Bool) -> [a] -> [a]
filter1 _ [] = []
filter1 p (x:xs) =
  if p x
    then x : filter1 p xs
    else filter1 p xs

-- Prints out every unit for a square string using filterUnitList, this is applied to every square string on the Sudoku board
units :: [(String, [[String]])]
units = [(x, filterUnitList x) | x<-squares4x4]

-- Takes a list of lists and concatenates into a single list, this using the concat function
foldList :: [[a]] -> [a]
foldList [] = []
foldList (x:xs) = x ++ foldList xs

-- Takes a list as input and deletes every duplicate in the list using recursion, removes from the start of the list, ex.
-- ["A1","B2","B3","A1","B2"] deletes the first two as they are duplicates of the one later in the list, so the output
-- is ["B3","A1","B2"]
removeDuplicates :: Eq a => [a] -> [a]
removeDuplicates [] = []
removeDuplicates (x:xs)
  | containsElem x xs = removeDuplicates xs
  | otherwise = x : removeDuplicates xs

-- Prints out every square string with a list of units which the square string are involved with (cols, rows and boxes).
-- Also deletes the current square string from the list as removesDuplicates does always keep one element of every element.
-- However the output of the list is correct but not in the correct order, we don't know really why.
peers :: [(String, [String])]
peers = [(x, delete x (removeDuplicates(foldList(filterUnitList x)))) | x<-squares4x4]











