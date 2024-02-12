package junicamp.sudoku

import junicamp.sudoku.Examples.*
import junicamp.sudoku.Pretty.*
import junicamp.sudoku.Sudoku.*

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

  def allRowsRepetitionFree(sudoku: Sudoku): Boolean = {
    sudoku.rows.forall(areCellsRepetitionFree)
  }

  // Column tests
  def getNthColumn(sudoku: Sudoku, n: Int): Column = {
    if (0<= n && n <= 8)
      sudoku.rows.map(row => row(n))
    else throw new IndexOutOfBoundsException(s" the index inserted: $n is invalid, please use an index between 0 and 8")
  }

  def getAllColumns(sudoku: Sudoku): Vector[Column] = {
    sudoku.rows.indices
      .map(n => getNthColumn(sudoku, n))
      .toVector
  }

  def areAllColumnsRepetitionFree(sudoku: Sudoku): Boolean = {
    getAllColumns(sudoku).forall(areCellsRepetitionFree)
  }

  def areAllColumnsSolved(sudoku: Sudoku): Boolean = {
    getAllColumns(sudoku).forall(areCellsSolved) && sudoku.rows.size == 9
  }

  def getNthBlock(sudoku: Sudoku, n: Int): Block = {
    if (0 <= n && n <= 8) {
      val row = n / 3 * 3
      val allRows = Vector(row, row + 1, row + 2)
      val column = n % 3 * 3

      allRows.flatMap(x => sudoku.rows(x).drop(column).take(3))
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

  def blockContainsOnlyValidValues(block: Block): Boolean = block match {
    case Vector() => true
    case Some(x) +: rest if (1 to 9).contains(x) => blockContainsOnlyValidValues(rest)
    case None +: rest => blockContainsOnlyValidValues(rest)
    case _ => false
  }

  def isBlockRepetitionFree(block: Block): Boolean = {
    def isBlockRepetitionFreeHelper(restElems: Block, set: Set[Int]): Boolean = restElems match {
      case Vector() => true
      case Some(x) +: rest =>
        if (set.contains(x)) false
        else isBlockRepetitionFreeHelper(rest, set + x)
      case None +: rest => isBlockRepetitionFreeHelper(rest, set)
    }

    isBlockRepetitionFreeHelper(block, Set.empty[Int])
  }


  def isBlockSolved(block: Block): Boolean = {
    def validateBlockHelper(restElems: Block, set: Set[Int]): Boolean = restElems match {
      case Vector() => set == Set(1, 2, 3, 4, 5, 6, 7, 8, 9)
      case Some(x) +: rest if (1 to 9).contains(x) => validateBlockHelper(rest, set + x)
      case _ => false
    }

    validateBlockHelper(block, Set.empty[Int])
  }

  def getAllBlocks(sudoku: Sudoku): Vector[Column] = {
    if (sudoku.rows.nonEmpty) {
      sudoku.rows.indices
        .map(n => getNthBlock(sudoku, n))
        .toVector
    }
    else Vector.empty[Column]
  }

  def AreAllBlocksRepetitionFree(sudoku: Sudoku): Boolean = {
    getAllBlocks(sudoku).forall(areCellsRepetitionFree) && sudoku.rows.size == 9
  }

  def AreAllBlocksSolved(sudoku: Sudoku): Boolean = {
    getAllBlocks(sudoku).forall(isBlockSolved) && sudoku.rows.size == 9
  }

// All Rows && Columns && Blocks validation
  def isSudokuRepetitionFree(sudoku:Sudoku): Boolean = {
    AreAllBlocksRepetitionFree(sudoku) &&
    areAllColumnsRepetitionFree(sudoku) &&
    allRowsRepetitionFree(sudoku)
  }




}
