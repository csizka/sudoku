package junicamp
package sudoku

import sudoku.Validation.*
import Pretty.*

import scala.annotation.tailrec

object Solving {
  def possibleSolutionsForCell(sudoku: Sudoku, rowIndex: Int, columnIndex: Int): Set[Int] = {
    if (0 <= rowIndex && 0 <= columnIndex && columnIndex <= 8 && rowIndex <= 8)
      (1 to 9).toSet --
        sudoku.rows(rowIndex).flatten.toSet --
        getNthColumn(sudoku, columnIndex).flatten.toSet --
        getNthBlock(sudoku, (rowIndex / 3 * 3) + (columnIndex / 3)).flatten.toSet
    else throw new IndexOutOfBoundsException(s"Row index $rowIndex || $columnIndex is/ are not valid, please give a number between 0 and 8.")
  }
  
  def numOfPossibleSolutionsForCell(sudoku: Sudoku, rowIndex: Int, columnIndex: Int): Int =
    possibleSolutionsForCell(sudoku, rowIndex, columnIndex).size
  
  def collectEmptyCellCoords(sudoku: Sudoku): List[(Int, Int)] = {
    val coords = for {
      x <- 0 to 8
      y <- 0 to 8
    } yield (x, y)
    coords.filter { case (x, y) => sudoku.rows(x)(y).isEmpty }.toList
  }

  def sumOfPossibleSolutionsForAllCells(sudoku: Sudoku): Int = {
    val coords = collectEmptyCellCoords(sudoku)
    coords
      .map { case (x, y) => numOfPossibleSolutionsForCell(sudoku, x, y) }
      .sum
  }

  def numOfEmptyCells(sudoku: Sudoku): Int =
    collectEmptyCellCoords(sudoku).size

  def calcNextSteps(sudoku: Sudoku, row: Int, col: Int): List[Sudoku] = {
    possibleSolutionsForCell(sudoku, row, col).toList.map(x => sudoku.insert(row, col, x))
  }

  def cellsWithSingleChoices(sudoku: Sudoku): List[(Int, Int, Int)] = {
    collectEmptyCellCoords(sudoku)
      .map { case (x, y) => (x, y, possibleSolutionsForCell(sudoku, x, y).toList) }
      .collect { case (x, y, List(singlePossibleValue)) => (x, y, singlePossibleValue) }
  }

  def fillCellsWithSingleChoices(sudoku: Sudoku): Sudoku = {
    cellsWithSingleChoices(sudoku)
      .foldLeft(sudoku) { case (accCur: Sudoku, (x, y, singlePossibleValue)) =>
        accCur.insert(x, y, singlePossibleValue)
      }
  }

  def fillCellsWithSingleChoicesRepeatedly: Sudoku => Sudoku =
    fix(fillCellsWithSingleChoices)

  @tailrec
  def fix[T](f: T => T)(x: T): T = {
    val res = f(x)
    if (x != res) fix(f)(res)
    else x
  }

  @tailrec
  def calcLogicalNextSteps(lstOfSudoku: List[Sudoku]): List[Sudoku] = lstOfSudoku match {
    case Nil => List.empty
    case curSudoku :: restSudokus =>
      val singleChoicesFilled = fix(fillCellsWithSingleChoices)(curSudoku)
      val emptyCells = collectEmptyCellCoords(singleChoicesFilled)
      val sudokuIsUnsolvable = emptyCells.exists{ case (x, y) => numOfPossibleSolutionsForCell(singleChoicesFilled, x, y) == 0} ||
        !isSudokuRepetitionFree(singleChoicesFilled)
      lazy val sudokuIsSolved = isSudokuSolved(singleChoicesFilled)
      if (sudokuIsSolved) {
        List(singleChoicesFilled)
      } else if (sudokuIsUnsolvable) {
        calcLogicalNextSteps(restSudokus)
      } else {
        val cellToFill = emptyCells.minBy((x, y) => numOfPossibleSolutionsForCell(singleChoicesFilled, x, y))
        calcLogicalNextSteps(calcNextSteps(singleChoicesFilled, cellToFill._1, cellToFill._2) ++ restSudokus)
      }
  }

  def finishSudoku(sudoku: Sudoku): Option[Sudoku] = {
    if (isSudokuValid(sudoku) && isSudokuRepetitionFree(sudoku))
      calcLogicalNextSteps(List(sudoku)).headOption
    else None
  }

}
