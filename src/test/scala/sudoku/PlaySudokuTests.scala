package junicamp
package sudoku

import sudoku.Examples.*
import sudoku.Validation.*
import sudoku.Sudoku.*
import sudoku.Solving.*
import PlaySudoku.*

import Generate.*
import utest.*

import scala.jdk.CollectionConverters.*

object PlaySudokuTests extends TestSuite {
  val tests = Tests {
    test("execCommand") {
      test("invalidActions") {
        test("nullInstructions") {
          assertMatch(execCommand(someCellMissingSudoku, "", testingSudoku)) {
            case Left(err: String) if err.contains("understood") => ()
          }
        }
        test("nonExistentActions") {
          assertMatch(execCommand(someCellMissingSudoku, "z", testingSudoku)) {
            case Left(err: String) if err.contains("understood") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "k236", testingSudoku)) {
            case Left(err: String) if err.contains("understood") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "/123", testingSudoku)) {
            case Left(err: String) if err.contains("understood") => ()
          }
        }
      }
      test("insertion") {
        test("nonInsertable") {
          assertMatch(execCommand(someCellMissingSudoku, "i", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "ik36", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "i024", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "i34b", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "i34", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "i740", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
        }
        test("nonEmpty"){
          assertMatch(execCommand(someCellMissingSudoku, "i114", testingSudoku)) {
            case Left(err: String) if err.contains("only those cells") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "i528", testingSudoku)) {
            case Left(err: String) if err.contains("only those cells") => ()
          }
        }
        test("workingInsertion") {
          assertMatch(execCommand(testingSudoku, "i913", testingSudoku)) {
            case Right(Sudoku(
            Vector(
            Vector(Some(5), None, Some(4), None, Some(7), None, None, Some(1), None),
            Vector(Some(6), Some(7), None, None, None, None, Some(3), Some(4), Some(8)),
            Vector(Some(1), None, None, Some(3), None, None, Some(5), None, None),
            Vector(None, Some(5), None, None, None, Some(1), None, None, None),
            Vector(None, Some(2), None, Some(8), Some(5), None, Some(7), None, Some(1)),
            Vector(Some(7), None, None, Some(9), None, Some(4), None, None, Some(6)),
            Vector(Some(9), Some(6), Some(1), None, Some(3), Some(7), None, Some(8), None),
            Vector(None, None, None, None, None, None, None, None, None),
            Vector(Some(3), None, Some(5), None, Some(8), None, Some(1), Some(7), None)))) => ()
          }
          assertMatch(execCommand(testingSudoku, "i192", testingSudoku)) {
            case Right(Sudoku(
            Vector(
            Vector(Some(5), None, Some(4), None, Some(7), None, None, Some(1), Some(2)),
            Vector(Some(6), Some(7), None, None, None, None, Some(3), Some(4), Some(8)),
            Vector(Some(1), None, None, Some(3), None, None, Some(5), None, None),
            Vector(None, Some(5), None, None, None, Some(1), None, None, None),
            Vector(None, Some(2), None, Some(8), Some(5), None, Some(7), None, Some(1)),
            Vector(Some(7), None, None, Some(9), None, Some(4), None, None, Some(6)),
            Vector(Some(9), Some(6), Some(1), None, Some(3), Some(7), None, Some(8), None),
            Vector(None, None, None, None, None, None, None, None, None),
            Vector(None, None, Some(5), None, Some(8), None, Some(1), Some(7), None)))) => ()
          }
          assertMatch(execCommand(testingSudoku, "i589", testingSudoku)) {
            case Right(Sudoku(
            Vector(
            Vector(Some(5), None, Some(4), None, Some(7), None, None, Some(1), None),
            Vector(Some(6), Some(7), None, None, None, None, Some(3), Some(4), Some(8)),
            Vector(Some(1), None, None, Some(3), None, None, Some(5), None, None),
            Vector(None, Some(5), None, None, None, Some(1), None, None, None),
            Vector(None, Some(2), None, Some(8), Some(5), None, Some(7), Some(9), Some(1)),
            Vector(Some(7), None, None, Some(9), None, Some(4), None, None, Some(6)),
            Vector(Some(9), Some(6), Some(1), None, Some(3), Some(7), None, Some(8), None),
            Vector(None, None, None, None, None, None, None, None, None),
            Vector(None, None, Some(5), None, Some(8), None, Some(1), Some(7), None)))) => ()
          }
        }
      }
      test("deletion") {
        test("nonValidIxes") {
          assertMatch(execCommand(someCellMissingSudoku, "d", testingSudoku)) {
            case Left(err: String) if err.contains("2 numbers") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "d)4", testingSudoku)) {
            case Left(err: String) if err.contains("2 numbers") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "d40", testingSudoku)) {
            case Left(err: String) if err.contains("2 numbers") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "d03", testingSudoku)) {
            case Left(err: String) if err.contains("2 numbers") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "d5", testingSudoku)) {
            case Left(err: String) if err.contains("2 numbers") => ()
          }
        }
        test("nonEmpty") {
          assertMatch(execCommand(someCellMissingSudoku, "d11", testingSudoku)) {
            case Left(err: String) if err.contains("was empty at the beginning") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, "d52", testingSudoku)) {
            case Left(err: String) if err.contains("was empty at the beginning") => ()
          }
        }
        test("workingDeletion") {
          assertMatch(execCommand(testingSudoku, "d59", emptySudoku)) {
            case Right(Sudoku(
            Vector(
            Vector(Some(5), None, Some(4), None, Some(7), None, None, Some(1), None),
            Vector(Some(6), Some(7), None, None, None, None, Some(3), Some(4), Some(8)),
            Vector(Some(1), None, None, Some(3), None, None, Some(5), None, None),
            Vector(None, Some(5), None, None, None, Some(1), None, None, None),
            Vector(None, Some(2), None, Some(8), Some(5), None, Some(7), None, None),
            Vector(Some(7), None, None, Some(9), None, Some(4), None, None, Some(6)),
            Vector(Some(9), Some(6), Some(1), None, Some(3), Some(7), None, Some(8), None),
            Vector(None, None, None, None, None, None, None, None, None),
            Vector(None, None, Some(5), None, Some(8), None, Some(1), Some(7), None)))) => ()
          }
          assertMatch(execCommand(testingSudoku, "d18", emptySudoku)) {
            case Right(Sudoku(
            Vector(
            Vector(Some(5), None, Some(4), None, Some(7), None, None, None, None),
            Vector(Some(6), Some(7), None, None, None, None, Some(3), Some(4), Some(8)),
            Vector(Some(1), None, None, Some(3), None, None, Some(5), None, None),
            Vector(None, Some(5), None, None, None, Some(1), None, None, None),
            Vector(None, Some(2), None, Some(8), Some(5), None, Some(7), None, Some(1)),
            Vector(Some(7), None, None, Some(9), None, Some(4), None, None, Some(6)),
            Vector(Some(9), Some(6), Some(1), None, Some(3), Some(7), None, Some(8), None),
            Vector(None, None, None, None, None, None, None, None, None),
            Vector(None, None, Some(5), None, Some(8), None, Some(1), Some(7), None),
            ))) => ()
          }
          assertMatch(execCommand(testingSudoku, "d71", emptySudoku)) {
            case Right(Sudoku(
            Vector(
            Vector(Some(5), None, Some(4), None, Some(7), None, None, Some(1), None),
            Vector(Some(6), Some(7), None, None, None, None, Some(3), Some(4), Some(8)),
            Vector(Some(1), None, None, Some(3), None, None, Some(5), None, None),
            Vector(None, Some(5), None, None, None, Some(1), None, None, None),
            Vector(None, Some(2), None, Some(8), Some(5), None, Some(7), None, Some(1)),
            Vector(Some(7), None, None, Some(9), None, Some(4), None, None, Some(6)),
            Vector(None, Some(6), Some(1), None, Some(3), Some(7), None, Some(8), None),
            Vector(None, None, None, None, None, None, None, None, None),
            Vector(None, None, Some(5), None, Some(8), None, Some(1), Some(7), None)))) => ()
          }
        }
      }
    }
  }
}
