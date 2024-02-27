package junicamp
package sudoku

import Sudoku.*
import Examples.*
import Pretty.*
import sudoku.Validation.*

import Generate.*
import Solving.*

import scala.util.Random
import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters._

@main
def main(): Unit = {
  val path = Paths.get("./test.txt")
  Files.write(path, List("Hello world!", "second line").asJava)

  val lines = Files.readAllLines(path).asScala
  println(lines)

  val wholeFileAsString = Files.readString(path)
  println(wholeFileAsString)

  val bytes = Files.readAllBytes(path).toList
  println(bytes)
}