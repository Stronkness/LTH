-- André Frisk (an8218fr-s) and Axel Tobieson Rova (ax2070to-s)

-- How to use the program:
-- Write main in the terminal and have the text file with Sudoku strings ready in your directory.
-- Please change the name to the file in the main function where the marker is.
-- When main is typed in you will se the current Sudoku Board out of 50 in the terminal and
-- four choices where you must type in 1, 2, 3 or 4..
-- 1. Which will solve the current Sudoku and print it out for you and go back to the four
--    choices, one Sudoku Board is gone now because it has been used.
-- 2. Solves all the Sudoku Boards that is left, prints out every solution in the terminal.
--    Please note that you cant interrupt the second option if you activate it. Once all Sudoku
--    Boards are used, the program will shut down. NOTE THAT THIS CAN TAKE VERY LONG TIME!
-- 3. The user will assign a value to a square and will get feedback if this can be done or not.
--    The program will then take you back to the four options and you can choose to keep on trying
--    to solve the current board or just solve it or every board left.
-- 4. This option exits the program.
--
-- Once all Sudoku Boards have been solved/used the program will shut down.


module SolveSudoku where

main = do
  fileContent <- readFile "easy50.txt" -- *
  let filtered = filter (`notElem` "\n=") fileContent
  let all = splitter 81 filtered
  choicer all

choicer :: [String] -> IO ()
choicer [] = putStrLn "No more Sudokus left to solve. Exits the program."
choicer (current:sudokus) = do
  putStrLn "\n---Current Board---"
  prettyPrint(current)
  putStrLn "-------------------"
  putStrLn "1. Solve the current Sudoku\n2. Solve all Sudokus from the file, starting with the current one\n3. Assign a value to a square in the current Sudoku Board\n4. Exit"
  choice <- getLine
  case choice of "1" -> choice1 (current:sudokus)
                 "2" -> choice2 (current:sudokus)
                 "3" -> choice3 (current:sudokus)
                 "4" -> putStrLn "\nExits the program, have a pleasant day!"
                 otherwise -> choicer (current:sudokus)

-- Solve the current Sudoku, return remaining to choicer
choice1 :: [String] -> IO()
choice1 (x:xs) = do
    let solved = fromMaybe' [] (solveSudoku x)
    let vals = [snd c| c <- solved]
    putStrLn "----Solved Board---"
    prettyPrint(foldr ((++).show) "" (concat vals))
    choicer xs

-- Solve all Sudokus from the current one, return to choicer
choice2 :: [String] -> IO()
choice2 [] = choicer []
choice2 (x:xs) = do
    putStrLn "-------------------"
    let solved = solveSudoku x
    let vals = [snd c| c <- (fromMaybe' [] solved)]
    prettyPrint(foldr ((++).show) "" (concat vals))
    choice2 xs

-- The user assigns a value, return to choicer
choice3 :: [String] -> IO()
choice3 (x:xs) = do
  putStrLn "Please specify a square with a letter from A-I and 1-9, ex. A2"
  square <- getLine
  putStrLn "Please specify a number to be assigned to the square from 1-9"
  nbr <- getLine
  let nbr_int = (read :: String -> Int) nbr
  let new_board = fromMaybe' [] (assign nbr_int square (fromMaybe' [] (parseBoard x)))
  let fixed_board = removeListSudoku new_board
  let vals = [snd c| c <- fixed_board]
  let final_result = foldr ((++).show) "" (vals)
  if(verifySudoku final_result == True) then
    choicer (final_result:xs)
  else
    putStrLn "\n"
  putStrLn "Your input square gave an illegal Sudoku Board, please try again!"
  choicer (x:xs)

-- Helper function to go from a Board to SudokuBoard so we can pretty print it and go further beyond the solution
removeListSudoku :: Board -> SudokuBoard
removeListSudoku board = map (\x -> if length(snd x) > 1 then (fst x, 0) else (fst x, head(snd x))) board

-- Takes a board string input, inserts this into the function that changes dots to zeros and then convert Char to Int using digitToInt,
-- map is used to do this on every individual element: map f [a,b,c] == [f a, f b, f c]
parseBoardOriginal :: String -> SudokuBoard
parseBoardOriginal result = zip squares (map digitToInt(replacePointsWithZero result))

-- Replaces every dot in the input to 0, only compatible with String as type specification didn't work for us, what can we do?
replacePointsWithZero :: String -> String
replacePointsWithZero xs = [if x=='.' then '0' else x | x<-xs]

-- Prints out the current board as a 4x4 or 9x9 Sudoku board
-- Uses a helper function to split the string into 9 pieces
prettyPrint :: String -> IO ()
prettyPrint board =
   -- 9x9 Sudoku
     if(((length board) `mod` 9) == 0) then do
      mapM_ (print.digitsToInt) (splitter 9 board)
      --mapM_ (print.validPrint) (splitter 9 board)
     else do
      putStrLn "Invalid Sudoku board! Insert a board with a 9x9 Sudoku!"

-- Another version of digitToInt but takes a String
digitsToInt :: String -> [Int]
digitsToInt [] = []
digitsToInt (x:xs)
  | x >= 'a' && x <= 'f' = fromEnum x - fromEnum 'a' + 10 : digitsToInt xs
  | x >= 'A' && x <= 'F' = fromEnum x - fromEnum 'A' + 10 : digitsToInt xs
  | otherwise = fromEnum x - fromEnum '0' : digitsToInt xs

-- Helper function for prettyPrint which splits the Sudoku board into
-- 4 or 9 lists and insert them in a list of lists, which will then be
-- printed out by the prettyPrint function. The int is the Sudoku size.
-- Is also used in main to split a long Sudoku string into different games.
splitter :: Int -> String -> [String]
splitter _ [] = []
splitter n board = (take n board) : (splitter n (drop n board))

-- Creates a type which should contain the position of the square for the sudoku and the value of it
type SudokuBoard = [(String, Int)]

-- Gives the peers of the specified input using the fromMaybe function
getPeers :: String -> [String]
getPeers x = fromMaybe' [] (lookup x peers)

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
verifySudoku x = validBoard(parseBoardOriginal x) && validUnits(x)

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
validUnit x y = and [elem z (concat(lookups x y)) | z <- map digitToInt (cols)]

-- Checks if the variable unitList is valid for a certain Sudoku board
validUnits :: String -> Bool
validUnits x = and [validUnit z (validBoardNumbers(parseBoardOriginal x)) | z <- unitList]

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
eliminateValue number square board = mapIf (map2 (id, deleteInt number)) (\tuples -> fst tuples == square) board

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

isDigit :: Char -> Bool
isDigit char = elem char "0123456789"

union :: [a] -> [a] -> [a]
union l1 l2 = concat [l1,l2]

--Own version of digitToInt using prelude and hexadecimal numbers (ASCII also)
digitToInt :: Char -> Int
digitToInt x
  | x >= 'a' && x <= 'f' = fromEnum x - fromEnum 'a' + 10
  | x >= 'A' && x <= 'F' = fromEnum x - fromEnum 'A' + 10
  | otherwise = fromEnum x - fromEnum '0'

-- Own version of delete Int (from List) using prelude
deleteInt :: Int -> [Int] -> [Int]
deleteInt _ [] = []
deleteInt s (x:xs)
  | s == x = deleteInt s xs
  | otherwise = x : deleteInt s xs

-- Own version of delete String (from List) using prelude
delete :: String -> [String] -> [String]
delete _ [] = []
delete s (x:xs)
  | s == x = delete s xs
  | otherwise = x : delete s xs