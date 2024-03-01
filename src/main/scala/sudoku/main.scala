package junicamp
package sudoku

import java.nio.file.{Files, Paths, Path}
import scala.jdk.CollectionConverters.*
import sudoku.Sudoku.*
import sudoku.Examples.*
import Pretty.*


@main
def main(): Unit = {
  def testSavingAndReading(path: Path, sudoku: Sudoku): Unit = {
    save(sudoku, path)
    println("sudoku saved")
    println(pretty(load(path)))
    Files.delete(path)
    println("sudoku deleted")
  }

  testSavingAndReading(Paths.get("./testThenDelete.txt"), partiallySolvedGoodSudoku)

  println(serialize(partiallySolvedGoodSudoku))
  println(partiallySolvedGoodSudoku)
}