package junicamp
package sudoku

import Sudoku.*

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

  def prettyList(sudoku: Sudoku): List[String] = {
    List(
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
    )
  }

  def pretty(sudoku: Sudoku): String = prettyList(sudoku).mkString("\n")

  def pretty(lst: List[String]): String = lst.mkString("\n")

  def prettyColourRow(lst: List[String], row: Int, cols: Vector[Int], colour:String): List[String] = {
    val rowIX = row * 2 + 1
    val orderedCols = cols.sorted
    val end = "\u001B[0m"
    val colourLen = colour.length + end.length
    val initLen = lst(rowIX).length
    val initIX = 0
    val (finalList, finalLen, finalIX) =
      orderedCols.foldLeft{(lst, lst(rowIX).length, initIX)} { case ((curList, curLen, curIX), curCoord) =>
      val charIX = (curCoord + 1) * 4 + curIX * colourLen - 1
      val nextRow = curList(rowIX).take(charIX) + colour + curList(rowIX).slice(charIX, charIX + 1) +
        "\u001b[0m" + curList(rowIX).slice(charIX + 1, curLen)
      val nextList = curList.updated(rowIX, nextRow)
      val nextLen = curLen + colourLen
      val nextIX = curIX + 1
      (nextList, nextLen, nextIX)
    }
    finalList
  }

  def prettyColours(sudoku: Sudoku, colour: String, coords: Map[Int, Set[Int]]): String = {
    val list = prettyList(sudoku)
    val finalList = coords.foldLeft(list){ case (curList, curCoord) =>
      val (rowIX, cols) = curCoord
      val colVector = cols.toVector
      val nextList = prettyColourRow(curList, rowIX, colVector, colour)
      nextList
    }
    finalList.mkString("\n")
  }


}
