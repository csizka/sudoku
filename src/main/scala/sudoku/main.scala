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
//println(pretty(generateControlledSudoku(Vector((0,0), (2,6), (3,1), (4,4), (7,5), (8,8)))))

playSudoku()
//println(prettyColours(goodSudoku, GREEN,Vector((1,1))))
//println(pretty(prettyColourRow(prettyList(goodSudoku), 8, Set(1,6,7,8), YELLOW)))
//println(prettyColourRow(prettyList(goodSudoku), 8, Set(1,2,3,4,9), YELLOW))
//val testMap: Map[Int, Set[Int]] = Map((0, Set(0,1,2,3,4,5,7,8,6)), (1, Set(4,5,6,7)), (2, Set(8)),
//  (3, Set(3,4,5,6,7,8)), (4, Set(0,1,6,8)), (5, Set(0,3,6,8)), (6, Set(0,4,6,8)), (7, Set(0,5,6,8)), (8, Set(0,2,6,8)))
//println(prettyColours(goodSudoku, YELLOW, testMap))
//println("\u001B[31m1\u001B[0m|2|\u001B[31m3\u001B[0m|4║\u001B[31m5\u001B[0m|6║\u001B[31m7\u001B[0m|8|\u001B[31m9\u001B[0m2\u001B[31m1\u001B[0m3" +
//    "\u001B[31m1\u001B[0m2|\u001B[31m3|\u001B[0m4║\u001B[31m5║\u001B[0m6║\u001B[31m7║\u001B[0m8\u001B[31m9\u001B[0m2\u001B[31m1\u001B[0m")
//println(allRepetitiveCoords(notGoodSudoku))
//  println(prettyColours(notGoodSudoku, RED, repetitionsByRows(notGoodSudoku)))
}

