package junicamp
package sudoku

import sudoku.Examples.*
import sudoku.Pretty.*
import sudoku.Solving.*
import sudoku.Sudoku.*

import scala.annotation.tailrec
import scala.util.*

object Validation {
  
  //Row Tests
  def areCellsSolved(vector: Vector[Cell]): Boolean = {
    @tailrec
    def areCellsSolvedHelper(restElems: Vector[Cell], set: Set[Int]): Boolean = restElems match {
      case Vector() => set == (1 to 9).toSet
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

  def areAllRowsSolved(sudoku: Sudoku): Boolean = {
    sudoku.rows.forall(areCellsSolved)
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

  def nthBlockCoords(sudoku: Sudoku, n: Int): Vector[(Int, Int)] = {
    if ( 0 <= n && n <= 8) {
      val fstXCoord = n / 3 * 3
      val fstYCoord = n % 3 * 3
      for {
        x <- Vector(fstXCoord, fstXCoord + 1, fstXCoord + 2)
        y <- Vector(fstYCoord, fstYCoord + 1, fstYCoord + 2)
      } yield (x,y)

    } else throw new IndexOutOfBoundsException(s"The index $n is not valid, please use one between 0 and 8.")
  }

  def getNthBlock(sudoku: Sudoku, n: Int): Block = {
    nthBlockCoords(sudoku, n).map( (x, y) => sudoku.rows(x)(y))
  }

  def getAllBlocks(sudoku: Sudoku): Vector[Block] = {
      sudoku.rows.indices
        .map(n => getNthBlock(sudoku, n))
        .toVector
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
    areAllBlocksSolved(sudoku) &&
    areAllColumnsSolved(sudoku) &&
    areAllRowsSolved(sudoku)
  }
}
