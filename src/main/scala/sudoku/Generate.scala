package junicamp
package sudoku

import sudoku.Examples.*
import sudoku.Solving.*
import sudoku.Sudoku.*
import sudoku.Validation.*

import scala.annotation.tailrec
import scala.util.Random

object Generate {

  val rand = new Random(System.nanoTime())

  def generateSolvedSudoku(row: Int, column: Int): Sudoku = {
    val startSudoku = emptySudoku.insert(row, column, rand.nextInt(9) + 1)
    finishSudoku(startSudoku).get
  }

  def generateControlledSudoku(coords: Vector[(Int, Int)]): Sudoku = {
    val startSudoku = coords.foldLeft(emptySudoku) { case (curSudoku, (rowIx, colIx)) =>
      curSudoku.insert(rowIx, colIx, rand.nextInt(9) + 1)
    }
    finishSudoku(startSudoku).get
  }

  def deleteRandomCell(sudoku: Sudoku): Sudoku = {
    def deleteHelper(rowIx: Int, colIx: Int, value: Option[Int]): Sudoku = {
      Sudoku(sudoku.rows.updated(rowIx, sudoku.rows(rowIx).updated(colIx, value)))
    }

    deleteHelper(rand.nextInt(9), rand.nextInt(9), None)
  }

  def generateEasySudoku(sudoku: Sudoku): Sudoku = {
    @tailrec
    def easyHelper(sudoku: Sudoku): Sudoku = {
      val curSudoku = deleteRandomCell(sudoku)
      if (numOfEmptyCells(curSudoku) <= 20 && countSolutions(curSudoku) == 1) easyHelper(curSudoku)
      else sudoku
    }

    easyHelper(sudoku)
  }

  def generateMediumSudoku(sudoku: Sudoku): Sudoku = {
    @tailrec
    def mediumHelper(sudoku: Sudoku): Sudoku = {
      val curSudoku = deleteRandomCell(sudoku)
      if (numOfEmptyCells(curSudoku) <= 30 && countSolutions(curSudoku) == 1) mediumHelper(curSudoku)
      else sudoku
    }

    mediumHelper(deleteRandomCell(deleteRandomCell(sudoku)))
  }

  def generateHardSudoku(sudoku: Sudoku): Sudoku = {
    @tailrec
    def hardHelper(sudoku: Sudoku): Sudoku = {
      val curSudoku = deleteRandomCell(sudoku)
      if (countSolutions(curSudoku) == 1) hardHelper(curSudoku)
      else sudoku
    }
    hardHelper(deleteRandomCell(deleteRandomCell(sudoku)))
  }

  def countSolutions(sudoku: Sudoku): Int = {
    @tailrec
    def countSudokuHelper(lstOfSudoku: List[Sudoku], resSet: Set[Sudoku]): Int = lstOfSudoku match {
      case curSudoku :: restSudokus =>
        val singleChoicesFilled = fillCellsWithSingleChoicesRepeatedly(curSudoku)
        val emptyCells = collectEmptyCellCoords(singleChoicesFilled)
        val sudokuIsUnsolvable = emptyCells.exists{ case (x, y) => numOfPossibleSolutionsForCell(singleChoicesFilled, x, y) == 0} ||
          !isSudokuRepetitionFree(singleChoicesFilled)
        if (isSudokuSolved(singleChoicesFilled))
          countSudokuHelper(restSudokus, resSet + singleChoicesFilled)
        else if (!sudokuIsUnsolvable)
            val cellToFill = emptyCells.minBy((x, y) => numOfPossibleSolutionsForCell(singleChoicesFilled, x, y))
            countSudokuHelper(calcNextSteps(singleChoicesFilled, cellToFill._1, cellToFill._2) ++ restSudokus, resSet)
        else countSudokuHelper(restSudokus, resSet)
      case Nil => resSet.size
    }
    countSudokuHelper(List(sudoku), Set.empty[Sudoku])
  }
}
