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
import Misc.*

sealed trait Command
case class Insert(rowIx: Int, colIx: Int, value: Int) extends Command
case class Delete(rowIx: Int, colIx: Int) extends Command

object PlaySudoku {

  def cellIsEmpty(sudoku: Sudoku, row: Int, column: Int): Boolean = {
    sudoku.rows(row)(column).isEmpty
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

  def parseCommand(inst: String): Either[String, Command] = inst.toList match {
    case Nil => Left(s"Your command: '$inst' was not understood, please check what went wrong and try something else! (~_~)")
    case 'i' :: rest =>
      parseInsertCmd(rest)
    case 'd' :: rest =>
      parseDelCmd(rest)
    case _ => Left(s"Your command: '$inst' was not understood, please check what went wrong and try something else! (~_~)")
  }

  def ixIsValid(ix: Int): Boolean = {
    (0 to 8).contains(ix)
  }

  def parseInsertCmd(args: List[Char]): Either[String, Command] = args.map(x => x.toInt - 49) match {
    case rowIx :: colIx :: valueMinusOne :: Nil
      if ixIsValid(rowIx) && ixIsValid(colIx) && ixIsValid(valueMinusOne) => Right(Insert(rowIx, colIx, valueMinusOne + 1))
    case _ => Left(
      s"Insertion could not be completed with the command ${args.mkString}. (>_<) " +
      s"After the letter 'i' there has to be 3 numbers that are between 1 and 9, " +
      s"and they have to point to a cell, that was empty in the original sudoku. Please try something else!"
    )
  }

  def parseDelCmd(args: List[Char]): Either[String, Command] = args.map(x => x.toInt - 49) match {
    case rowIx :: colIx :: Nil
      if ixIsValid(rowIx) && ixIsValid(colIx) => Right(Delete(rowIx, colIx))
    case _ => Left(
      s"Deletion could not be completed with command: ${args.mkString}. (u_u)" +
      s" The 2 numbers after the letter 'd' need to be between 1 and 9, and they can only point to a cell, " +
      s"that was empty at the beginning of the game. Please try something else."
    )
  }

  def execCommand(curSudoku: Sudoku, cmd: Command, startSudoku: Sudoku): Either[String, Sudoku] = cmd match {
    case Insert(rowIx, colIx, value) =>
      if (cellIsEmpty(startSudoku, rowIx, colIx)) {
        val nextSudoku = curSudoku.insert(rowIx, colIx, value)
        println(s"inserting value: $value to row: ${rowIx + 1} column: ${colIx + 1}")
        Right(nextSudoku)
      } else {
        Left(s"Insertion could not be completed with the command $cmd. (o_o)" +
          s" only those cells can be modified, that were empty in the original sudoku. Please try something else!")
      }
    case Delete(rowIx, colIx) =>
      if (cellIsEmpty(startSudoku, rowIx, colIx)) {
        println(s"deleting value in row: ${rowIx + 1} column: ${colIx + 1}")
        val nextSudoku = curSudoku.delete(rowIx, colIx)
        Right(nextSudoku)
      } else {
        Left(s"Deletion could not be completed with command: $cmd. (u_u) The 2 numbers after the letter 'd' need to be between 1 and 9, and they can only point to a cell, that was empty at the beginning of the game. Please try something else.")
      }
    case _ => Left("Unimplemented exec command")
  }

  @tailrec
  def choosingNextMove(curSudoku: Sudoku, name: String, startSudoku: Sudoku): Sudoku = {
    if (isSudokuSolved(curSudoku)) {
      println(s"Congrats $name, you have solved the Sudoku! (*.*) Look at how beautiful it is:")
      println(pretty(curSudoku))
      curSudoku
    } else {
      println("Here is the current state of your Sudoku:")
      println(pretty(curSudoku))
      println("Write down your next move in the following format:")
      println("1st char => action: i = insert, d = delete")
      println("2nd char => number of the row in which the action should be done (needed for insertion and deletion)")
      println("3rd char => number of the column in which the action should be done (needed for insertion and deletion)")
      println("4th char => the value with which the action should be done (needed for insertion)")
      println("Example 1: i231 = insert the value 1 to row 2 column 3, example 2: d67 = delete the value of row 6 column 7, example 3: r = reset original sudoku.")

      val rawCmdStr = readLine()
      val cmdParsingRes = parseCommand(rawCmdStr)

      cmdParsingRes match {
        case Right(cmd) =>
          execCommand(curSudoku, cmd, startSudoku) match {
            case Right (nextSudoku) =>
              choosingNextMove(nextSudoku, name, startSudoku)
            case Left(error) =>
              println(error)
              choosingNextMove(curSudoku, name, startSudoku)
        }
        case Left(error) =>
          println(error)
          choosingNextMove(curSudoku, name, startSudoku)
      }
    }
  }

  def playSudoku(): Unit = {
    println("Write your name to start a new game!(^u^)")
    val name = readLine()
    println(s"Hi $name, write in the number of the level you would like to play! 1: Easy, 2: Medium, 3: Hard (OuO)")
    val sudoku = generateSudoku()
    choosingNextMove(sudoku, name, sudoku)
  }

  def main(args: Array[String]): Unit = {
    choosingNextMove(oneCellMissingSudoku, "k", someCellMissingSudoku)
  }
}