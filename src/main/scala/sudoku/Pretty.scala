package junicamp.sudoku

import Sudoku.*

object Pretty {

  val firstRowPretty: String =  "╔═══╤═══╤═══╦═══╤═══╤═══╦═══╤═══╤═══╗"
  val singleRowPretty: String = "╟───┼───┼───╫───┼───┼───╫───┼───┼───╢"
  val doubleRowPretty: String = "╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣"
  val lastRowPretty: String =   "╚═══╧═══╧═══╩═══╧═══╧═══╩═══╧═══╧═══╝"

  def prettyCell(cell: Cell): String = cell match {
    case Some(x) => x.toString
    case None => " "
  }

  def prettyRow(r: Row): String = List(
    r
      .take(3)
      .map(prettyCell)
      .mkString("║ ", " | ", " ║"),
    r
      .slice(3, 6)
      .map(prettyCell)
      .mkString(" ", " | ", " "),
    r
      .slice(6, 9)
      .map(prettyCell)
      .mkString("║ ", " | ", " ║"),
  ).mkString

  def pretty(sudoku: Sudoku): String = List(
    firstRowPretty,
    prettyRow(sudoku.rows(0)),
    singleRowPretty,
    prettyRow(sudoku.rows(1)),
    singleRowPretty,
    prettyRow(sudoku.rows(2)),
    doubleRowPretty,
    prettyRow(sudoku.rows(3)),
    singleRowPretty,
    prettyRow(sudoku.rows(4)),
    singleRowPretty,
    prettyRow(sudoku.rows(5)),
    doubleRowPretty,
    prettyRow(sudoku.rows(6)),
    singleRowPretty,
    prettyRow(sudoku.rows(7)),
    singleRowPretty,
    prettyRow(sudoku.rows(8)),
    lastRowPretty,
  ).mkString("\n")

}
