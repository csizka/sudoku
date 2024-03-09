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
    test("parseInsertCmd"){
      assertMatch(parseInsertCmd("53".toList)) {
        case Left(err: String) => ()
      }
      assertMatch(parseInsertCmd("490".toList)) {
        case Left(err: String) => ()
      }
      assertMatch(parseInsertCmd("k99".toList)) {
        case Left(err: String) => ()
      }
      assertMatch(parseInsertCmd("112".toList)) {
        case Right(Insert(0, 0, 2)) => ()
      }
      assertMatch(parseInsertCmd("958".toList)) {
        case Right(Insert(8, 4, 8)) => ()
      }
    }
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
            case Right(x: Sudoku) if x == testingSudoku.insert(8,0,3) => ()
          }
          assertMatch(execCommand(testingSudoku, "i192", testingSudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.insert(0,8,2) => ()
          }
          assertMatch(execCommand(testingSudoku, "i589", testingSudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.insert(4,7,9) => ()
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
            case Right(x: Sudoku) if x == testingSudoku.delete(4, 8) => ()
          }
          assertMatch(execCommand(testingSudoku, "d18", emptySudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.delete(0, 7) => ()
          }
          assertMatch(execCommand(testingSudoku, "d71", emptySudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.delete(6, 0) => ()
          }
        }
      }
    }
  }
}
