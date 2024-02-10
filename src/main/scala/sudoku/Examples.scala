package junicamp.sudoku

object Examples {

  val row1To9: Row = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9).map(x => Some(x))

  val emptyRow: Row = Vector.fill(9)(None)

  val testSudoku1 = Sudoku(
    Vector.fill(9)(row1To9)
  )

  val emptySudoku = Sudoku(
    Vector.fill(9)(emptyRow)
  )

}
