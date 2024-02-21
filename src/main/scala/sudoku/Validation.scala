package junicamp
package sudoku

import sudoku.Examples.*
import sudoku.Pretty.*
import sudoku.Sudoku.*

import scala.annotation.tailrec

object Validation {

  //Row Tests
  def areCellsSolved(vector: Vector[Cell]): Boolean = {
    @tailrec
    def areCellsSolvedHelper(restElems: Vector[Cell], set: Set[Int]): Boolean = restElems match {
      case Vector() => set == Set(1, 2, 3, 4, 5, 6, 7, 8, 9)
      case Some(x) +: rest if (1 to 9).contains(x) => areCellsSolvedHelper(rest, set + x)
      case _ => false
    }
    areCellsSolvedHelper(vector, Set.empty[Int])
  }

  def areCellsRepetitionFree(vector: Vector[Cell]): Boolean = {
    val filledCells = vector.flatten
    filledCells.size == filledCells.toSet.size
  }

  def areCellsValid(vector: Vector[Cell]): Boolean = {
    vector.flatten.toSet.subsetOf((1 to 9).toSet)
  }

  def areCellsFilled(vector: Vector[Cell]): Boolean = {
    vector.size == 9 && !vector.contains(None)
  }

  def allRowsSolved(sudoku: Sudoku): Boolean = {
    sudoku.rows.forall(areCellsSolved) && sudoku.rows.size == 9
  }

  //noinspection ScalaWeakerAccess
  def allRowsRepetitionFree(sudoku: Sudoku): Boolean = {
    sudoku.rows.forall(areCellsRepetitionFree)
  }

  //noinspection ScalaWeakerAccess
  // Column tests
  def getNthColumn(sudoku: Sudoku, n: Int): Column = {
    if (0<= n && n <= 8)
      sudoku.rows.map(row => row(n))
    else throw new IndexOutOfBoundsException(s" the index inserted: $n is invalid, please use an index between 0 and 8")
  }

  //noinspection ScalaWeakerAccess
  def getAllColumns(sudoku: Sudoku): Vector[Column] = {
    sudoku.rows.indices
      .map(n => getNthColumn(sudoku, n))
      .toVector
  }

  //noinspection ScalaWeakerAccess
  def areAllColumnsRepetitionFree(sudoku: Sudoku): Boolean = {
    getAllColumns(sudoku).forall(areCellsRepetitionFree)
  }

  def areAllColumnsSolved(sudoku: Sudoku): Boolean = {
    getAllColumns(sudoku).forall(areCellsSolved)
  }

  //noinspection ScalaWeakerAccess
  def getNthBlock(sudoku: Sudoku, n: Int): Block = {
    if (0 <= n && n <= 8) {
      val row = n / 3 * 3
      val allRows = Vector(row, row + 1, row + 2)
      val column = n % 3 * 3

      allRows.flatMap(x => sudoku.rows(x).slice(column, column + 3))
    }
    else throw new IndexOutOfBoundsException(s"The index $n is not valid, please use one between 0 and 8.")
  }
  def getNthBlockV2(sudoku: Sudoku, n: Int): Block = {
    if (0 <= n && n <= 8) {
      val firstXCoord = n / 3 * 3
      val firstYCoord = n % 3 * 3
      val blockCoords = for {
        x <- Vector(firstXCoord, firstXCoord + 1, firstXCoord + 2)
        y <- Vector(firstYCoord, firstYCoord + 1, firstYCoord + 2)
      } yield (x,y)
      blockCoords.map((x, y) => sudoku.rows(x)(y))
    }
    else throw new IndexOutOfBoundsException(s"The index $n is not valid, please use one between 0 and 8.")
  }

  //noinspection ScalaWeakerAccess
  def getAllBlocks(sudoku: Sudoku): Vector[Column] = {
    if (sudoku.rows.nonEmpty) {
      sudoku.rows.indices
        .map(n => getNthBlock(sudoku, n))
        .toVector
    }
    else Vector.empty[Column]
  }

  //noinspection ScalaWeakerAccess
  def areAllBlocksRepetitionFree(sudoku: Sudoku): Boolean = {
    getAllBlocks(sudoku).forall(areCellsRepetitionFree)
  }

  def areAllBlocksSolved(sudoku: Sudoku): Boolean = {
    getAllBlocks(sudoku).forall(areCellsSolved)
  }

// All Rows && Columns && Blocks validation
  def isSudokuRepetitionFree(sudoku:Sudoku): Boolean = {
    areAllBlocksRepetitionFree(sudoku) &&
    areAllColumnsRepetitionFree(sudoku) &&
    allRowsRepetitionFree(sudoku)
  }

  def isSudokuValid(sudoku: Sudoku): Boolean = {
    sudoku.rows.forall(areCellsValid)
}

  def isSudokuSolved(sudoku: Sudoku): Boolean = {
    areAllBlocksRepetitionFree(sudoku) &&
    areAllColumnsRepetitionFree(sudoku) &&
    allRowsRepetitionFree(sudoku) &&
    sudoku.rows.flatten.size == 81 &&
    numOfEmptyCells(sudoku) == 0 &&
    sudoku.rows.flatten.map {
      case Some(x) => x
      case None => 0
    }.toSet == (1 to 9).toSet

  }

