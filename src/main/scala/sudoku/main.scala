package junicamp
package sudoku

import sudoku.Examples.*
import sudoku.Generate.*
import sudoku.Misc.*
import sudoku.PlaySudoku.*
import sudoku.Pretty.*
import sudoku.Sudoku.*

import scala.annotation.tailrec
import scala.collection.immutable.Set
import scala.io.AnsiColor.*
import scala.io.StdIn.*
import scala.util.Random


@main
def main(): Unit = {
playSudoku()

}

