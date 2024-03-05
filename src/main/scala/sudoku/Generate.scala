package junicamp
package sudoku

import sudoku.Validation.*
import sudoku.Solving.*
import sudoku.Sudoku.*
import sudoku.Examples.*

import junicamp.sudoku.Generate.countSolutions

import scala.util.Random
import scala.annotation.tailrec

// TODO: make generation deterministic by adding seeds to functions
// val rnd = new Random(rndSeed)
// val rdnNum - rnd.nextInt
object Generate {

  def generateSolvedSudoku(): Sudoku = {
    val startSudoku = emptySudoku.insert(Random.nextInt(9), Random.nextInt(9), Random.nextInt(8) + 1)

    finishSudoku(startSudoku).get
  }
  def generateNonRandomSolvedSudoku(row: Int, column: Int): Sudoku = {
    val startSudoku = emptySudoku.insert(row, column, Random.nextInt(8) + 1)
    finishSudoku(startSudoku).get
  }
  def generateEasySudoku(sudoku: Sudoku): Sudoku = {
    @tailrec
    def easyHelper(sudoku: Sudoku): Sudoku = {
      val curSudoku = sudoku.deleteRandomCell()
      if (numOfEmptyCells(curSudoku) <= 20 && countSolutions(curSudoku) == 1) easyHelper(curSudoku)
      else sudoku
    }

    easyHelper(sudoku)
  }

  def generateMediumSudoku(sudoku: Sudoku): Sudoku = {
    @tailrec
    def mediumHelper(sudoku: Sudoku): Sudoku = {
      val curSudoku = sudoku.deleteRandomCell()
      if (numOfEmptyCells(curSudoku) <= 30 && countSolutions(curSudoku) == 1) mediumHelper(curSudoku)
      else sudoku
    }

    mediumHelper(sudoku.deleteRandomCell().deleteRandomCell())
  }

  def generateHardSudoku(sudoku: Sudoku): Sudoku = {
    @tailrec
    def hardHelper(sudoku: Sudoku): Sudoku = {
      val curSudoku = sudoku.deleteRandomCell()
      if (numOfEmptyCells(curSudoku) <= 40 && countSolutions(curSudoku) == 1) hardHelper(curSudoku)
      else sudoku
    }
    hardHelper(sudoku.deleteRandomCell().deleteRandomCell())
  }
  def generateTheHardestSudoku(sudoku: Sudoku): Sudoku = {
    @tailrec
    def hardestHelper(sudoku: Sudoku): Sudoku = {
      val curSudoku = sudoku.deleteRandomCell()
      if (numOfEmptyCells(curSudoku) <= 60 && countSolutions(curSudoku) == 1) hardestHelper(curSudoku)
      else sudoku
    }
    hardestHelper(sudoku.deleteRandomCell().deleteRandomCell())
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
