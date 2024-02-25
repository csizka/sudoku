package junicamp
package sudoku

import Examples.*

import scala.util.Random

type Cell = Option[Int]
type Row = Vector[Cell]
type Column = Vector[Cell]
type Block = Vector[Cell]
case class Sudoku(rows: Vector[Row]) {
  def insert(rowIx: Int, colIx: Int, value: Int): Sudoku = {
    Sudoku(rows.updated(rowIx, rows(rowIx).updated(colIx, Some(value))))
  }

  def deleteRandomCell(): Sudoku = {
    def deleteHelper(rowIx: Int, colIx: Int, value: Option[Int]): Sudoku = {
      Sudoku(rows.updated(rowIx, rows(rowIx).updated(colIx, value)))
    }
    deleteHelper(Random.nextInt(9), Random.nextInt(9), None)
  }
}

object Sudoku {
}
