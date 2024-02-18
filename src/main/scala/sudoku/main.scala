package junicamp.sudoku

import Sudoku.*
import Examples.*
import Pretty.*
import Validation.*

//TODO assert istead of prinln
@main
def main(): Unit = {
  assert(!areCellsSolved(Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map(x => Some(x))))
  assert(allRowsSolved(goodSudoku))
  assert(areCellsFilled(Vector(1, 2, 3, 4, 5, 6, 7, 8).map(x => Some(x)) :+ Some(1)))
  assert(areCellsRepetitionFree(goodSudoku.rows(0)))
  assert(!areCellsRepetitionFree(Vector(1, 2, 3, 4, 5, 6, 9, 8, 9).map(x => Some(x))))
  assert(!areCellsValid(Vector(1, 2, 3, 4, 5, 6, 7, 8, 10).map(x => Some(x)) :+ None))
  // println(pretty(goodSudoku))
  assert(getNthBlockV2(goodSudoku, 8) == Vector(Some(2), Some(8), Some(4), Some(6), Some(3), Some(5), Some(1), Some(7), Some(9)))
  assert(isSudokuRepetitionFree(goodSudoku))
  assert(!isSudokuRepetitionFree(notGoodSudoku))
  assert(!isSudokuRepetitionFree(testSudoku1))
  assert(isSudokuSolved(goodSudoku))
  assert(isSudokuValid(goodSudoku))
  assert(isSudokuValid(notGoodSudoku))
  assert(!isSudokuSolved(notGoodSudoku))
  // println(pretty(partiallySolvedGoodSudoku))
  assert(possibleSolutionsForCell(partiallySolvedGoodSudoku, 6,1) == Set(6))
  assert(possibleSolutionsForCell(partiallySolvedGoodSudoku, 0,5) == Set(2, 8))
  assert(possibleSolutionsForCell(partiallySolvedGoodSudoku, 3, 8) == Set(2,3))
  assert(Sudoku(
    Vector(
      Vector(Some(5), None, Some(4), Some(6), Some(7), None, None, Some(1), None),
      Vector(Some(6), Some(7), None, None, None, None, Some(3), Some(4), Some(8)),
      Vector(Some(1), None, None, Some(3), None, None, Some(5), Some(6), None),
      Vector(None, Some(5), None, None, Some(6), Some(1), Some(4), None, None),
      Vector(None, Some(2), None, Some(8), Some(5), Some(3), Some(7), None, Some(1)),
      Vector(Some(7), Some(1), None, Some(9), Some(2), Some(4), None, None, Some(6)),
      Vector(Some(9), None, Some(1), None, Some(3), None, Some(2), None, Some(4)),
      Vector(2, 8, 7, 4, 1, 9, 6, 3, 5).map(x => Some(x)),
      Vector(None, None, Some(5), Some(2), Some(8), None, Some(1), Some(7), Some(9)),
    )).insert(0, 1, 3) == Sudoku(
      Vector(
        Vector(Some(5), Some(3), Some(4), Some(6), Some(7), None, None, Some(1), None),
        Vector(Some(6), Some(7), None, None, None, None, Some(3), Some(4), Some(8)),
        Vector(Some(1), None, None, Some(3), None, None, Some(5), Some(6), None),
        Vector(None, Some(5), None, None, Some(6), Some(1), Some(4), None, None),
        Vector(None, Some(2), None, Some(8), Some(5), Some(3), Some(7), None, Some(1)),
        Vector(Some(7), Some(1), None, Some(9), Some(2), Some(4), None, None, Some(6)),
        Vector(Some(9), None, Some(1), None, Some(3), None, Some(2), None, Some(4)),
        Vector(2, 8, 7, 4, 1, 9, 6, 3, 5).map(x => Some(x)),
        Vector(None, None, Some(5), Some(2), Some(8), None, Some(1), Some(7), Some(9)),
      )))
  assert(numOfPossibleSolutionsForCell(someCellMissingSudoku,0,5) == 1)
  assert(numOfEmptyCells(oneCellMissingSudoku) == 1)
  assert(numOfEmptyCells(someCellMissingSudoku) == 4)
  // println(pretty(someCellMissingSudoku))
  assert(numOfPossibleSolutionsForCell(someCellMissingSudoku,0,5) == 1)
  assert(numOfPossibleSolutionsForCell(someCellMissingSudoku,8,0) == 1)
  assert(numOfPossibleSolutionsForCell(someCellMissingSudoku,8,1) == 1)
  assert(numOfPossibleSolutionsForCell(someCellMissingSudoku,8,5) == 1)
  assert(sumOfPossibleSolutionsForAllCells(someCellMissingSudoku) == 4)
  assert(sumOfPossibleSolutionsForAllCells(oneCellMissingSudoku) == 1)
  assert(calcLogicalNextSteps(List(someCellMissingSudoku)) == List(goodSudoku))
  assert(calcLogicalNextSteps(List(oneCellMissingSudoku)) == List(goodSudoku))
  assert(finishSudoku(someCellMissingSudoku) == goodSudoku)
  assert(finishSudoku(oneCellMissingSudoku) == goodSudoku)
  assert(finishSudoku(partiallySolvedGoodSudoku) == goodSudoku)
  println(finishSudoku(partiallySolvedGoodSudoku))
  try {finishSudoku(notGoodSudoku) } catch {
    case e: IllegalArgumentException => println("not solvable Sudoku")
  }



}