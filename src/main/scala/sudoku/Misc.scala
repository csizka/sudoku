package junicamp
package sudoku

import sudoku.Examples.*
import sudoku.PlaySudoku.*
import sudoku.Pretty.*
import sudoku.Solving.*
import sudoku.Validation.*

import java.nio.file.{Files, Path, Paths}
import scala.annotation.tailrec
import scala.io.AnsiColor
import scala.io.StdIn.readLine
import scala.jdk.CollectionConverters.*
import scala.util.Random

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

object Serde{
  def serialize(sudoku: Sudoku): List[String] =
    sudoku.rows.map { row =>
      row.map(_.fold(".")(x => x.toString)).mkString
    }.toList

  def deserialize(serializedSudoku: List[String]): Sudoku = {
    Sudoku(serializedSudoku.map {
      _.stripSuffix("\n").map {
        case '.' => None
        case x => Some(x.toInt - 48)
      }.toVector
    }.toVector)
  }

  val path = Paths.get("./test.txt")
  val lines = Files.readAllLines(path).asScala
  val wholeFileAsString = Files.readString(path)
  val bytes = Files.readAllBytes(path).toList

  def save(sudoku: Sudoku, path: Path): Unit = {
    Files.write(path, serialize(sudoku).asJava)
  }

  def load(path: Path): Sudoku = {
    deserialize(Files.readAllLines(path).asScala.toList)
  }
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

  def execUndoTailRec(curSudoku: Sudoku, changes: CellHistory, numOfSteps: Int): (Either[String, Sudoku], CellHistory) = {
    @tailrec
    def execUndoHelper(curSudoku: Sudoku, changes: CellHistory): (Either[String, Sudoku], CellHistory) = changes match {
      case (rowIx: Int, colIx: Int, value) +: rest =>
        value match
          case None =>
            println(s"Deleting value in row: ${rowIx + 1}, column: ${colIx + 1}")
            execUndoHelper(curSudoku.delete(rowIx, colIx), rest)
          case Some(num) =>
            println(s"Rewriting value: $num in row: ${rowIx + 1}, column: ${colIx + 1}")
            execUndoHelper(curSudoku.insert(rowIx, colIx, num), rest)
      case _ =>
        (Right(curSudoku), changes.drop(numOfSteps))
    }

    execUndoHelper(curSudoku, changes.take(numOfSteps))
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
        println(s"$value makes the sudoku repetitive. (x.X) Details: row ${rowIndex + 1} is ${isRowRepetitive(curSudoku, rowIndex)}, column ${columnIndex + 1} is ${isColumnRepetitive(curSudoku, columnIndex)}, block is ${isRowRepetitive(curSudoku, (rowIndex / 3 * 3) + (columnIndex / 3))},Please try writing something else in!")
        fillCell(sudoku, name)
      }
    } else if (!cellIsEmpty(sudoku: Sudoku, rowIndex: Int, columnIndex: Int)) {
      println(s"The cell you wanted to write into is not empty (>_<). Check row ${rowIndex + 1}, column ${columnIndex + 1}, and try again.")
      fillCell(sudoku, name)
    }
  }
  

  def countOfSingleChoiceCells(sudoku: Sudoku): Int = {
    collectEmptyCellCoords(sudoku).count { case (x, y) => numOfPossibleSolutionsForCell(sudoku, x, y) == 1 }
  }

  def prettyColourRow(prettyRows: List[String], row: Int, cols: Vector[Int], colour: String): List[String] = {
    val rowIX = row * 2 + 1
    val orderedCols = cols.sorted
    val end = "\u001B[0m"
    val colourLen = colour.length + end.length
    val initLen = prettyRows(rowIX).length
    val initIX = 0

    val (finalList, finalLen, finalIX) =
      orderedCols.foldLeft {
        (prettyRows, prettyRows(rowIX).length, initIX)
      } { case ((curList, curLen, curIX), curCoord) =>
        val charIX = (curCoord + 1) * 4 + curIX * colourLen - 1
        val nextRow = curList(rowIX).take(charIX) + colour + curList(rowIX).slice(charIX, charIX + 1) +
          "\u001b[0m" + curList(rowIX).slice(charIX + 1, curLen)
        val nextList = curList.updated(rowIX, nextRow)
        val nextLen = curLen + colourLen
        val nextIX = curIX + 1
        (nextList, nextLen, nextIX)
      }
    finalList
  }

  def prettyList(sudoku: Sudoku): List[String] = {
    List(
      " " + firstRowPretty + " ",
      "1" + prettyRow(sudoku.rows(0)) + "1",
      " " + singleRowPretty + " ",
      "2" + prettyRow(sudoku.rows(1)) + "2",
      " " + singleRowPretty + " ",
      "3" + prettyRow(sudoku.rows(2)) + "3",
      " " + doubleRowPretty + " ",
      "4" + prettyRow(sudoku.rows(3)) + "4",
      " " + singleRowPretty + " ",
      "5" + prettyRow(sudoku.rows(4)) + "5",
      " " + singleRowPretty + " ",
      "6" + prettyRow(sudoku.rows(5)) + "6",
      " " + doubleRowPretty + " ",
      "7" + prettyRow(sudoku.rows(6)) + "7",
      " " + singleRowPretty + " ",
      "8" + prettyRow(sudoku.rows(7)) + "8",
      " " + singleRowPretty + " ",
      "9" + prettyRow(sudoku.rows(8)) + "9",
      " " + lastRowPretty + " ",
    )
  }
  def prettyColours(sudoku: Sudoku, colour: String, coords: Map[Int, Set[Int]]): String = {
    val list = prettyList(sudoku)
    val finalList = coords.foldLeft(list) { case (curList, curCoord) =>
      val (rowIX, cols) = curCoord
      val colVector = cols.toVector
      val nextList = prettyColourRow(curList, rowIX, colVector, colour)
      nextList
    }
    finalList.mkString("\n")
  }
}

