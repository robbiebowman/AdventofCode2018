package daynine

import java.util

import scala.io.Source

object DayNine {

  def main(args: Array[String]): Unit = {
    val input = Source.fromFile("./src/main/scala/daynine/day_nine_input").getLines.next()
    val puzzleDef = ("(\\d+) players; last marble is worth (\\d+) points" r) findAllIn input

    val playerCount = puzzleDef.group(1).toInt
    val marbles = puzzleDef.group(2).toInt

    println(s"Player count: $playerCount, marbles: $marbles")

    val initialGame = Game(0, 1, Array(0), (0 until playerCount).map(_ -> 0).toMap)
    val finishedGame = placeMarblesUntil(initialGame, marbles)

    println(s"High score: ${finishedGame.scoreboard.map(_._2).max}, turns: ${finishedGame.turn}")
  }

  def placeMarblesUntil(game: Game, marbles: Int): Game = {
    if (1 == marbles) {
      game
    } else {
      val gameWithNewMarble = placeMarble(game, game.turn)
      placeMarblesUntil(gameWithNewMarble, marbles-1)
    }
  }

  def placeMarble(game: Game, marble: Int): Game = {
    if (marble % 23 == 0) {
       val indexOfRemovedMarble = if (game.currentMarbleIndex - 7 >= 0) game.currentMarbleIndex - 7 else game.circle.size + (game.currentMarbleIndex - 7)
       val playerScoreIndex = game.turn % game.scoreboard.size
       val newScoreboard = game.scoreboard + (playerScoreIndex -> (game.scoreboard(playerScoreIndex) + marble + game.circle(indexOfRemovedMarble)))
       val newCircle = game.circle.take(indexOfRemovedMarble) ++ game.circle.drop(indexOfRemovedMarble + 1)
       Game(indexOfRemovedMarble, game.turn + 1, newCircle, newScoreboard)
    } else {
      val insertIndex = if(game.currentMarbleIndex + 2 > game.circle.size) 1 else game.currentMarbleIndex + 2
      val newCircle = (game.circle.take(insertIndex) :+ marble) ++  game.circle.drop(insertIndex)
      Game(insertIndex, game.turn + 1, newCircle, game.scoreboard)
    }
  }
}

case class Game(currentMarbleIndex: Int, turn: Int, circle: Array[Int], scoreboard: Map[Int, Int])