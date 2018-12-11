package dayten

import scala.io.Source
import scala.language.postfixOps
import java.io._

object DayTen {

  def main(args: Array[String]): Unit = {
    val info = readCoordinatesAndVectors("./src/main/scala/dayten/day_ten_input")
    val coordinates = info.map(_._1)
    val vectors = info.map(_._2)

    val smallestSquare = smallestGridCoordinates(coordinates, vectors, Long.MaxValue, 0)

    drawStars(smallestSquare._1) // Part 1 = LCPGPXGL
    println(smallestSquare._2) // Part 2 = 10639
  }

  def smallestGridCoordinates(coordinates: Array[(Int, Int)],
                              vectors: Array[(Int, Int)],
                              grid: Long,
                              iteration: Int // Only used for part 2 puzzle
                             ): (Array[(Int, Int)], Int) = {
    val newCoordinates = applyVector(coordinates, vectors)
    val newGrid = enclosingGrid(newCoordinates)
    if (newGrid > grid) (coordinates, iteration)
    else smallestGridCoordinates(newCoordinates, vectors, newGrid, iteration + 1)
  }

  def applyVector(coordinates: Array[(Int, Int)], vectors: Array[(Int, Int)]): Array[(Int, Int)] = {
    coordinates.zip(vectors).map(p => (p._1._1 + p._2._1, p._1._2 + p._2._2))
  }

  def drawStars(coordinates: Array[(Int, Int)]): Unit = {
    val lineSep = System.getProperty("line.separator")
    val pw = new PrintWriter(new File("./src/main/scala/dayten/day_ten_output.txt"))
    val (minX, minY, maxX, maxY) = extremities(coordinates)
    (minX to maxX).foreach(x => pw.write((minY to maxY).map(y => if (coordinates.contains((x, y))) '#' else ' ').mkString + lineSep))
    pw.close()
  }

  def enclosingGrid(coordinates: Array[(Int, Int)]): Long = {
    val (minX, minY, maxX, maxY) = extremities(coordinates)
    val xlen = math.abs(maxX - minX)
    val ylen = math.abs(maxY - minY)
    xlen.toLong * ylen.toLong // Prevents Int overflow
  }

  def extremities(coordinates: Array[(Int, Int)]): (Int, Int, Int, Int) = {
    val minX = coordinates.map(_._1).min
    val minY = coordinates.map(_._2).min
    val maxX = coordinates.map(_._1).max
    val maxY = coordinates.map(_._2).max
    (minX, minY, maxX, maxY)
  }

  def readCoordinatesAndVectors(filePath: String): Array[((Int, Int), (Int, Int))] = {
    Source.fromFile(filePath).getLines.map(lineToCoordAndVector).toArray
  }

  def lineToCoordAndVector(l: String): ((Int, Int), (Int, Int)) = {
    val lineDef = """position=< *([-\d]+), *([-\d]+)> velocity=< *([-\d]+), *([-\d]+)>""" r
    val matches = lineDef findAllIn l
    ((matches.group(1).toInt, matches.group(2).toInt), (matches.group(3).toInt, matches.group(4).toInt))
  }

}