package junicamp
package sudoku

import sudoku.Examples.*
import sudoku.Validation.*
import sudoku.Sudoku.*
import sudoku.Solving.*

import Generate.*
import utest.*

import java.nio.file.{Files, Paths, Path}
import scala.jdk.CollectionConverters.*
import java.awt.event.KeyEvent


object ValidationTest extends TestSuite {
  val tests = Tests {
    test("validation") {
      test("areCellsSolved") {
        val filledButInvalidRow = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map(x => Some(x))
        areCellsSolved(filledButInvalidRow) ==> false
        allRowsSolved(goodSudoku) ==> true
        allRowsSolved(oneCellMissingSudoku) ==> false
        allRowsSolved(someCellMissingSudoku) ==> false
        isSudokuSolved(goodSudoku) ==> true
        isSudokuSolved(notGoodSudoku) ==> false
      }
      test("areCellsValid"){
        val filledButInvalidRow = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map(x => Some(x))
        areCellsValid(filledButInvalidRow) ==> false
        val filledValidRow = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9).map(x => Some(x)) :+ None
        areCellsValid(filledValidRow) ==> true
        isSudokuValid(goodSudoku) ==> true
        isSudokuValid(notGoodSudoku) ==> true
      }

      test("isSudokuRepetitionFree"){
        isSudokuRepetitionFree(goodSudoku) ==> true
        isSudokuRepetitionFree(notGoodSudoku) ==> false
        isSudokuRepetitionFree(testSudoku1) ==> false
        areCellsRepetitionFree(goodSudoku.rows(0)) ==> true
        val filledRepetitiveRow = Vector(1, 2, 3, 4, 5, 6, 8, 8, 9).map(x => Some(x))
        areCellsRepetitionFree(filledRepetitiveRow) ==> false
      }
      test("areCellsFilled"){
        val filledValidRow = Vector(1, 2, 3, 4, 5, 6, 7, 8).map(x => Some(x)) :+ Some(1)
        areCellsFilled(filledValidRow) ==> true
      }
      test("getNthBlock"){
        val eighthBlock = Vector(Some(2), Some(8), Some(4), Some(6), Some(3), Some(5), Some(1), Some(7), Some(9))
        getNthBlockV2(goodSudoku, 8) ==> eighthBlock
      }

    }

