package junicamp
package sudoku

import sudoku.Examples.*

import scala.util.Random

type Cell = Option[Int]
type Row = Vector[Cell]
type Column = Vector[Cell]
type Block = Vector[Cell]
type Coord = (Int, Int)
type CellHistory = Vector[(Int, Int, Cell)]

case class Sudoku(rows: Vector[Row]) {
  def update(rowIx: Int, colIx: Int, value: Cell): Sudoku = {
    Sudoku(rows.updated(rowIx, rows(rowIx).updated(colIx, value)))
  }
  def insert(rowIx: Int, colIx: Int, value: Int): Sudoku = {
    update(rowIx, colIx, Some(value))
  }
  def delete(rowIx: Int, colIx: Int): Sudoku = {
    update(rowIx, colIx, None)
  }
}



