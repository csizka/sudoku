package junicamp
package sudoku

object Pretty {
  
  val firstRowPretty =  " ╔═1═╤═2═╤═3═╦═4═╤═5═╤═6═╦═7═╤═8═╤═9═╗ "
  val singleRowPretty = " ╟───┼───┼───╫───┼───┼───╫───┼───┼───╢ "
  val doubleRowPretty = " ╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣ "
  val lastRowPretty =   " ╚═1═╧═2═╧═3═╩═4═╧═5═╧═6═╩═7═╧═8═╧═9═╝ "


  def prettyCell(cell: Cell): String = cell match {
    case Some(x) => x.toString
    case None => " "
  }

  def prettyColouredCell(cell: Cell, colour: String, reset: String = "\u001B[0m"): String = cell match {
    case Some(x) => colour + x.toString + reset
    case None => " "
  }

  def prettyRow(r: Row, cellsToColour: Set[Int] = Set(), colour: String = "\u001B[31m"): String = {
    def prettifyCell(ixs: List[Int], start: String, middle: String, end: String): String =
      ixs.map {
        case index if cellsToColour.contains(index) => prettyColouredCell(r(index), colour)
        case index => prettyCell(r(index))
      }.mkString(start, middle, end)

    List(
      prettifyCell(List(0, 1, 2), "║ ", " | ", " ║"),
      prettifyCell(List(3, 4, 5), " ", " | ", " "),
      prettifyCell(List(6, 7, 8), "║ ", " | ", " ║")
    ).mkString
  }

  def pretty(sudoku: Sudoku, cellsToColour: Map[Int, Set[Int]] = Map(), colour: String = "\u001B[31m"): String = {
    def fullPrettyRow(ix: Int): String =
      s"${ix + 1}" + prettyRow(sudoku.rows(ix), cellsToColour.getOrElse(ix, Set()), colour) + s"${ix + 1}"

    val frameRowsWithoutLast: Vector[String] = Vector(
      firstRowPretty,
      doubleRowPretty,
      doubleRowPretty,
    ).flatMap ( row => Vector(row, singleRowPretty, singleRowPretty))

    frameRowsWithoutLast
      .zipWithIndex
      .flatMap { case (elem, ix) => Vector(elem, fullPrettyRow(ix)) }
      .appended(lastRowPretty)
      .mkString("\n")
  }

  def printColoredMsg(color: String, msg: String): Unit =
    println(color + msg + "\u001b[0m")
}