package dayeleven

import scala.language.postfixOps

object DayEleven {

  val serialNumber = 1718

  def main(args: Array[String]): Unit = {
    val grid = squareGrid(300)

    val highestPowerSquare = (1 to 300).flatMap(size => (0 until 300 - size)
      .map(x => (size, x))).flatMap(p => (0 until 300 - p._1).map(y => (p._1, p._2, y)))
      .maxBy(p => squareTotalPower(p._2, p._3, p._1, grid))

    println(s"Biggest: ${highestPowerSquare._2 + 1},${highestPowerSquare._3 + 1},${highestPowerSquare._1}") // Part 1 = 243,34
  }

  def squareTotalPower(x: Int, y: Int, size: Int, grid: Array[Array[Int]]): Int = {
    (0 until size).flatMap(X => (0 until size).map(Y => grid(x + X)(y + Y))).sum
  }

  def powerLevel(x: Int, y: Int, serial: Int): Int = {
    val firstThreeDigits = (((x + 10) * y + serial) * (x + 10)) % 1000
    ((firstThreeDigits - (firstThreeDigits % 100)) / 100) - 5
  }

  def squareGrid(size: Int): Array[Array[Int]] = {
    (1 to size).map(x => (1 to size).map(y => powerLevel(x, y, serialNumber)).toArray).toArray
  }

}