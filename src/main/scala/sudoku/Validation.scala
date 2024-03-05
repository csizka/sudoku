package junicamp
package sudoku

import sudoku.Examples.*
import sudoku.Pretty.*
import sudoku.Sudoku.*
import sudoku.Solving.*
import scala.util.*

import scala.annotation.tailrec

// TODO: separate solving algo from validation and move to new file

object Validation {

  //Row Tests
  def areCellsSolved(vector: Vector[Cell]): Boolean = {
    @tailrec
    def areCellsSolvedHelper(restElems: Vector[Cell], set: Set[Int]): Boolean = restElems match {
      case Vector() => set == Set(1, 2, 3, 4, 5, 6, 7, 8, 9)
      case Some(x) +: rest if (1 to 9).contains(x) => areCellsSolvedHelper(rest, set + x)
      case _ => false
    }
    areCellsSolvedHelper(vector, Set.empty[Int])
  }

  def areCellsRepetitionFree(vector: Vector[Cell]): Boolean = {
    val filledCells = vector.flatten
    filledCells.size == filledCells.toSet.size
  }

  def areCellsValid(vector: Vector[Cell]): Boolean = {
    vector.flatten.toSet.subsetOf((1 to 9).toSet)
  }

  def areCellsFilled(vector: Vector[Cell]): Boolean = {
    vector.size == 9 && !vector.contains(None)
  }

  def allRowsSolved(sudoku: Sudoku): Boolean = {
    sudoku.rows.forall(areCellsSolved) && sudoku.rows.size == 9
  }

  def allRowsRepetitionFree(sudoku: Sudoku): Boolean = {
    sudoku.rows.forall(areCellsRepetitionFree)
  }

  // Column tests
  def getNthColumn(sudoku: Sudoku, n: Int): Row = {
    if (0 <= n && n <= 8)
      sudoku.rows.map(row => row(n))
    else throw new IndexOutOfBoundsException(s" the index inserted: $n is invalid, please use an index between 0 and 8")
  }
  
  def getAllColumns(sudoku: Sudoku): Vector[Column] = {
    sudoku.rows.indices
      .map(n => getNthColumn(sudoku, n))
      .toVector
  }

  def areAllColumnsRepetitionFree(sudoku: Sudoku): Boolean = {
    getAllColumns(sudoku).forall(areCellsRepetitionFree)
  }

  def areAllColumnsSolved(sudoku: Sudoku): Boolean = {
    getAllColumns(sudoku).forall(areCellsSolved)
  }

  def getNthBlock(sudoku: Sudoku, n: Int): Block = {
    if (0 <= n && n <= 8) {
      val row = n / 3 * 3
      val allRows = Vector(row, row + 1, row + 2)
      val column = n % 3 * 3

      allRows.flatMap(x => sudoku.rows(x).slice(column, column + 3))
    }
    else throw new IndexOutOfBoundsException(s"The index $n is not valid, please use one between 0 and 8.")
  }
  def getNthBlockV2(sudoku: Sudoku, n: Int): Block = {
    if (0 <= n && n <= 8) {
      val firstXCoord = n / 3 * 3
      val firstYCoord = n % 3 * 3
      val blockCoords = for {
        x <- Vector(firstXCoord, firstXCoord + 1, firstXCoord + 2)
        y <- Vector(firstYCoord, firstYCoord + 1, firstYCoord + 2)
      } yield (x,y)
      blockCoords.map((x, y) => sudoku.rows(x)(y))
    }
    else throw new IndexOutOfBoundsException(s"The index $n is not valid, please use one between 0 and 8.")
  }
  
  def getAllBlocks(sudoku: Sudoku): Vector[Column] = {
    if (sudoku.rows.nonEmpty) {
      sudoku.rows.indices
        .map(n => getNthBlock(sudoku, n))
        .toVector
    }
    else Vector.empty[Column]
  }
  
  def areAllBlocksRepetitionFree(sudoku: Sudoku): Boolean = {
    getAllBlocks(sudoku).forall(areCellsRepetitionFree)
  }

  def areAllBlocksSolved(sudoku: Sudoku): Boolean = {
    getAllBlocks(sudoku).forall(areCellsSolved)
  }
  
  def isSudokuRepetitionFree(sudoku:Sudoku): Boolean = {
    areAllBlocksRepetitionFree(sudoku) &&
    areAllColumnsRepetitionFree(sudoku) &&
    allRowsRepetitionFree(sudoku)
  }

  def isSudokuValid(sudoku: Sudoku): Boolean = {
    sudoku.rows.forall(areCellsValid)
}

  def isSudokuSolved(sudoku: Sudoku): Boolean = {
    isSudokuRepetitionFree(sudoku) &&
    sudoku.rows.flatten.size == 81 &&
    numOfEmptyCells(sudoku) == 0 &&
    sudoku.rows.flatten.map {
      case Some(x) => x
      case None => 0
    }.toSet == (1 to 9).toSet

  }
}
