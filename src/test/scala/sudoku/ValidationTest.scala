package junicamp
package sudoku

import sudoku.Examples.*
import sudoku.Validation.*

import utest.*

object ValidationTest extends TestSuite {
  val tests = Tests {
    test("areCellsSolved") {
      val filledButInvalidRow = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map(x => Some(x))
      assert(!areCellsSolved(filledButInvalidRow))
    }

    test("countSolutions") {
      countSolutions(oneCellMissingSudoku) ==> 1
      countSolutions(partiallySolvedGoodSudoku) ==> 1
      countSolutions(goodSudoku) ==> 1
    }
  }
}