    test("solver"){
      test("catchUnsolvableSudoku") {
        try {
          finishSudoku(notGoodSudoku)
        } catch {
          case e: IllegalArgumentException => println("not solvable Sudoku")
        }}
      test("countSolutions") {
        countSolutions(oneCellMissingSudoku) ==> 1
        countSolutions(partiallySolvedGoodSudoku) ==> 1
        countSolutions(goodSudoku) ==> 1
      }
      test("possibleSolutions"){
        possibleSolutionsForCell(partiallySolvedGoodSudoku, 6, 1) ==> Set(6)
        possibleSolutionsForCell(partiallySolvedGoodSudoku, 0, 5) ==> Set(2, 8)
        possibleSolutionsForCell(partiallySolvedGoodSudoku, 3, 8) ==> Set(2, 3)
        numOfPossibleSolutionsForCell(someCellMissingSudoku, 0, 5) ==> 1
        numOfPossibleSolutionsForCell(someCellMissingSudoku, 8, 0) ==> 1
        numOfPossibleSolutionsForCell(someCellMissingSudoku, 8, 1) ==> 1
        numOfPossibleSolutionsForCell(someCellMissingSudoku, 8, 5) ==> 1
        sumOfPossibleSolutionsForAllCells(someCellMissingSudoku) ==> 4
        sumOfPossibleSolutionsForAllCells(oneCellMissingSudoku) ==> 1
        numOfPossibleSolutionsForCell(someCellMissingSudoku, 0, 5) ==> 1
      }
      test("emptyCells"){
        numOfEmptyCells(oneCellMissingSudoku) ==> 1
        numOfEmptyCells(someCellMissingSudoku) ==> 4
      }
      test("numOfChoices"){
        countOfSingleChoiceCells(goodSudoku) ==> 0
        countOfSingleChoiceCells(oneCellMissingSudoku) ==> 1
        countOfSingleChoiceCells(someCellMissingSudoku) ==> 4
      }
      test("solver"){
        calcLogicalNextSteps(List(oneCellMissingSudoku)) ==> List(goodSudoku)
        calcLogicalNextSteps(List(someCellMissingSudoku)) ==> List(goodSudoku)
        finishSudoku(oneCellMissingSudoku) ==> Option(goodSudoku)
        finishSudoku(someCellMissingSudoku) ==> Option(goodSudoku)
        finishSudoku(partiallySolvedGoodSudoku) ==> Option(goodSudoku)
      }
      test("insert"){
        Sudoku(
          Vector(
            Vector(Some(5), None, Some(4), Some(6), Some(7), None, None, Some(1), None),
            Vector(Some(6), Some(7), None, None, None, None, Some(3), Some(4), Some(8)),
            Vector(Some(1), None, None, Some(3), None, None, Some(5), Some(6), None),
            Vector(None, Some(5), None, None, Some(6), Some(1), Some(4), None, None),
            Vector(None, Some(2), None, Some(8), Some(5), Some(3), Some(7), None, Some(1)),
            Vector(Some(7), Some(1), None, Some(9), Some(2), Some(4), None, None, Some(6)),
            Vector(Some(9), None, Some(1), None, Some(3), None, Some(2), None, Some(4)),
            Vector(2, 8, 7, 4, 1, 9, 6, 3, 5).map(x => Some(x)),
            Vector(None, None, Some(5), Some(2), Some(8), None, Some(1), Some(7), Some(9)),
          )).insert(0, 1, 3) ==> Sudoku(
          Vector(
            Vector(Some(5), Some(3), Some(4), Some(6), Some(7), None, None, Some(1), None),
            Vector(Some(6), Some(7), None, None, None, None, Some(3), Some(4), Some(8)),
            Vector(Some(1), None, None, Some(3), None, None, Some(5), Some(6), None),
            Vector(None, Some(5), None, None, Some(6), Some(1), Some(4), None, None),
            Vector(None, Some(2), None, Some(8), Some(5), Some(3), Some(7), None, Some(1)),
            Vector(Some(7), Some(1), None, Some(9), Some(2), Some(4), None, None, Some(6)),
            Vector(Some(9), None, Some(1), None, Some(3), None, Some(2), None, Some(4)),
            Vector(2, 8, 7, 4, 1, 9, 6, 3, 5).map(x => Some(x)),
            Vector(None, None, Some(5), Some(2), Some(8), None, Some(1), Some(7), Some(9)),
          ))
      }
    }

    test("serialize&Deserialize"){
      val deserializedSerializedGoodSudoku = deserialize(serialize(goodSudoku))
      deserializedSerializedGoodSudoku ==> goodSudoku
      val deserializedSerializedOneCellMissingSudoku = deserialize(serialize(oneCellMissingSudoku))
      deserializedSerializedOneCellMissingSudoku ==> oneCellMissingSudoku
      val deserializedSerializedSomeCellMissingSudoku = deserialize(serialize(someCellMissingSudoku))
      deserializedSerializedSomeCellMissingSudoku ==> someCellMissingSudoku
      val deserializedSerializedPartiallySolvedGoodSudoku = deserialize(serialize(partiallySolvedGoodSudoku))
      deserializedSerializedPartiallySolvedGoodSudoku ==> partiallySolvedGoodSudoku
      val deserializedSerializedNotGoodSudoku = deserialize(serialize(notGoodSudoku))
      deserializedSerializedNotGoodSudoku ==> notGoodSudoku
    }
    test("testSavingAndReading"){
      def testSavingAndReading(path: Path, sudoku: Sudoku): Unit = {
        save(sudoku, path)
        println("sudoku saved")
        Files.exists(path) ==> true
        load(path) ==> sudoku
        Files.delete(path)
        println("sudoku deleted")
      }
      testSavingAndReading(Paths.get("./testThenDelete.txt"), notGoodSudoku)
      testSavingAndReading(Paths.get("./testThenDelete.txt"), partiallySolvedGoodSudoku)
      testSavingAndReading(Paths.get("./testThenDelete.txt"), testingSudoku)



    }


  }
}
