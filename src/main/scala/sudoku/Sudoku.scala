package junicamp.sudoku

import Examples.*

type Cell = Option[Int]
type Row = Vector[Cell]
case class Sudoku(rows: Vector[Row])

object Sudoku {
}
