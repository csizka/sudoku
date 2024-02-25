package junicamp
package sudoku

import sudoku.Validation._

import utest._

object ValidationTest {
  def main(args: Array[String]) = {
    assert(!areCellsSolved(Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map(x => Some(x))))
  }
}
