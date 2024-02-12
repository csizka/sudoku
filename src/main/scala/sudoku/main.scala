package junicamp.sudoku

import Sudoku.*
import Examples.*
import Pretty.*
import Validation.*

//TODO assert istead of prinln
@main
def main(): Unit = {
  println(areCellsSolved(Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map(x => Some(x))))
  println(allRowsSolved(goodSudoku))
  println(areCellsFilled(Vector(1, 2, 3, 4, 5, 6, 7, 8).map(x => Some(x)) :+ Some(1)))
  println(areCellsRepetitionFree(goodSudoku.rows(0)))
  println(areCellsRepetitionFree(Vector(1, 2, 3, 4, 5, 6, 9, 8, 9).map(x => Some(x))))
  println(areCellsValid(Vector(1, 2, 3, 4, 5, 6, 7, 8, 10).map(x => Some(x)) :+ None))
  println(pretty(goodSudoku))
  println(getNthBlockV2(goodSudoku, 8))
  println(isSudokuRepetitionFree(goodSudoku))
  println(isSudokuRepetitionFree(notGoodSudoku))
  println(isSudokuRepetitionFree(testSudoku1))




}