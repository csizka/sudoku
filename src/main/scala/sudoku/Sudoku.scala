package junicamp
package sudoku

import Examples.*
import java.nio.file.{Files, Paths, Path}
import scala.jdk.CollectionConverters._
import java.awt.event.KeyEvent

import scala.util.Random

type Cell = Option[Int]
type Row = Vector[Cell]
type Column = Vector[Cell]
type Block = Vector[Cell]
case class Sudoku(rows: Vector[Row]) {
  def insert(rowIx: Int, colIx: Int, value: Int): Sudoku = {
    Sudoku(rows.updated(rowIx, rows(rowIx).updated(colIx, Some(value))))
  }
  def delete(rowIx: Int, colIx: Int): Sudoku = {
    Sudoku(rows.updated(rowIx, rows(rowIx).updated(colIx, None)))
  }

  // TODO: make deterministic by instantiating scala.util.Random with a predefined seed (also add seed param for this fun)
  def deleteRandomCell(): Sudoku = {
    def deleteHelper(rowIx: Int, colIx: Int, value: Option[Int]): Sudoku = {
      Sudoku(rows.updated(rowIx, rows(rowIx).updated(colIx, value)))
    }
    deleteHelper(Random.nextInt(9), Random.nextInt(9), None)
  }
}

object Sudoku {
  def serialize(sudoku: Sudoku): List[String] =
    sudoku.rows.map { row =>
      row.map(_.fold(".")(x => x.toString)).mkString
    }.toList

  def isPrintableChar(c: Char) =
    !Character.isISOControl(c) &&
      c != KeyEvent.CHAR_UNDEFINED &&
      Option(Character.UnicodeBlock.of(c)).fold(false)(
        _ ne Character.UnicodeBlock.SPECIALS)

  def deserialize(serializedSudoku: List[String]): Sudoku = {
    Sudoku(serializedSudoku.map {
      _.stripSuffix("\n").map {
        case '.' => None
        case x => Some(x.toInt - 48)
      }.toVector
    }.toVector)
  }

  val path = Paths.get("./test.txt")
  val lines = Files.readAllLines(path).asScala
  val wholeFileAsString = Files.readString(path)
  val bytes = Files.readAllBytes(path).toList

  def save(sudoku: Sudoku, path: Path): Unit = {
    Files.write(path, serialize(sudoku).asJava)
  }

  def load(path: Path): Sudoku = {
    Sudoku(
        Files.readAllLines(path).asScala.map {
          _.map {
            case '.' => None
            case x => Some(x.toInt - 48)
          }.toVector
        }.toVector
    )
  }

  def giveHint(sudoku: Sudoku): Unit = {

  }

}
