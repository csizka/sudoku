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
  println(pretty(goodSudoku))
  assert(getNthBlockV2(goodSudoku, 8) == Vector(Some(2), Some(8), Some(4), Some(6), Some(3), Some(5), Some(1), Some(7), Some(9)))
  assert(isSudokuRepetitionFree(goodSudoku))
  assert(!isSudokuRepetitionFree(notGoodSudoku))
  assert(!isSudokuRepetitionFree(testSudoku1))
  assert(isSudokuSolved(goodSudoku))
  assert(isSudokuValid(goodSudoku))
  assert(isSudokuValid(notGoodSudoku))
  assert(!isSudokuSolved(notGoodSudoku))



}