  def possibleSolutionsForCell(sudoku: Sudoku, rowIndex: Int, columnIndex: Int): Set[Int] = {
    if (0 <= rowIndex && 0 <= columnIndex && columnIndex <= 8 && rowIndex <= 8)
      (1 to 9).toSet --
      sudoku.rows(rowIndex).flatten.toSet --
      getNthColumn(sudoku, columnIndex).flatten.toSet --
      getNthBlock(sudoku, (rowIndex / 3 * 3) + (columnIndex / 3)).flatten.toSet
    else throw new IndexOutOfBoundsException(s"Row index $rowIndex || $columnIndex is/ are not valid, please give a number between 0 and 8.")
  }

  //noinspection ScalaWeakerAccess
  def numOfPossibleSolutionsForCell(sudoku: Sudoku, rowIndex: Int, columnIndex: Int): Int =
    possibleSolutionsForCell(sudoku, rowIndex, columnIndex).size

  //noinspection ScalaWeakerAccess
  def collectEmptyCellCoords(sudoku: Sudoku): List[(Int, Int)] = {
    val coords = for {
        x <- 0 to 8
        y <- 0 to 8
    } yield (x, y)
    coords.filter { case (x,y) => sudoku.rows(x)(y).isEmpty }.toList
  }

  def sumOfPossibleSolutionsForAllCells(sudoku: Sudoku): Int = {
    val coords = collectEmptyCellCoords(sudoku)
    coords.foldLeft(0) { case (accCur: Int, (x, y)) => accCur + numOfPossibleSolutionsForCell(sudoku, x, y) }
  }
  def sumOfPossibleSolutionsForAllCellsV2(sudoku: Sudoku): Int = {
    val coords = for {
      x <- 0 to 8
      y <- 0 to 8
    } yield (x, y)
    coords.foldLeft(0) { case (accCur: Int, (x, y)) => accCur + numOfPossibleSolutionsForCell(sudoku, x, y) }
  }

  def numOfEmptyCells(sudoku: Sudoku): Int =
    collectEmptyCellCoords(sudoku).size

  //noinspection ScalaWeakerAccess
  def calcNextSteps(sudoku: Sudoku, row: Int, col: Int): List[Sudoku] = {
    possibleSolutionsForCell(sudoku, row, col).toList.map(x => sudoku.insert(row, col, x))
  }

  //noinspection ScalaWeakerAccess
  def findCellsToFill(sudoku: Sudoku): IndexedSeq[ (Int, Int)] = {
    val coords = for {
      x <- 0 to 8
      y <- 0 to 8
    } yield (x, y)
    coords
      .filter { case (x, y) => numOfPossibleSolutionsForCell(sudoku, x, y) > 0 }
  }

  //noinspection ScalaWeakerAccess
  def addSingleChoices(sudoku: Sudoku): Sudoku = {
    collectEmptyCellCoords(sudoku).filter { case (x, y) => numOfPossibleSolutionsForCell(sudoku, x, y) == 1}
      .foldLeft(sudoku) { case (accCur: Sudoku, (x, y)) => accCur.insert(x, y, possibleSolutionsForCell(sudoku, x, y).toList.head)}
  }

  def calcLogicalNextSteps(lstOfSudoku: List[Sudoku]): List[Sudoku] = {
    if (lstOfSudoku.nonEmpty) {
      val curSudoku = lstOfSudoku.head
      println(curSudoku)
      val restSudokus = lstOfSudoku.drop(1)
      val cellsToFill = collectEmptyCellCoords(curSudoku).filter { case (x, y) => numOfPossibleSolutionsForCell(curSudoku, x, y) > 0 }
      println(cellsToFill)
      if (cellsToFill.nonEmpty)
        val singleChoices = cellsToFill.filter((x, y) => numOfPossibleSolutionsForCell(curSudoku, x, y) == 1)
        if (singleChoices.nonEmpty)
          calcLogicalNextSteps(addSingleChoices(curSudoku) +: restSudokus)
        else {
          val cellToFill = cellsToFill.minBy((x, y) => numOfPossibleSolutionsForCell(curSudoku, x, y))
          println(calcNextSteps(curSudoku, cellToFill._1, cellToFill._2))
          calcLogicalNextSteps(calcNextSteps(curSudoku, cellToFill._1, cellToFill._2) ++ restSudokus)
        }
      else if (isSudokuSolved(curSudoku))
          List(curSudoku)
      else calcLogicalNextSteps(restSudokus)
    } else throw new IllegalArgumentException("the Sudoku is unsolvable")
  }
  def finishSudoku(sudoku: Sudoku): Sudoku = {
    if (isSudokuValid(sudoku) && isSudokuRepetitionFree(sudoku))
      calcLogicalNextSteps(List(sudoku)).head
    else throw new Exception("the Sudoku is unsolvable")
  }
}
