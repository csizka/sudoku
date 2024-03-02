package junicamp
package sudoku

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.util.Random

class misc {
  def guessingGame(): Unit = {
    val randomNum = Random.nextInt(101)
    println("Guess the number between 0 and 100:")
    var guessedNum = readLine().toInt
    var i = 0
    while (guessedNum != randomNum) {
      if (guessedNum > randomNum) {
        println(s"The number is smaller than $guessedNum")
      } else {
        println(s"The number is bigger than $guessedNum")
      }
      println("Guess again: ")
      guessedNum = readLine().toInt
      i = i + 1
    }
    println(s"You found the number: $randomNum in $i guesses, congrats!")}

  def guessingGameV2(): Unit = {
    val randomNum = Random.nextInt(101)
    println("Write in your name:")
    val name = readLine()
    println(s" $name, guess the number between 0 and 100:")

    @tailrec
    def checkGuessedNum(curGuess: Int, min: Int, max: Int, numTries: Int): Unit = {
      if (curGuess == randomNum) {
        println(s"Congrats $name, you have guessed right in $numTries attempts!")
      } else if (curGuess > randomNum) {
        val newMax = List(curGuess - 1, max).min
        println(s"$curGuess is too high, guess again between $min and $newMax")
        checkGuessedNum(readLine().toInt, min, newMax, numTries + 1)
      } else {
        val newMin = List(min, curGuess + 1).max
        println(s"$curGuess is too low, guess again between $newMin and $max!")
        val newGuess = readLine().toInt
        checkGuessedNum(newGuess, newMin, max, numTries + 1)
      }
    }
    checkGuessedNum(readLine().toInt, 0, 100, 1)
  }
}
