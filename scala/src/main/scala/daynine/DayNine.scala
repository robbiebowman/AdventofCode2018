package daynine

import java.util

import scala.io.Source

object DayNine {

  def main(args: Array[String]): Unit = {
    val input = Source.fromFile("./src/main/scala/daynine/day_nine_test_input").getLines.next()
    val puzzleDef = ("(\\d+) players; last marble is worth (\\d+) points" r) findAllIn input

    val playerCount = puzzleDef.group(1).toInt
    val targetScore = puzzleDef.group(2).toInt

    val initialGame = Game(0, 1, List(0), (0 until playerCount).map(_ -> List[Int]()).toMap, 0)
    val finishedGame = placeMarblesUntil(initialGame, targetScore)

    println(s"High score: ${finishedGame.scoreboard.map(_._2.sum).sum}, turns: ${finishedGame.turn}")
  }

  def placeMarblesUntil(game: Game, targetScore: Int): Game = {
    if (game.lastScore == targetScore) {
      game
    } else {
      val gameWithNewMarble = placeMarble(game, game.turn)
      placeMarblesUntil(gameWithNewMarble, targetScore)
    }
  }

  def placeMarble(game: Game, marble: Int): Game = {
    if (marble % 23 == 0) {
      val indexOfRemovedMarble = if (game.currentMarbleIndex - 7 >= 0) game.currentMarbleIndex - 7 else game.circle.size + (game.currentMarbleIndex - 7)
      val playerScoreIndex = game.turn % game.scoreboard.size
      val newScoreboard = game.scoreboard + (playerScoreIndex -> (game.scoreboard(playerScoreIndex) :+ marble :+ game.circle(indexOfRemovedMarble)))
      val newCircle = game.circle.take(indexOfRemovedMarble) ++ game.circle.drop(indexOfRemovedMarble + 1)
      Game(indexOfRemovedMarble, game.turn + 1, newCircle, newScoreboard, marble + game.circle(indexOfRemovedMarble))
    } else {
      val insertIndex = if(game.currentMarbleIndex + 2 > game.circle.size) 1 else game.currentMarbleIndex + 2
      val newCircle = (game.circle.take(insertIndex) :+ marble) ++ game.circle.drop(insertIndex)
      Game(insertIndex, game.turn + 1, newCircle, game.scoreboard, 0)
    }
  }
}

case class Game(currentMarbleIndex: Int, turn: Int, circle: List[Int], scoreboard: Map[Int, List[Int]], lastScore: Int)