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
    test("parseDelCmd") {
      assertMatch(parseDelCmd("50".toList)) {
        case Left(err: String) => ()
      }
      assertMatch(parseDelCmd("4".toList)) {
        case Left(err: String) => ()
      }
      assertMatch(parseDelCmd("k99".toList)) {
        case Left(err: String) => ()
      }
      assertMatch(parseDelCmd("18".toList)) {
        case Right(Delete(0, 7)) => ()
      }
      assertMatch(parseDelCmd("95".toList)) {
        case Right(Delete(8, 4)) => ()
      }
    }
    test("parseCommand"){
      assertMatch(parseCommand("53")) {
        case Left(err: String) => ()
      }
      assertMatch(parseCommand("")) {
        case Left(err: String) => ()
      }
      assertMatch(parseCommand("i53")) {
        case Left(err: String) => ()
      }
      assertMatch(parseCommand("b")) {
        case Left(err: String) => ()
      }
      assertMatch(parseCommand("d18")) {
        case Right(Delete(0, 7)) => ()
      }
      assertMatch(parseCommand("i958")) {
        case Right(Insert(8, 4, 8)) => ()
      }
    }
    test("execCommand") {
      test("insertion") {
        test("invalid"){
          assertMatch(execCommand(someCellMissingSudoku, Insert(0,0,4), testingSudoku)) {
            case Left(err: String) if err.contains("only those cells") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, Insert(4,1,8), testingSudoku)) {
            case Left(err: String) if err.contains("only those cells") => ()
          }
        }
        test("valid") {
          assertMatch(execCommand(testingSudoku, Insert(8,0,3), testingSudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.insert(8,0,3) => ()
          }
          assertMatch(execCommand(testingSudoku, Insert(0,8,2), testingSudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.insert(0,8,2) => ()
          }
          assertMatch(execCommand(testingSudoku, Insert(4,7,9), testingSudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.insert(4,7,9) => ()
          }
        }
      }
      test("deletion") {
        test("invalid") {
          assertMatch(execCommand(someCellMissingSudoku, Delete(0,0), testingSudoku)) {
            case Left(err: String) if err.contains("was empty at the beginning") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, Delete(4,1), testingSudoku)) {
            case Left(err: String) if err.contains("was empty at the beginning") => ()
          }
        }
        test("valid") {
          assertMatch(execCommand(testingSudoku, Delete(4, 8), emptySudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.delete(4, 8) => ()
          }
          assertMatch(execCommand(testingSudoku, Delete(0, 7), emptySudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.delete(0, 7) => ()
          }
          assertMatch(execCommand(testingSudoku, Delete(6, 0), emptySudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.delete(6, 0) => ()
          }
        }
      }
    }
  }
}
