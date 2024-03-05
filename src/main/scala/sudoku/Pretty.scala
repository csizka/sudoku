package junicamp
package sudoku

import Sudoku.*

//noinspection ScalaWeakerAccess
object Pretty {

  val firstRowPretty: String =  "╔═1═╤═2═╤═3═╦═4═╤═5═╤═6═╦═7═╤═8═╤═9═╗"
  val singleRowPretty: String = "╟───┼───┼───╫───┼───┼───╫───┼───┼───╢"
  val doubleRowPretty: String = "╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣"
  val lastRowPretty: String =   "╚═1═╧═2═╧═3═╩═4═╧═5═╧═6═╩═7═╧═8═╧═9═╝"

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
    " " + firstRowPretty + " ",
    "1" + prettyRow(sudoku.rows(0)) + "1",
    " " + singleRowPretty + " ",
    "2" + prettyRow(sudoku.rows(1)) + "2",
    " " + singleRowPretty + " ",
    "3" + prettyRow(sudoku.rows(2)) + "3",
    " " + doubleRowPretty + " ",
    "4" + prettyRow(sudoku.rows(3)) + "4",
    " " + singleRowPretty + " ",
    "5" + prettyRow(sudoku.rows(4)) + "5",
    " " + singleRowPretty + " ",
    "6" + prettyRow(sudoku.rows(5)) + "6",
    " " + doubleRowPretty + " ",
    "7" + prettyRow(sudoku.rows(6)) + "7",
    " " + singleRowPretty + " ",
    "8" + prettyRow(sudoku.rows(7)) + "8",
    " " + singleRowPretty + " ",
    "9" + prettyRow(sudoku.rows(8)) + "9",
    " " + lastRowPretty + " ",
  ).mkString("\n")



}
