package junicamp
package sudoku

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.util.Random
import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters.*
import sudoku.Sudoku.*
import sudoku.Examples.*
import Pretty.*
import Generate.*
import Validation.*

object PlaySudoku {

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

  def cellIsEmpty(sudoku: Sudoku, row: Int, column: Int): Boolean = {
    sudoku.rows(row)(column).isEmpty
  }

  def isColumnRepetitive(sudoku: Sudoku, index: Int): String = {
    if(areCellsRepetitionFree(getNthColumn(sudoku, index))) "not repetitive"
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

  @tailrec
  def generateSudoku(): Sudoku = {
    val level = readLine().toInt
    if (level == 1) {
      generateEasySudoku(generateNonRandomSolvedSudoku(0, 0))
    } else if (level == 2) {
      generateMediumSudoku(generateNonRandomSolvedSudoku(0, 0))
    } else if (level == 3) {
      generateHardSudoku(generateNonRandomSolvedSudoku(0, 0))
    } else {
      println(s"Invalid level requested: $level. Please write in the number of the level you would like to play! 1: Easy, 2: Medium, 3: Hard")
      generateSudoku()
    }
  }


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
      println(s"$value makes the sudoku repetitive. (x.X) Details: row ${rowIndex + 1} is ${isRowRepetitive(curSudoku,rowIndex)}, column ${columnIndex + 1} is ${isColumnRepetitive(curSudoku,columnIndex)}, block is ${isRowRepetitive(curSudoku,(rowIndex / 3 * 3) + (columnIndex / 3))},Please try writing something else in!")
        fillCell(sudoku, name)
      }
    } else if (!cellIsEmpty(sudoku: Sudoku, rowIndex: Int, columnIndex: Int)) {
      println(s"The cell you wanted to write into is not empty (>_<). Check row ${rowIndex + 1}, column ${columnIndex + 1}, and try again.")
      fillCell(sudoku, name)
    }

  }

  def choosingNextMove(sudoku: Sudoku, name: String): Sudoku = {
    println(pretty(sudoku))
    println(s" $name, you can find your sudoku above, write in your next move:")
    println("1st char => action: i = insert, d = delete")
    println("2nd char => number of the row in which the action should be done")
    println("3rd char => number of the column in which the action should be done")
    println("4th char => the value with which the action should be done")
    println("example 1: i231 = insert the value 1 to row 2 column 3, example 2: d67 = delete the value of row 6 column 7")

    val instructions = readLine()
    val action = instructions.charAt(0)
    val rowIx = instructions.charAt(1).toInt - 49
    val colIx = instructions.charAt(2).toInt - 49
    val charNum = instructions.length
    val insertable = {
      instructions.length > 3 &&
      instructions.charAt(1).isDigit &&
      instructions.charAt(2).isDigit &&
      instructions.charAt(3).isDigit
    }

    if (action == 'i' && charNum >= 4) {
      val value = instructions.charAt(3).toInt - 48
      println(s"inserting value: $value to row: ${rowIx + 1} column: ${colIx + 1}")
      val curSudoku = sudoku.insert(rowIx, colIx, value)
      choosingNextMove(curSudoku, name)

    } else if (action == 'd') {
      println(s"deleting value in row: ${rowIx + 1} column: ${colIx + 1}")
      val curSudoku = sudoku.delete(rowIx, colIx,None)
      choosingNextMove(curSudoku, name)

    } else println(s"I did not understand your request: '$instructions', please check what went wrong and try something else!" )
    choosingNextMove(sudoku, name)
  }
  def playSudoku(): Unit = {
    println("Write your name to start a new game!")
    val name = readLine()
    println(s"Hi $name, write in the number of the level you would like to play! 1: Easy, 2: Medium, 3: Hard")
    val sudoku = generateSudoku()
    fillCell(sudoku, name)
  }


  def main(args: Array[String]): Unit = {
    choosingNextMove(generateEasySudoku(goodSudoku), "k")




  }

}