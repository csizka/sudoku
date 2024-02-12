package junicamp.sudoku

import Examples.*

type Cell = Option[Int]
type Row = Vector[Cell]
type Column = Vector[Cell]
type Block = Vector[Cell]
case class Sudoku(rows: Vector[Row])

object Sudoku {
}
