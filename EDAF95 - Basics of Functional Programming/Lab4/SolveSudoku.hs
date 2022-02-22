-- André Frisk (an8218fr-s) and Axel Tobieson Rova (ax2070to-s)

module SolveSudoku where

-- by Adrian Roth

import Data.Bool
import Data.Char
import Data.List
import Data.Maybe

cross :: [a] -> [a] -> [[a]]
cross s1 s2 = [[r, c] | r <- s1, c <- s2]

rowBoxes, colBoxes :: [String]
rowBoxes = ["ABC", "DEF", "GHI"]
colBoxes = ["123", "456", "789"]

rows, cols :: String
rows = concat rowBoxes
cols = concat colBoxes

squares :: [String]
squares = cross rows cols

unitList :: [[String]]
unitList = [cross rows [c] | c <- cols]
        ++ [cross [r] cols | r <- rows]
        ++ [cross rs cs | rs <- rowBoxes, cs <- colBoxes]

units :: [(String, [[String]])]
units = [(s, filter (elem s) unitList) | s <- squares]

peers :: [(String, [String])]
peers = map (\(s, u) -> (s, delete s (foldl union [] u))) units

type Board = [(String, [Int])]

allDigits :: [Int]
allDigits = [1, 2, 3, 4, 5, 6, 7, 8, 9]
infAllDigits = repeat allDigits
emptyBoard = zip squares infAllDigits

parseSquare :: (String, Char) -> Board -> Maybe Board
parseSquare (s, x) values
  | x == '.' || x == '0' = return values
  | isDigit x = assign (digitToInt x) s values
  | otherwise = fail "not a valid grid"

parseBoard :: String -> Maybe Board
parseBoard = foldr ((=<<) . parseSquare) (Just emptyBoard) . zip squares

-- Lab 4

-- Apply a function to both pairs
-- why (f,g), perhaps map f over a,b should work too
map2 :: (a -> c, b -> d) -> (a, b) -> (c, d)
map2 (f,g) (a,b) = (f a, g b)

-- Apply function if condition is true to list. I guess mapIf (func) (cond) list"
-- works: tried, mapIf (* 2) (even) [1,2,3,4,5,6]
mapIf :: (a -> a) -> (a -> Bool) -> [a] -> [a]
mapIf _ _ [] = []
mapIf (f) (k) x = [if k c then f c else c | c <- x]

-- Checks if the first succeeds, if not check the other one, if both succeeds choose the first one
maybeOr :: Maybe a -> Maybe a -> Maybe a
maybeOr x Nothing = x
maybeOr Nothing y = y
maybeOr x y = x

-- Returns the first Just element in a list, if there does not exist any Just elements, return Nothing
firstJust :: [Maybe a] -> Maybe a
firstJust [] = Nothing
firstJust (Nothing : xs) = firstJust xs
firstJust (Just x : xs) = Just x

-- Checks a list of tuples and returns the second parameter of the tuple which is a list of [b]
lookupList :: Eq a => a -> [(a, [b])] -> [b]
lookupList _ [] = []
lookupList s (x:xs)
  | s == (fst x) = (snd x)
  | otherwise = lookupList s xs

-- Own version of monadic bind function
maybeBind :: Maybe a -> (a -> Maybe b) -> Maybe b
maybeBind x f = case x of
  Nothing -> Nothing
  Just x  -> f x

-- Tries to replace the first argument with the second argument if the first argument exists in the list of [a]
tryReplace :: Eq a => a -> a -> [a] -> Maybe [a]
tryReplace _ _ [] = Nothing
tryReplace y y' (x:xs)
  | x == y = Just (y':xs)
  | otherwise = fmap (x:) $ tryReplace y y' xs

-- Takes a parameter and a Maybe parameter, if Maybe is a Just then return the value in Just, otherwise the first parameter
fromMaybe' :: a -> Maybe a -> a
fromMaybe' x Nothing = x
fromMaybe' _ (Just y) = y

-- Switches places with each other and puts it into a Maybe [a] list using recursion, maybeBind and tryReplace
recursiveReplacement :: Eq a => [a] -> [a] -> [a] -> Maybe [a]
recursiveReplacement _ _ [] = Nothing
recursiveReplacement [] _ list = Just list
recursiveReplacement _ [] list = Just list
recursiveReplacement (x:xs) (y:ys) list = (maybeBind (tryReplace x y list)) (recursiveReplacement xs ys)

-- Either sets or eliminates a value given to thr square in the Board, uses mapIf and map2 to execute this.
-- The function id is used here to prevent the first parameter of the tuple to do anything.
setValue, eliminateValue :: Int -> String -> Board -> Board
setValue number square board =  mapIf (map2 (id, filter (== number))) (\tuples -> fst tuples == square) board
eliminateValue number square board = mapIf (map2 (id, delete number)) (\tuples -> fst tuples == square) board

-- Tries to eliminate a number from the input String in the Board. If the condition isn't met, that the
-- elimination can be procedure then we call to eliminateValue to eliminate this number from the Board.
-- The first condition checks if it possible or not to do the elimination.
eliminate :: Int -> String -> Board -> Maybe Board
eliminate number square board
  | (lookupList square elimination) == [] = Nothing
  | otherwise = Just elimination
    where elimination = (eliminateValue number square board)

-- Assigns a number to a square and calls a helper function to eliminate the number from every peer of the square.
-- Returns a Maybe Board.
assign :: Int -> String -> Board -> Maybe Board
assign number square board = assign' (lookupList square peers) number (Just (setValue number square board))

-- Helper function for assign, which goes through every peer of the square input and eliminates the number
-- we want to assign to this square, so that the square only has that number among the peers
assign' :: [String] -> Int -> Maybe Board -> Maybe Board
assign' _ _ Nothing = Nothing
assign' [] _ board = board
assign' (peer:peers) number board = assign' peers number (board `maybeBind` eliminate number peer)

-- Helper function for solveSudoku
-- maybebind assign solvesudoku || solveSudoku' sqs (fromMaybe' [] (assign digit sq board) )
solveSudoku' :: [String] -> Board -> Maybe Board
solveSudoku' _ [] = Nothing
solveSudoku' [] board = Just board
solveSudoku' (sq:sqs) board = firstJust ([maybeBind (assign digit sq board) (solveSudoku' sqs) | digit <- allDigits])

-- Solves the Sudoku for the input String, uses squares for the first parameter to the helper function
-- because squares gives all the possible squares that can be used for the game (9x9 in this case)
solveSudoku :: String -> Maybe Board
solveSudoku boardString = maybeBind (parseBoard boardString) (solveSudoku' squares)