package junicamp.sudoku

import Examples.*

type Cell = Option[Int]
type Row = Vector[Cell]
type Column = Vector[Cell]
type Block = Vector[Cell]
case class Sudoku(rows: Vector[Row]) {
  def insert(rowIx: Int, colIx: Int, value: Int): Sudoku = {
    Sudoku(rows.updated(rowIx, rows(rowIx).updated(colIx, Some(value))))
  }
}

object Sudoku {
}
