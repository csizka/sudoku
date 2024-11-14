package junicamp
package sudoku

import sudoku.Examples.*
import sudoku.Generate.*
import sudoku.Pretty.*
import sudoku.Solving.*
import sudoku.Sudoku.*
import sudoku.Validation.*

import java.nio.file.{Files, Path, Paths}
import scala.annotation.{nowarn, tailrec}
import scala.io.AnsiColor.*
import scala.io.StdIn.readLine
import scala.jdk.CollectionConverters.*
import scala.util.{Random, Try}

sealed trait Command
case class Insert(rowIx: Int, colIx: Int, value: Int) extends Command
case class Delete(rowIx: Int, colIx: Int) extends Command
case class Restart() extends Command
case class Undo(numOfSteps: Int) extends Command
case class Finish() extends Command
case class Hint() extends Command

object PlaySudoku {

  def cellIsEmpty(sudoku: Sudoku, row: Int, column: Int): Boolean = {
    sudoku.rows(row)(column).isEmpty
  }

  def definedCells(sudoku: Sudoku): Map[Int, Vector[(Int, Int)]] = {
    val coords = for {
      rowIx <- 0 to 8
      colIx <- 0 to 8
    } yield (rowIx, colIx)
    coords
      .toVector
      .filter((rowIx, colIx) => sudoku.rows(rowIx)(colIx).isDefined)
      .groupBy { case (rowIx, colIx) => sudoku.rows(rowIx)(colIx).get}
  }

  def nthRowRepetitiveCoords(rowIx: Int, sudoku: Sudoku): Vector[Coord]  = {
    (0 to 8)
      .toVector
      .flatMap(colIx => sudoku.rows(rowIx)(colIx).map(v => v -> (rowIx, colIx)))
      .groupMap(_._1)(_._2)
      .filter(_._2.size > 1)
      .values
      .toVector
      .flatten
  }

  def nthColRepetitiveCoords(colIx: Int, sudoku: Sudoku): Vector[(Int, Int)] = {
    {0 to 8}
      .toVector
      .flatMap(rowIx => sudoku.rows(rowIx)(colIx).map(v => v -> (rowIx, colIx)))
      .groupMap(_._1)(_._2)
      .filter(_._2.size > 1)
      .values
      .toVector
      .flatten
  }

  def nthBlockRepetitiveCoords(blockIx: Int, sudoku: Sudoku): Vector[(Int, Int)] = {
    nthBlockCoords(sudoku, blockIx).flatMap((rowIx, colIx) => sudoku.rows(rowIx)(colIx).map(v => v -> (rowIx, colIx)))
      .groupMap(_._1)(_._2)
      .filter(_._2.size > 1)
      .values
      .toVector
      .flatten
  }

  def allRepetitiveCoords(sudoku: Sudoku): Vector[(Int, Int)] = {
    {0 to 8}
      .toVector
      .flatMap { index =>
        nthRowRepetitiveCoords(index, sudoku) ++
          nthColRepetitiveCoords(index, sudoku) ++
          nthBlockRepetitiveCoords(index, sudoku)
      }.distinct
  }

  def repetitionsByRows(sudoku: Sudoku): Map[Int, Set[Int]] = {
    val finalMap = allRepetitiveCoords(sudoku).foldLeft(Map[Int, Set[Int]]()) { case (curMap, (rowIx, colIx)) =>
      curMap + (rowIx -> (curMap.getOrElse(rowIx, Set()) + colIx))
    }
    finalMap
  }

  val zeroCoords = Vector((0,0), (2,6), (3,1), (4,4), (7,5), (8,8))

  def checkLevelInst(inst: String): Either[String, Sudoku] = inst match {
    case "1" =>
      println("You have chosen Easy level.")
      Right(generateEasySudoku(generateControlledSudoku(zeroCoords)))
    case "2" =>
      println("You have chosen Medium level.")
      Right(generateMediumSudoku(generateControlledSudoku(zeroCoords)))
    case "3" =>
      println("You have chosen Hard level.")
      Right(generateHardSudoku(generateControlledSudoku(zeroCoords)))
    case _   =>
      Left(s"Invalid level requested: $inst. Please write in the number of the level you would like to play! 1: Easy, 2: Medium, 3: Hard"
      )
  }

  @tailrec
  def generateSudoku(level: String = readLine()): Sudoku = {
    checkLevelInst(level) match {
      case Right(sudoku) => sudoku
      case Left(error) =>
        println(error)
        generateSudoku()
    }
  }

  def parseCommand(inst: String): Either[String, Command] = inst.toList match {
    case 'i' :: rest =>
      parseInsertCmd(rest)
    case 'd' :: rest =>
      parseDelCmd(rest)
    case 'u' :: rest =>
      parseUndoCmd(rest)
    case 'f' :: Nil =>
      Right(Finish())
    case 'r' :: Nil =>
      Right(Restart())
    case 'h' :: Nil =>
      Right(Hint())
    case _ => Left(s"Your command: '$inst' was not understood, please check what went wrong and try something else! (~_~)")
  }

  def ixIsValid(ix: Int): Boolean = {
    (0 to 8).contains(ix)
  }

