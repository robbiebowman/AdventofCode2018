package dayten

import scala.io.Source
import scala.language.postfixOps
import java.io._

object DayTen {

  def main(args: Array[String]): Unit = {
    val info = readCoordinatesAndVectors("./src/main/scala/dayten/day_ten_input")
    val coordinates = info.map(_._1)
    val vectors = info.map(_._2)

    val smallestSquare = lowestEntropyCoordinate(coordinates, vectors, Long.MaxValue)
    drawStars(smallestSquare)
  }

  def lowestEntropyCoordinate(coordinates: Array[(Int, Int)],
                              vectors: Array[(Int, Int)],
                              entropy: Long): Array[(Int, Int),] = {
    val newCoordinates = applyVector(coordinates, vectors)
    val newEntropy = crudeEntropy(newCoordinates)
    if (newEntropy > entropy) coordinates else lowestEntropyCoordinate(newCoordinates, vectors, newEntropy)
  }

  def applyVector(coordinates: Array[(Int, Int)], vectors: Array[(Int, Int)]): Array[(Int, Int)] = {
    coordinates.zip(vectors).map(p => (p._1._1 + p._2._1, p._1._2 + p._2._2))
  }

  def drawStars(coordinates: Array[(Int, Int)]): Unit = {
    val lineSep = System.getProperty("line.separator")
    val pw = new PrintWriter(new File("./src/main/scala/dayten/day_ten_output.txt" ))
    val (minX, minY, maxX, maxY) = extremities(coordinates)
    (minX to maxX).foreach(x => {
      pw.write((minY to maxY).map(y => if (coordinates.contains((x, y))) '#' else ' ').mkString + lineSep)
      println(s"printed line $x, $minX $maxX")
    })
    pw.close()
  }

  def crudeEntropy(coordinates: Array[(Int, Int)]): Long = {
    val (minX, minY, maxX, maxY) = extremities(coordinates)
    val xlen = math.abs(maxX - minX)
    val ylen = math.abs(maxY - minY)
    xlen.toLong * ylen.toLong
  }

  def isAdjacent(p1: (Int, Int), p2: (Int, Int)): Boolean = {
    math.abs(p1._1 - p2._1) <= 1 && math.abs(p1._2 - p2._2) <= 1
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