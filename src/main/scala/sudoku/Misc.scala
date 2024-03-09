package junicamp
package sudoku

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.util.Random
import PlaySudoku.*
import Validation.*
import junicamp.sudoku.Pretty.pretty

def isColumnRepetitive(sudoku: Sudoku, index: Int): String = {
  if (areCellsRepetitionFree(getNthColumn(sudoku, index))) "not repetitive"
  else "repetitive"
}

def isBlockRepetitive(sudoku: Sudoku, index: Int): String = {
  if (areCellsRepetitionFree(getNthBlock(sudoku, index))) "not repetitive"
  else "repetitive"
}

def isRowRepetitive(sudoku: Sudoku, index: Int): String = {
  if (areCellsRepetitionFree(sudoku.rows(index))) "not repetitive"
  else "repetitive"
}


object Misc{
  def guessingGame(): Unit = {
    val randomNum = Random.nextInt(101)
    println("Guess the number between 0 and 100:")
    var guessedNum = readLine().toInt
    var i = 0
    while (guessedNum != randomNum) {
      if (guessedNum > randomNum) {
        println(s"The number is smaller than $guessedNum")
      } else {
        println(s"The number is bigger than $guessedNum")
      }
      println("Guess again: ")
      guessedNum = readLine().toInt
      i = i + 1
    }
    println(s"You found the number: $randomNum in $i guesses, congrats!")
  }

  def guessingGameV2(): Unit = {
    val randomNum = Random.nextInt(101)
    println("Write in your name:")
    val name = readLine()
    println(s" $name, guess the number between 0 and 100:")

    @tailrec
    def checkGuessedNum(curGuess: Int, min: Int, max: Int, numTries: Int): Unit = {
      if (curGuess == randomNum) {
        println(s"Congrats $name, you have guessed right in $numTries attempts!")
      } else if (curGuess > randomNum) {
        val newMax = List(curGuess - 1, max).min
        println(s"$curGuess is too high, guess again between $min and $newMax")
        checkGuessedNum(readLine().toInt, min, newMax, numTries + 1)
      } else {
        val newMin = List(min, curGuess + 1).max
        println(s"$curGuess is too low, guess again between $newMin and $max!")
        val newGuess = readLine().toInt
        checkGuessedNum(newGuess, newMin, max, numTries + 1)
      }
    }

    checkGuessedNum(readLine().toInt, 0, 100, 1)
  }

  @tailrec
  def readCurNum: Int = {
    val curNum = readLine().toInt
    if (1 <= curNum && curNum <= 9) {
      curNum
    } else {
      println(s"You have entered a wrong value: $curNum, please enter a number between 1 and 9! (u_u)")
      readCurNum
    }
  }

  def numToIndex(num: Int): Int = num - 1

  @tailrec
  def fillCell(sudoku: Sudoku, name: String): Unit = {
    println("Here is your Sudoku: ")
    println(pretty(sudoku))
    println("Which row do you want to write into? (1-9)")
    val rowIndex = numToIndex(readCurNum)
    println("Which column do you want to write into? (1-9)")
    val columnIndex = numToIndex(readCurNum)
    if (cellIsEmpty(sudoku: Sudoku, rowIndex: Int, columnIndex: Int)) {
      println(s"Which number do you want to write in to row ${rowIndex + 1}, column ${columnIndex + 1}? (1-9)")
      val value = readCurNum
      val curSudoku = sudoku.insert(rowIndex, columnIndex, value)
      val sudokuIsSolved = isSudokuSolved(curSudoku)
      val sudokuIsRepetitionFree = isSudokuRepetitionFree(curSudoku)
      if (sudokuIsSolved) {
        println(s"Congrats $name, you have solved the Sudoku! (*.*) Look at how beautiful it is:")
        println(pretty(curSudoku))
      } else if (!sudokuIsSolved && sudokuIsRepetitionFree) {
        fillCell(curSudoku, name)
      } else if (!sudokuIsRepetitionFree) {
        println(s"$value makes the sudoku repetitive. (x.X) Details: row ${rowIndex + 1} is ${isRowRepetitive(curSudoku, rowIndex)}, column ${columnIndex + 1} is ${isColumnRepetitive(curSudoku, columnIndex)}, block is ${isRowRepetitive(curSudoku, (rowIndex / 3 * 3) + (columnIndex / 3))},Please try writing something else in!")
        fillCell(sudoku, name)
      }
    } else if (!cellIsEmpty(sudoku: Sudoku, rowIndex: Int, columnIndex: Int)) {
      println(s"The cell you wanted to write into is not empty (>_<). Check row ${rowIndex + 1}, column ${columnIndex + 1}, and try again.")
      fillCell(sudoku, name)
    }

  }

}