  def updateCell(curSudoku: Sudoku, cellParams: (Int, Int, Cell)): Sudoku = cellParams match {
    case (rowIx, colIx, value) =>
      Sudoku(curSudoku.rows.updated(rowIx, curSudoku.rows(rowIx).updated(colIx, value)))
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
  def parseUndoCmd(args: List[Char]): Either[String, Command] = {
    Try(args.mkString.toInt).toOption match {
      case Some(num) if 0 < num =>
        Right(Undo(num))
      case _ => Left(
        s"Undo could not be completed with command: u${args.mkString}. (u_u)" +
          s" The number after the letter 'u' needs to be between 1 and the number of steps already made. Please try something else."
      )
    }
  }

  @nowarn("msg=Unreachable case except for null")
  def execCommand(curSudoku: Sudoku, cmd: Command, startSudoku: Sudoku, changes: CellHistory):
  (Either[String, Sudoku], CellHistory) = cmd match {
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
    case Undo(numOfSteps: Int) =>
      val numOfChanges = changes.length
      if (numOfChanges == 0) {
        (Left(s"There are no steps to undo, choose some other action. (-.-)"), changes)
      } else if (numOfChanges - numOfSteps >= 0) {
        execUndo(curSudoku, changes, numOfSteps)
      } else {
        printColoredMsg(RED, s"You cannot undo more steps than the number of changes you did, therefore $numOfChanges steps will be undone. (x_x)")
        execUndo(curSudoku, changes, numOfChanges)
      }
    case Finish() =>
      if (!isSudokuRepetitionFree(curSudoku)) {
        (Left(s"The Sudoku can not be finished, it contains at least 1 repetition (°o°) " +
          s"please check the repetition."), changes)
      } else if (isSudokuRepetitionFree(curSudoku) && finishSudoku(curSudoku).isEmpty) {
        (Left(s"There is no repetition, but at least one value is entered to the wrong cell (>_<) " +
          s"please restart the game or undo until it becomes solvable."), changes)
      } else {
        (Right(finishSudoku(curSudoku).get), changes)
      }
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
    case Hint() =>
      if (isSudokuRepetitionFree(curSudoku)) {
        cellsWithSingleChoices(curSudoku) match {
          case (rowIx, colIx, value) :: rest =>
            printColoredMsg(GREEN, s"In row ${rowIx + 1}, column ${colIx + 1} only value '$value' can be written to avoid repetition.")
            (Right(curSudoku), changes)
          case Nil =>
            val coords = collectEmptyCellCoords(curSudoku)
              .minBy{ case (rowIx, colIx) => numOfPossibleSolutionsForCell(curSudoku, rowIx, colIx) }
            val possibleVals = possibleSolutionsForCell(curSudoku, coords._1, coords._2)
            printColoredMsg(GREEN, s"At this point to row ${coords._1 + 1}, column ${coords._1 + 1} only one of the following" +
              s" numbers can be written: $possibleVals")
            (Right(curSudoku), changes)
        }
      } else {
        (Left("First make sure your Sudoku is repetition free."), changes)
      }
    case _ => (Left("Unimplemented exec command"), changes)
  }

  def execUndo(curSudoku: Sudoku, changes: CellHistory, numOfSteps: Int): (Either[String, Sudoku], CellHistory) = {
    val undoChanges = changes.take(numOfSteps)
    val nextSudoku = undoChanges.foldLeft(curSudoku)(updateCell)
    (Right(nextSudoku), changes.drop(numOfSteps))
  }

  def instructions(): Unit = {
    println("Write down your next move in the following format:")
    println("1st char => action: i = insert, d = delete, r = restart, u = undo, f = finish, h = hint")
    println("2nd char => number of the row in which the action should be done (needed for insertion and deletion) " +
      "OR the number of steps that should be undone (needed for undo)")
    println("3rd char => number of the column in which the action should be done (needed for insertion and deletion)")
    println("4th char => the value with which the action should be done (needed for insertion)")
    println("Example 1: i231 = insert the value 1 to row 2 column 3, example 2: d67 = delete the value of row 6 column 7, " +
      "example 3: r = restart original sudoku, example 4: u3 = undo the last 3 steps, 5: f = finish sudoku, 6: h = give me a hint.")
  }
  @tailrec
  def choosingNextMove(curSudoku: Sudoku, name: String, startSudoku: Sudoku, changes: CellHistory): Sudoku = {

    if (isSudokuSolved(curSudoku)) {
      printColoredMsg(GREEN, s"Congrats $name, you have solved the Sudoku! (*.*) Look at how beautiful it is:")
      printColoredMsg(GREEN, pretty(curSudoku))
      curSudoku
    } else {
      val repCoordsByRow = repetitionsByRows(curSudoku)
      if (repCoordsByRow.isEmpty) {
        println("Here is the current state of your Sudoku:")
      } else {
        printColoredMsg(RED, "Here is the current state of your Sudoku. All the repetitions in the rows, columns and blocks can be found highlighted with red below: (°-°)")
      }
      println(pretty(curSudoku, repCoordsByRow, RED))
      instructions()

      val rawCmdStr = readLine()
      val cmdParsingRes = parseCommand(rawCmdStr)

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
    choosingNextMove(sudoku, name, sudoku, Vector[(Int,Int,Cell)]())
  }

}