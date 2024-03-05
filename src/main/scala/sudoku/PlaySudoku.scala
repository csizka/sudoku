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
      println(s"You have entered a wrong value: $curNum, please enter a number between 1 and 9!")
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
    println("Here is your Sudoku:")
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
        println(s"Congrats $name, you have solved the Sudoku! ^.^ Look at how beautiful it is:")
        println(pretty(curSudoku))
      } else if (!sudokuIsSolved && sudokuIsRepetitionFree) {
        fillCell(curSudoku, name)
      } else if (!sudokuIsRepetitionFree) {
      println(s"$value makes the sudoku repetitive. Details: row ${rowIndex + 1} is ${isRowRepetitive(curSudoku,rowIndex)}, column ${columnIndex + 1} is ${isColumnRepetitive(curSudoku,columnIndex)}, block is ${isRowRepetitive(curSudoku,(rowIndex / 3 * 3) + (columnIndex / 3))},Please try writing something else in!")
        fillCell(sudoku, name)
      }
    } else if (!cellIsEmpty(sudoku: Sudoku, rowIndex: Int, columnIndex: Int)) {
      println(s"The cell you wanted to write into is not empty. Check row ${rowIndex + 1}, column ${columnIndex + 1}, and try again.")
      fillCell(sudoku, name)
    }

  }
  def playSudoku(): Unit = {
    println("Write your name to start a new game!")
    val name = readLine()
    println(s"Hi $name, write in the number of the level you would like to play! 1: Easy, 2: Medium, 3: Hard")
    val sudoku = generateSudoku()
    fillCell(sudoku, name)
  }


  def main(args: Array[String]): Unit = {
playSudoku()




  }

}