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
      test("invalidActions") {
        test("nullInstructions") {
          assertMatch(execRawCommand(someCellMissingSudoku, "", testingSudoku)) {
            case Left(err: String) if err.contains("understood") => ()
          }
        }
        test("nonExistentActions") {
          assertMatch(execRawCommand(someCellMissingSudoku, "z", testingSudoku)) {
            case Left(err: String) if err.contains("understood") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "k236", testingSudoku)) {
            case Left(err: String) if err.contains("understood") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "/123", testingSudoku)) {
            case Left(err: String) if err.contains("understood") => ()
          }
        }
      }
      test("insertion") {
        test("nonInsertable") {
          assertMatch(execRawCommand(someCellMissingSudoku, "i", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "ik36", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "i024", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "i34b", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "i34", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "i740", testingSudoku)) {
            case Left(err: String) if err.contains("3 numbers") => ()
          }
        }
        test("nonEmpty"){
          assertMatch(execRawCommand(someCellMissingSudoku, "i114", testingSudoku)) {
            case Left(err: String) if err.contains("only those cells") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "i528", testingSudoku)) {
            case Left(err: String) if err.contains("only those cells") => ()
          }
        }
        test("workingInsertion") {
          assertMatch(execRawCommand(testingSudoku, "i913", testingSudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.insert(8,0,3) => ()
          }
          assertMatch(execRawCommand(testingSudoku, "i192", testingSudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.insert(0,8,2) => ()
          }
          assertMatch(execRawCommand(testingSudoku, "i589", testingSudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.insert(4,7,9) => ()
          }
        }
      }
      test("deletion") {
        test("nonValidIxes") {
          assertMatch(execRawCommand(someCellMissingSudoku, "d", testingSudoku)) {
            case Left(err: String) if err.contains("2 numbers") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "d)4", testingSudoku)) {
            case Left(err: String) if err.contains("2 numbers") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "d40", testingSudoku)) {
            case Left(err: String) if err.contains("2 numbers") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "d03", testingSudoku)) {
            case Left(err: String) if err.contains("2 numbers") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "d5", testingSudoku)) {
            case Left(err: String) if err.contains("2 numbers") => ()
          }
        }
        test("nonEmpty") {
          assertMatch(execRawCommand(someCellMissingSudoku, "d11", testingSudoku)) {
            case Left(err: String) if err.contains("was empty at the beginning") => ()
          }
          assertMatch(execRawCommand(someCellMissingSudoku, "d52", testingSudoku)) {
            case Left(err: String) if err.contains("was empty at the beginning") => ()
          }
        }
        test("workingDeletion") {
          assertMatch(execRawCommand(testingSudoku, "d59", emptySudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.delete(4, 8) => ()
          }
          assertMatch(execRawCommand(testingSudoku, "d18", emptySudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.delete(0, 7) => ()
          }
          assertMatch(execRawCommand(testingSudoku, "d71", emptySudoku)) {
            case Right(x: Sudoku) if x == testingSudoku.delete(6, 0) => ()
          }
        }
      }
    }
  }
}
