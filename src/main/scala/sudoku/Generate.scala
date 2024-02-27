package junicamp
package sudoku

import sudoku.Validation.*
import sudoku.Solving.*

import scala.annotation.tailrec

object Generate {
  def generateEasySudoku(sudoku: Sudoku): Sudoku = {
    def easyHelper(sudoku: Sudoku): Sudoku = {
      val curSudoku = sudoku.deleteRandomCell()
      if (countOfSingleChoiceCells(curSudoku) >= 6) generateEasySudoku(curSudoku)
      else sudoku
    }

    easyHelper(sudoku.deleteRandomCell().deleteRandomCell().deleteRandomCell().deleteRandomCell().deleteRandomCell().deleteRandomCell().deleteRandomCell())
  }

  def generateMediumSudoku(sudoku: Sudoku): Sudoku = {
    def mediumHelper(sudoku: Sudoku): Sudoku = {
      val curSudoku = sudoku.deleteRandomCell()
      if (countOfSingleChoiceCells(curSudoku) >= 1) generateEasySudoku(curSudoku)
      else sudoku
    }

    mediumHelper(sudoku.deleteRandomCell().deleteRandomCell())
  }

  def generateHardSudoku(sudoku: Sudoku): Sudoku = {
    def mediumHelper(sudoku: Sudoku): Sudoku = {
      val curSudoku = sudoku.deleteRandomCell()
      if (countOfSingleChoiceCells(curSudoku) > 0) generateEasySudoku(curSudoku)
      else sudoku
    }

    mediumHelper(sudoku.deleteRandomCell().deleteRandomCell())
  }

  def countSolutions(sudoku: Sudoku): Int = {
    @tailrec
    def countSudokuHelper(lstOfSudoku: List[Sudoku], resSet: Set[Sudoku]): Int = {
      if (lstOfSudoku.nonEmpty) {
        val curSudoku = lstOfSudoku.head
        val restSudokus = lstOfSudoku.drop(1)
        val cellsToFill = collectEmptyCellCoords(curSudoku).filter { case (x, y) => numOfPossibleSolutionsForCell(curSudoku, x, y) > 0 }
        if (cellsToFill.nonEmpty)
          val singleChoices = cellsToFill.filter((x, y) => numOfPossibleSolutionsForCell(curSudoku, x, y) == 1)
          if (singleChoices.nonEmpty)
            countSudokuHelper(fillCellsWithSingleChoices(curSudoku) +: restSudokus, resSet)
          else {
            val cellToFill = cellsToFill.minBy((x, y) => numOfPossibleSolutionsForCell(curSudoku, x, y))
            countSudokuHelper(calcNextSteps(curSudoku, cellToFill._1, cellToFill._2) ++ restSudokus, resSet)
          }
        else if (isSudokuSolved(curSudoku))
          countSudokuHelper(restSudokus, resSet + curSudoku)
        else countSudokuHelper(restSudokus, resSet)
      } else resSet.size
    }

    countSudokuHelper(List(sudoku), Set.empty[Sudoku])
  }

  def countOfSingleChoiceCells(sudoku: Sudoku): Int = {
    collectEmptyCellCoords(sudoku).count { case (x, y) => numOfPossibleSolutionsForCell(sudoku, x, y) == 1 }
  }
  
  
}
