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

  def identifyRepetitiveVector(rows: Vector[Row]): Option[Int] = {
    {0 to 8}.toList.find(x => !areCellsRepetitionFree(rows(x)))
  }

  def identifyRepetitiveColumn(sudoku: Sudoku): Option[Int] = {
    val allColumns = getAllColumns(sudoku)
    identifyRepetitiveVector(allColumns)
  }

  def identifyRepetitiveBlock(sudoku: Sudoku): Option[Int] = {
    val allBlocks = getAllBlocks(sudoku)
    identifyRepetitiveVector(allBlocks)
  }

  def identifyRepetitiveRow(sudoku: Sudoku): Option[Int] = {
    val allRows = sudoku.rows
    identifyRepetitiveVector(sudoku.rows)
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
      if (isSudokuSolved(curSudoku)) {
        println(s" Congrats $name, you have solved the Sudoku! ^.^ Look at how beautiful it is:")
        println(pretty(curSudoku))
      } else if (!isSudokuSolved(curSudoku) && isSudokuRepetitionFree(curSudoku)) {
        fillCell(curSudoku, name)
      } else if (!isSudokuRepetitionFree(curSudoku)) {

      }
    } else if (!cellIsEmpty(sudoku: Sudoku, rowIndex: Int, columnIndex: Int)) {
      println(s"The cell you wanted to write into is not empty. Check row ${rowIndex + 1}, column ${columnIndex + 1}, and try again.")
      fillCell(sudoku, name)
    }

  }
  def playSudoku(): Unit = {
    println("Write your name to start a new game!")
    val name = readLine()
    println(s"Hi $name, write in the number of the level you would like to play! 1: Easy, 2: Medium, 3: Hard, 4: Very Hard")

    def checkLevel(): Unit = {}
    val level = readLine().toInt
    if (level == 1) {
      val sudoku = generateEasySudoku(generateNonRandomSolvedSudoku(0, 0))


    } else if (level == 2) {
      val sudoku = generateMediumSudoku(generateNonRandomSolvedSudoku(0, 0))

    } else if (level == 3) {
      val sudoku = generateHardSudoku(generateNonRandomSolvedSudoku(0, 0))

    } else if (level == 4) {
      val sudoku = generateTheHardestSudoku(generateNonRandomSolvedSudoku(0, 0))

    } else {
      println(s"Invalid level requested: $level. Please write in the number of the level you would like to play! 1: Easy, 2: Medium, 3: Hard, 4: Very Hard ")
      checkLevel()
    }
    checkLevel()

  }



  def main(args: Array[String]): Unit = {

  }

}