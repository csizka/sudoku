package junicamp
package sudoku

import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters.*
import sudoku.Sudoku.*
import sudoku.Examples.*
import sudoku.Misc.*
import sudoku.Generate.*
import sudoku.PlaySudoku.*

import Pretty.*

import scala.annotation.tailrec
import scala.io.StdIn.*
import scala.io.AnsiColor._
import java.nio.charset.Charset
import scala.util.Random


@main
def main(): Unit = {
//PlaySudoku.main(Array())


  playSudoku()



}
