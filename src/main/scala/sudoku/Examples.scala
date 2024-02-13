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
  val goodSudoku = Sudoku(
    Vector(
      Vector(5, 3, 4, 6, 7, 8, 9, 1, 2).map(x => Some(x)),
      Vector(6, 7, 2, 1, 9, 5, 3, 4, 8).map(x => Some(x)),
      Vector(1, 9, 8, 3, 4, 2, 5, 6, 7).map(x => Some(x)),
      Vector(8, 5, 9, 7, 6, 1, 4, 2, 3).map(x => Some(x)),
      Vector(4, 2, 6, 8, 5, 3, 7, 9, 1).map(x => Some(x)),
      Vector(7, 1, 3, 9, 2, 4, 8, 5, 6).map(x => Some(x)),
      Vector(9, 6, 1, 5, 3, 7, 2, 8, 4).map(x => Some(x)),
      Vector(2, 8, 7, 4, 1, 9, 6, 3, 5).map(x => Some(x)),
      Vector(3, 4, 5, 2, 8, 6, 1, 7, 9).map(x => Some(x)),
    )
  )

  val notGoodSudoku = Sudoku(
    Vector(
      Vector(5, 3, 4, 6, 7, 8, 9, 1, 2).map(x => Some(x)),
      Vector(6, 7, 2, 1, 9, 5, 3, 4, 8).map(x => Some(x)),
      Vector(1, 9, 8, 3, 4, 2, 5, 6, 7).map(x => Some(x)),
      Vector(8, 5, 9, 7, 6, 1, 4, 2, 3).map(x => Some(x)),
      Vector(4, 2, 6, 8, 5, 3, 7, 9, 1).map(x => Some(x)),
      Vector(7, 1, 3, 9, 2, 4, 8, 5, 6).map(x => Some(x)),
      Vector(9, 6, 1, 5, 3, 7, 2, 8, 4).map(x => Some(x)),
      Vector(2, 8, 7, 4, 1, 9, 6, 5, 5).map(x => Some(x)),
      Vector(3, 4, 5, 2, 8, 6, 1, 7, 9).map(x => Some(x)),
    )
  )

  val partiallySolvedGoodSudoku = Sudoku(
    Vector(
      Vector(Some(5), None, Some(4), Some(6), Some(7), None, None, Some(1), None),
      Vector(Some(6), Some(7), None, None, None, None, Some(3), Some(4), Some(8)),
      Vector(Some(1), None, None, Some(3), None, None, Some(5), Some(6), None),
      Vector(None, Some(5), None, None, Some(6), Some(1), Some(4), None, None),
      Vector(None, Some(2), None, Some(8), Some(5), Some(3), Some(7), None, Some(1)),
      Vector(Some(7), Some(1), None, Some(9), Some(2), Some(4), None, None, Some(6)),
      Vector(Some(9), None, Some(1), None, Some(3), None, Some(2), None, Some(4)),
      Vector(2, 8, 7, 4, 1, 9, 6, 3, 5).map(x => Some(x)),
      Vector(None,None, Some(5), Some(2), Some(8), None, Some(1), Some(7), Some(9)),
    )
  )
}
