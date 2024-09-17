# CLI Sudoku

## Description

<a href="https://en.wikipedia.org/wiki/Sudoku" target="_blank">What is a Sudoku?</a>

### What can a user do with this program? 

__*Play a Sudoku puzzle utilizing their Command Line.*__ <a href="https://youtu.be/v676LL1DYCQ" target="_blank">Demonstration video</a>

#### Optional difficulties

- Easy 
- Medium 
- Hard 

#### Optional actions

- Inserting a value to a specified cell - *in case it was not defined at the beginning of the game, regardless of whether it has a value at the moment*
- Deleting a value from a specified cell - *in case it was not defined at the beginning of the game*
- Undoing a defined number of steps - *up to the number of insertions and deletions done since the last restart or the beginning of the game*
- Asking for a hint - *in case there are no repetitions, the program gives a suggestion to which cell what values could be written, prioritizing the cells where the number of possible values is the lowest*
- Restarting the game - *in which case the user will be prompted to confirm their choice in order to avoid accidental restarts*
- Asking the program to finish the game - *after printing the finished the Sudoku, the program finishes*

#### Non-optional functions

- The program highlights all values that collide with another value in their row, column or block with red colour.

## Implementation

### Technologies used
- sbt 1.9.8 (Oracle Corporation Java 21.0.1)
- Scala 3.3.1 (11.0.19, Java OpenJDK 64-Bit Server VM)

### Data structure choices

Numbers (and the lack of them) in the Sudoku are represented as `Option[Int]`s, called `Cell`s.
They are stored by `Rows` in the form of `Vector[Cell]`, as they need to be sequenced and random access should be low-cost as insertion a deletion at any index will be frequent.
Complete Sudokus consist of `Vector`s of `Row`s, as this will be printed later on with new lines and other graphics added to and between `Row`s and transforming these into one `String`.

The other logical units of Sudokus (`Column`s and `Block`s) are also represented as `Vector[Cell]`s, as they are basically needed for the same validations as `Row`s.
They can easily be retrieved from the rows with the use of mathematical heuristics  => column x is the xth index of each `Row`, while `Block` x consists of `Cell`s indexed with all the combinations of row indexes: ((x / 3 * 3) to (x / 3 * 3) + 2) and column indexes: ((n % 3 * 3) to (n % 3 * 3 + 2)).

In order to track changes in specified `Cell`s, and easily access them, type `Cellhistory = Vector[(Int, Int, Cell)]` is introduced. The first Int is the row index, while the second is the column index in the Tuples.

### File structure

The project consists of the following files:
- Sudoku - *Definition of types and methods*
- Validation - *Function definitions for checking whether a logical unit consists valid values, is repetition-free and is solved*
- Solving - *Function definitions that are needed for solving the Sudoku*
- Generate - *Function definitions that are needed for generating Sudokus with a defined difficulty level*
- Pretty - *Function definitions needed for printing the current state of the Sudoku to the console*
- PlaySudoku - *The core logic of the game, function definitions that parse and execute the user commands*
- Examples - *Sudokus that are used for testing the accuracy of functions*
- ValidationTests - *Unit tests for validation and solving methods*
- PlaySudokuTests - *Unit tests for the methods defined in PlaySudoku*
- main - *For running the game*
- Misc - *Prototypes or other methods that were not used in the final game but would not want to discard*
- README and intro and pictures - *Markdown files required for the SC50 Final Project Submission and Graphics that are inserted into this file*


### Solving logic

The core logic of solving a Sudoku is basically a backtracking algorithm which is going through all possible Sudokus that can be generated from the initial Sudoku using a List to store them until a solved Sudoku is found or there are no other Sudokus to consider.

It's runtime is exponential, but there are several built in steps, that aim to fasten the running-time of it:
- The Sudokus that are in a more advanced state - which consequently take less steps to identify as unsolvable or to solve - are placed at the beginning of the list, and will consequently be used earlier.
- The function identifies the `Cell`s for which there are the least amount of possible values that could be filled in without causing repetitions and fills those first. In case a single-choice empty `Cell` is filled, it can either generate more single-choice `Cell`s, for this reason the below widely-used function is utilized to repeat this step before any other steps are considered, fastening the elimination or solving of more advanced Sudokus. *Noting, that because of this, repetitions can be created, hence checking for these needed to be incorporated to the algorithm.*
```` Scala
  def fix[T](f: T => T)(x: T): T = {
    val res = f(x)
    if (x != res) fix(f)(res)
    else x
  }
````
- It is also checked whether there is at least one empty `Cell` that can not be filled with any value without causing repetitions, in which case the Sudoku is unsolvable and will be discarded.
- Checking whether a Sudoku is solved is only done when there are no empty `Cell`s in the Sudoku.

### Generating logic

Generating Sudokus is implemented with the use of finishSudoku (above). In order to retrieve different Sudokus when playing, and helping the testing of several functions, the project is utilizing System.nonaTime as new Random, and the following Vector's Tuples are used as coordinates where to generate random numbers between 1 and 9 - as these can not cause unsolvable Sudokus, but can be generating 9^6 different Sudokus: 
`Vector((0,0), (2,6), (3,1), (4,2), (7,5), (8,8))`

### Visual representation

| The Sudokus are visually represented as seen below: | In case there are repetitions in the `Row`s, `Column`s or `Block`s, all numbers are highlighted with RED ANSI colour when printing the Sudoku to the console. It looks like this when being printed: |
|-----------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![img_9.png](img_9.png)                             | ![img_11.png](img_11.png)                                                                                                                                                                            |

### Possible features to be added in the future
- Players could save and load their own games.
- Further Unit tests could be added to check the statistics of generating Sudokus, and with the help of these the function could be enhanced more easily.
