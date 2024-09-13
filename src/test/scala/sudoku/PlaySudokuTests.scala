package junicamp
package sudoku

import sudoku.Examples.*
import sudoku.Validation.*
import sudoku.Sudoku.*
import sudoku.Solving.*

import PlaySudoku.*
import Generate.*
import sudoku.PlaySudokuTests.emptyHistory
import utest.*

import scala.jdk.CollectionConverters.*

object PlaySudokuTests extends TestSuite {
  val emptyHistory = Vector.empty[(Int, Int, Cell)]
  val nonEmptyHistory = Vector((0,0,Some(1)), (0,0,None), (0,0,Some(1)), (0,0,None))
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
    test("parseUndoCmd"){
      assertMatch(parseUndoCmd("00".toList)) {
        case Left(err: String) => ()
      }
      assertMatch(parseUndoCmd("14".toList)) {
        case Right(Undo(14)) => ()
      }
      assertMatch(parseUndoCmd("f4".toList)) {
        case Left(err: String) => ()
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
      assertMatch(parseCommand("d4")) {
        case Left(err: String) => ()
      }
      assertMatch(parseCommand("r4")) {
        case Left(err: String) => ()
      }
      assertMatch(parseCommand("h0")) {
        case Left(err: String) => ()
      }
      assertMatch(parseCommand("d765")) {
        case Left(err: String) => ()
      }
      assertMatch(parseCommand("f3")) {
        case Left(err: String) => ()
      }
      assertMatch(parseCommand("i2345")) {
        case Left(err: String) => ()
      }
      assertMatch(parseCommand("u2b5")) {
        case Left(err: String) => ()
      }
      assertMatch(parseCommand("d18")) {
        case Right(Delete(0, 7)) => ()
      }
      assertMatch(parseCommand("i958")) {
        case Right(Insert(8, 4, 8)) => ()
      }
      assertMatch(parseCommand("r")) {
        case Right(Restart()) => ()
      }
      assertMatch(parseCommand("f")) {
        case Right(Finish()) => ()
      }
      assertMatch(parseCommand("h")) {
        case Right(Hint()) => ()
      }
      assertMatch(parseCommand("u958")) {
        case Right(Undo(958)) => ()
      }
      assertMatch(parseCommand("u2")) {
        case Right(Undo(2)) => ()
      }
    }
    test("execCommand") {
      test("insertion") {
        test("invalid"){
          assertMatch(execCommand(someCellMissingSudoku, Insert(0,0,4), testingSudoku, emptyHistory)) {
            case (Left(err: String), emptyHistory) if err.contains("only those cells") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, Insert(4,1,8), testingSudoku, emptyHistory)) {
            case (Left(err: String), emptyHistory) if err.contains("only those cells") => ()
          }
        }
        test("valid") {
          assertMatch(execCommand(testingSudoku, Insert(8,0,3), testingSudoku, emptyHistory)) {
            case (Right(x: Sudoku), Vector((8,0, None))) if x == testingSudoku.insert(8,0,3) => ()
          }
          assertMatch(execCommand(testingSudoku, Insert(0,8,2), testingSudoku, emptyHistory)) {
            case (Right(x: Sudoku), Vector((0,8, None))) if x == testingSudoku.insert(0,8,2) => ()
          }
          assertMatch(execCommand(testingSudoku, Insert(4,7,9), testingSudoku, emptyHistory)) {
            case (Right(x: Sudoku), Vector((4,7, None))) if x == testingSudoku.insert(4,7,9) => ()
          }
        }
      }
      test("deletion") {
        test("invalid") {
          assertMatch(execCommand(someCellMissingSudoku, Delete(0,0), testingSudoku, emptyHistory)) {
            case (Left(err: String), emptyHistory) if err.contains("was empty at the beginning") => ()
          }
          assertMatch(execCommand(someCellMissingSudoku, Delete(4,1), testingSudoku, emptyHistory)) {
            case (Left(err: String), emptyHistory) if err.contains("was empty at the beginning") => ()
          }
        }
        test("valid") {
          assertMatch(execCommand(testingSudoku, Delete(4, 8), emptySudoku, emptyHistory)) {
            case (Right(x: Sudoku), Vector((4,8, Some(1)))) if x == testingSudoku.delete(4, 8) => ()
          }
          assertMatch(execCommand(testingSudoku, Delete(0, 7), emptySudoku, emptyHistory)) {
            case (Right(x: Sudoku), Vector((0,7, Some(1)))) if x == testingSudoku.delete(0, 7) => ()
          }
          assertMatch(execCommand(testingSudoku, Delete(6, 0), emptySudoku, emptyHistory)) {
            case (Right(x: Sudoku), Vector((6,0, Some(9)))) if x == testingSudoku.delete(6, 0) => ()
          }
        }
      }
      test("undo"){
        test("invalid") {
          assertMatch(execCommand(testingSudoku, Undo(5), testingSudoku, emptyHistory)) {
            case (Left(err: String), emptyHistory) if err.contains("There are no steps to undo") => ()
          }
        }
        test("valid") {
          assertMatch(execCommand(emptySudoku.insert(0,0,2), Undo(5), emptySudoku, nonEmptyHistory)) {
            case (Right(x: Sudoku), emptyHistory) if x == emptySudoku => ()
          }
          assertMatch(execCommand(emptySudoku.insert(0,0,2), Undo(1), emptySudoku, Vector((0, 0, Some(1))))) {
            case (Right(x: Sudoku), emptyHistory) if x == emptySudoku.insert(0,0,1) => ()
          }
          assertMatch(execCommand(testingSudoku, Undo(1), emptySudoku, Vector((1, 1, None), (0, 0, None)))) {
            case (Right(x: Sudoku), Vector((0, 0, None))) if x == testingSudoku.delete(1, 1) => ()
          }
        }
      }
      test("hint") {
        assertMatch(execCommand(testingSudoku, Hint(), emptySudoku, nonEmptyHistory)) {
          case (Right(x: Sudoku), nonEmptyHistory) if x == testingSudoku => ()
        }
        assertMatch(execCommand(emptySudoku, Hint(), emptySudoku, emptyHistory)) {
          case (Right(x: Sudoku), emptyHistory) if x == emptySudoku => ()
        }
      }
      test("finish") {
        assertMatch(execCommand(someCellMissingSudoku, Finish(), emptySudoku, nonEmptyHistory)) {
          case (Right(x: Sudoku), nonEmptyHistory) if x == goodSudoku => ()
        }
        assertMatch(execCommand(emptySudoku, Finish(), emptySudoku, emptyHistory)) {
          case (Right(x: Sudoku), emptyHistory) if isSudokuSolved(x) => ()
        }
      }
    }
  }
}
