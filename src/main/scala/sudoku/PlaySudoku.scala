package junicamp
package sudoku

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.util.Random
import java.nio.file.{Files, Path, Paths}
import scala.io.AnsiColor._
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
case class Restart() extends Command

object PlaySudoku {

  def cellIsEmpty(sudoku: Sudoku, row: Int, column: Int): Boolean = {
    sudoku.rows(row)(column).isEmpty
  }
  def checkLevelInst(inst: String): Either[String, Sudoku] = inst match {
    case "1" =>
      println("You have chosen Easy level.")
      Right(generateEasySudoku(generateNonRandomSolvedSudoku(0, 0)))
    case "2" =>
      println("You have chosen Medium level.")
      Right(generateMediumSudoku(generateNonRandomSolvedSudoku(0, 0)))
    case "3" =>
      println("You have chosen Hard level.")
      Right(generateHardSudoku(generateNonRandomSolvedSudoku(0, 0)))
    case _   =>
      Left(s"${RED}Invalid level requested: $inst. Please write in the number of the level you would like to play! 1: Easy, 2: Medium, 3: Hard $RESET"
      )
  }

  @tailrec
  def generateSudoku(): Sudoku = {
    val level = readLine()
    checkLevelInst(level) match {
      case Right(sudoku) => sudoku
      case Left(error) =>
        println(error)
        generateSudoku()
    }
    }

  def parseCommand(inst: String): Either[String, Command] = inst.toList match {
    case Nil => Left(s"${RED}Your command: '$inst' was not understood, please check what went wrong and try something else! (~_~)$RESET")
    case 'i' :: rest =>
      parseInsertCmd(rest, inst)
    case 'd' :: rest =>
      parseDelCmd(rest, inst)
    case 'r' :: Nil =>
      Right(Restart())
    case _ => Left(s"${RED}Your command: '$inst' was not understood, please check what went wrong and try something else! (~_~)$RESET")
  }

  def ixIsValid(ix: Int): Boolean = {
    (0 to 8).contains(ix)
  }

  def parseInsertCmd(args: List[Char], rawCmd: String): Either[String, Command] = args.map(x => x.toInt - 49) match {
    case rowIx :: colIx :: valueMinusOne :: Nil
      if ixIsValid(rowIx) && ixIsValid(colIx) && ixIsValid(valueMinusOne) => Right(Insert(rowIx, colIx, valueMinusOne + 1))
    case _ => Left(
      s"${RED}Insertion could not be completed with the command $rawCmd. (>_<) " +
      s"After the letter 'i' there has to be 3 numbers that are between 1 and 9, " +
      s"and they have to point to a cell, that was empty in the original sudoku. Please try something else!$RESET"
    )
  }

  def parseDelCmd(args: List[Char], rawCmd: String): Either[String, Command] = args.map(x => x.toInt - 49) match {
    case rowIx :: colIx :: Nil
      if ixIsValid(rowIx) && ixIsValid(colIx) => Right(Delete(rowIx, colIx))
    case _ => Left(
      s"${RED}Deletion could not be completed with command: $args. (u_u)" +
      s" The 2 numbers after the letter 'd' need to be between 1 and 9, and they can only point to a cell, " +
      s"that was empty at the beginning of the game. Please try something else.$RESET"
    )
  }


  def execCommand(curSudoku: Sudoku, cmd: Command, rawCmd: String, startSudoku: Sudoku): Either[String, Sudoku] = cmd match {
    case Insert(rowIx, colIx, value) =>
      if (cellIsEmpty(startSudoku, rowIx, colIx)) {
        val nextSudoku = curSudoku.insert(rowIx, colIx, value)
        println(s"inserting value: $value to row: ${rowIx + 1} column: ${colIx + 1}")
        Right(nextSudoku)
      } else {
        Left(s"${RED}Insertion could not be completed with the command $rawCmd. (o_o)" +
          s" only those cells can be modified, that were empty in the original sudoku. Please try something else!$RESET")
      }
    case Delete(rowIx, colIx) =>
      if (cellIsEmpty(startSudoku, rowIx, colIx)) {
        println(s"deleting value in row: ${rowIx + 1} column: ${colIx + 1}")
        val nextSudoku = curSudoku.delete(rowIx, colIx)
        Right(nextSudoku)
      } else {
        Left(s"${RED}Deletion could not be completed with command: $rawCmd. (u_u) The 2 numbers after the letter 'd' need to be between 1 and 9, and they can only point to a cell, that was empty at the beginning of the game. Please try something else.$RESET")
      }
    case Restart() =>
      println("Are you sure you want to restart? If you do, all your progress will be lost (ToT).")
      println("Write in 'y' if you want ro restart. If you do not want to restart write in: 'n'")
      val response = readLine()
      response.toList match {
        case 'y' :: Nil =>
          println("Restarting the game.")
          Right(startSudoku)
        case 'n' :: Nil =>
          println("Resuming to the previous state the game.")
          Right(curSudoku)
        case _ =>
          println(s"${RED} Your command:$response was not understood, therefore you are resuming to the previous state the game $RESET.")
          Right(curSudoku)
      }
    case _ => Left(s"${RED}Unimplemented exec command$RESET")
  }

  @tailrec
  def choosingNextMove(curSudoku: Sudoku, name: String, startSudoku: Sudoku): Sudoku = {
    if (isSudokuSolved(curSudoku)) {
      println(s"${GREEN}Congrats $name, you have solved the Sudoku! (*.*) Look at how beautiful it is:$RESET")
      println(GREEN + pretty(curSudoku))
      curSudoku
    } else {
      println("Here is the current state of your Sudoku:")
      println(pretty(curSudoku))
      println("Write down your next move in the following format:")
      println("1st char => action: i = insert, d = delete")
      println("2nd char => number of the row in which the action should be done (needed for insertion and deletion)")
      println("3rd char => number of the column in which the action should be done (needed for insertion and deletion)")
      println("4th char => the value with which the action should be done (needed for insertion)")
      println("Example 1: i231 = insert the value 1 to row 2 column 3, example 2: d67 = delete the value of row 6 column 7, example 3: r = restart original sudoku.")

      val rawCmdStr = readLine()
      val cmdParsingRes = parseCommand(rawCmdStr)

      cmdParsingRes match {
        case Right(cmd) =>
          execCommand(curSudoku, cmd, rawCmdStr, startSudoku) match {
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

}