package junicamp.sudoku
import Examples.*
import junicamp.sudoku.Sudoku.{doublePrettyRow, singlePrettyRow}

type Cell = Option[Int]
type Row = Vector[Cell]
case class Sudoku(rows: Vector[Row])

object Sudoku {
  def prettyCell(cell: Cell): String =  cell match {
    case Some(x) => x.toString
    case None => " "
  }

  def prettyRow(r:Row): String = List(
    r
      .take(3)
      .map(prettyCell)
      .mkString("║ ", " | " , " ║"),
    r
      .slice(3,6)
      .map(prettyCell)
      .mkString(" ", " | " , " "),
    r
      .slice(6,9)
      .map(prettyCell)
      .mkString("║ ", " | ", " ║"),
  ).mkString

  def pretty(sudoku: Sudoku): String = List(
    firstPrettyRow,
    prettyRow(sudoku.rows(0)),
    singlePrettyRow,
    prettyRow(sudoku.rows(1)),
    singlePrettyRow,
    prettyRow(sudoku.rows(2)),
    doublePrettyRow,
    prettyRow(sudoku.rows(3)),
    singlePrettyRow,
    prettyRow(sudoku.rows(4)),
    singlePrettyRow,
    prettyRow(sudoku.rows(5)),
    doublePrettyRow,
    prettyRow(sudoku.rows(6)),
    singlePrettyRow,
    prettyRow(sudoku.rows(7)),
    singlePrettyRow,
    prettyRow(sudoku.rows(8)),
    lastPrettyRow,
  ).mkString("\n")


  val firstPrettyRow: String = "╔═══╤═══╤═══╦═══╤═══╤═══╦═══╤═══╤═══╗"
  val singlePrettyRow: String = "╟───┼───┼───╫───┼───┼───╫───┼───┼───╢"
  val doublePrettyRow: String = "╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣"
  val lastPrettyRow: String = "╚═══╧═══╧═══╩═══╧═══╧═══╩═══╧═══╧═══╝"
  val prettyTest = pretty(testSudoku1)




   //List("asd", "qwe", "foo", "bar").mkString("[", " | ", "]")
//    "╔═══╤═══╤═══╦═══╤═══╤═══╦═══╤═══╤═══╗" + "\n" +
//    " kjc "


}

/*

╔═══╤═══╤═══╦═══╤═══╤═══╦═══╤═══╤═══╗
║ 8 │ 5 │   ║   │   │ 2 ║ 4 │   │   ║
╟───┼───┼───╫───┼───┼───╫───┼───┼───╢
║ 7 │ 2 │   ║   │   │   ║   │   │ 9 ║
╟───┼───┼───╫───┼───┼───╫───┼───┼───╢
║   │   │ 4 ║   │   │   ║   │   │   ║
╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣
║   │   │   ║ 1 │   │ 7 ║   │   │ 2 ║
╟───┼───┼───╫───┼───┼───╫───┼───┼───╢
║ 3 │   │ 5 ║   │   │   ║ 9 │   │   ║
╟───┼───┼───╫───┼───┼───╫───┼───┼───╢
║   │ 4 │   ║   │   │   ║   │   │   ║
╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣
║   │   │   ║   │ 8 │   ║   │ 7 │   ║
╟───┼───┼───╫───┼───┼───╫───┼───┼───╢
║   │ 1 │ 7 ║   │   │   ║   │   │   ║
╟───┼───┼───╫───┼───┼───╫───┼───┼───╢
║   │   │   ║   │ 3 │ 6 ║   │ 4 │   ║
╚═══╧═══╧═══╩═══╧═══╧═══╩═══╧═══╧═══╝


*/