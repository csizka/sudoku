package junicamp
package sudoku

import scala.annotation.{nowarn, tailrec}
import scala.io.StdIn.readLine
import scala.util.{Random, Try}
import java.nio.file.{Files, Path, Paths}
import scala.io.AnsiColor.*
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
case class Undo(numOfSteps: Int, cmds: Vector[(Int, Int, Cell)]) extends Command

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
      Left(s"Invalid level requested: $inst. Please write in the number of the level you would like to play! 1: Easy, 2: Medium, 3: Hard"
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

  def parseCommand(inst: String, changes: Vector[(Int, Int, Cell)]): Either[String, Command] = inst.toList match {
    case Nil => Left(s"Your command: '$inst' was not understood, please check what went wrong and try something else! (~_~)")
    case 'i' :: rest =>
      parseInsertCmd(rest)
    case 'd' :: rest =>
      parseDelCmd(rest)
    case 'u' :: rest =>
      parseUndoCmd(rest, changes)
    case 'r' :: Nil =>
      Right(Restart())
    case _ => Left(s"Your command: '$inst' was not understood, please check what went wrong and try something else! (~_~)")
  }

  def ixIsValid(ix: Int): Boolean = {
    (0 to 8).contains(ix)
  }

  def parseInsertCmd(args: List[Char]): Either[String, Command] = args.map(x => x.toInt - 49) match {
    case rowIx :: colIx :: valueMinusOne :: Nil
      if ixIsValid(rowIx) && ixIsValid(colIx) && ixIsValid(valueMinusOne) => Right(Insert(rowIx, colIx, valueMinusOne + 1))
    case _ => Left(
      s"Insertion could not be completed with the command i${args.mkString}. (>_<) " +
      s"After the letter 'i' there has to be 3 numbers that are between 1 and 9, " +
      s"and they have to point to a cell, that was empty in the original sudoku. Please try something else!"
    )
  }

  def parseDelCmd(args: List[Char]): Either[String, Command] = args.map(x => x.toInt - 49) match {
    case rowIx :: colIx :: Nil
      if ixIsValid(rowIx) && ixIsValid(colIx) => Right(Delete(rowIx, colIx))
    case _ => Left(
      s"Deletion could not be completed with command: d${args.mkString}. (u_u)" +
      s" The 2 numbers after the letter 'd' need to be between 1 and 9, and they can only point to a cell, " +
      s"that was empty at the beginning of the game. Please try something else."
    )
  }
  //TODO: should work for multi-digits as well
  def parseUndoCmd(args: List[Char], changes: Vector[(Int, Int, Cell)]): Either[String, Command] = {
    def stepCount = changes.size
    Try(args.mkString.toInt).toOption match {
      case Some(num) if num > stepCount =>
        printColoredMsg(RED, "The number of steps you requested to undo is bigger, than the number of step you made, so the game will be restarted.")
        Right(Restart())
      case Some(num) if 0 < num =>
        Right(Undo(num, changes))
      case _ => Left(
        s"Undo could not be completed with command: u${args.mkString}. (u_u)" +
          s" The number after the letter 'u' needs to be between 1 and the number of steps already made. Please try something else."
      )
    }
  }

  @nowarn("msg=Unreachable case except for null")
  def execCommand(curSudoku: Sudoku, cmd: Command, startSudoku: Sudoku, changes: Vector[(Int, Int, Cell)]):
  (Either[String, Sudoku], Vector[(Int, Int, Cell)]) = cmd match {
    case Insert(rowIx, colIx, value) =>
      if (cellIsEmpty(startSudoku, rowIx, colIx)) {
        val nextSudoku = curSudoku.insert(rowIx, colIx, value)
        println(s"inserting value: $value to row: ${rowIx + 1} column: ${colIx + 1}")
        (Right(nextSudoku),(rowIx, colIx, curSudoku.rows(rowIx)(colIx)) +: changes)
      } else {
        (Left(s"Insertion could not be completed with the command i${rowIx + 1}${colIx + 1}$value. (o_o)" +
          s" only those cells can be modified, that were empty in the original sudoku. Please try something else!"), changes)
      }
    case Delete(rowIx, colIx) =>
      if (cellIsEmpty(startSudoku, rowIx, colIx)) {
        println(s"deleting value in row: ${rowIx + 1} column: ${colIx + 1}")
        val nextSudoku = curSudoku.delete(rowIx, colIx)
        (Right(nextSudoku), (rowIx, colIx, curSudoku.rows(rowIx)(colIx)) +: changes)
      } else {
        (Left(s"Deletion could not be completed with command: d${rowIx + 1}${colIx + 1}. (u_u) " +
          s"The 2 numbers after the letter 'd' need to be between 1 and 9, and they can only point to a cell, " +
          s"that was empty at the beginning of the game. Please try something else."), changes)
      }
    case Undo(numOfSteps: Int, toReset: Vector[(Int, Int, Cell)]) =>
      execUndo(curSudoku, changes, numOfSteps)
    case Restart() =>
      println("Are you sure you want to restart? If you do, all your progress will be lost (ToT).")
      println("Write in 'y' if you want ro restart. If you do not want to restart write in: 'n'")
      val response = readLine()
      response match {
        case "y" =>
          println("Restarting the game.")
          (Right(startSudoku), Vector.empty[(Int,Int,Cell)])
        case "n" =>
          println("Resuming to the previous state the game.")
          (Right(curSudoku), changes)
        case _   =>
          (Left(s"Your command:$response was not understood, therefore you are resuming to the previous state the game."), changes)
      }
    case _ => (Left("Unimplemented exec command"), changes)
  }

  def execUndo(curSudoku:Sudoku, changes: Vector[(Int, Int, Cell)], numOfSteps: Int): (Either[String, Sudoku], Vector[(Int, Int, Cell)]) = {
    @tailrec
    def execUndoHelper(curSudoku: Sudoku, changes: Vector[(Int, Int, Cell)]): (Either[String, Sudoku], Vector[(Int, Int, Cell)]) = changes match {
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
  def choosingNextMove(curSudoku: Sudoku, name: String, startSudoku: Sudoku, changes: Vector[(Int, Int, Cell)]): Sudoku = {
    if (isSudokuSolved(curSudoku)) {
      printColoredMsg(GREEN, s"Congrats $name, you have solved the Sudoku! (*.*) Look at how beautiful it is:")
      printColoredMsg(GREEN, pretty(curSudoku))
      curSudoku
    } else {
      println("Here is the current state of your Sudoku:")
      println(pretty(curSudoku))
      println("Write down your next move in the following format:")
      println("1st char => action: i = insert, d = delete, r = restart, u = undo")
      println("2nd char => number of the row in which the action should be done (needed for insertion and deletion) " +
        "OR the number of steps that should ne undone (needed for undo)")
      println("3rd char => number of the column in which the action should be done (needed for insertion and deletion)")
      println("4th char => the value with which the action should be done (needed for insertion)")
      println("Example 1: i231 = insert the value 1 to row 2 column 3, example 2: d67 = delete the value of row 6 column 7, " +
        "example 3: r = restart original sudoku, example 4: u3 = undo the last 3 steps.")

      val rawCmdStr = readLine()
      val cmdParsingRes = parseCommand(rawCmdStr, changes)

      cmdParsingRes match {
        case Right(cmd) =>
          execCommand(curSudoku, cmd, startSudoku, changes) match {
            case (Right (nextSudoku), curChanges) =>
              choosingNextMove(nextSudoku, name, startSudoku, curChanges)
            case (Left(error), curChanges) =>
              printColoredMsg(RED, error)
              choosingNextMove(curSudoku, name, startSudoku, changes)
          }
        case Left(error) =>
          println(printColoredMsg(RED, error))
          choosingNextMove(curSudoku, name, startSudoku, changes)
      }
    }
  }

  def playSudoku(): Unit = {
    println("Write your name to start a new game!(^u^)")
    val name = readLine()
    println(s"Hi $name, write in the number of the level you would like to play! 1: Easy, 2: Medium, 3: Hard (OuO)")
    val sudoku = generateSudoku()
    choosingNextMove(sudoku, name, sudoku, Vector.empty[(Int,Int,Cell)])
  }

}