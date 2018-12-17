package daythirteen

import scala.io.Source

object DayThirteen {

  private val directions = Array('<', '>', '^', 'v')
  private val nextTurn = Map[Char, Char](('l', 's'), ('s', 'r'), ('r', 'l')) // (prevMove, nextMove)

  sealed case class Cart(
                          location: (Int, Int), // (row, col)
                          lastTurn: Char, // 'l', 's', 'r'
                          direction: Char // '<', '>', '^', 'v'
                        )

  def main(args: Array[String]): Unit = {
    val map = readMap("./src/main/scala/daythirteen/day_thirteen_part_two_test_input")
    val carts = findCarts(map)
    val cleanMap = removeCarts(map)

    val crashLocation = findCrash(carts, cleanMap)
    println(s"${crashLocation._2},${crashLocation._1}") // Part 1 = 116,91

    val uncrashedCart = findLastRemainingCart(0, carts, map).location
    println(s"${uncrashedCart._2},${uncrashedCart._1}") // Part 2 =
  }

  def findCollisions(i: Int, cs: Array[Cart], m: Array[String]): Array[(Int, Int)] = {
    if (i == cs.length) Array.empty
    else {
      val carts = cs updated(i, moveCart(cs(i), m))
      val cols = cs.map(c => c.location).groupBy(identity).filter(_._2.length > 1).keys.toArray
      cols ++ findCollisions(i + 1, carts, m)
    }
  }

  def collisions(cs: Array[Cart], m: Array[String]): Array[(Int, Int)] = {
    cs.map(c => c.location).groupBy(identity).filter(_._2.length > 1).keys.toArray
  }

  def findLastRemainingCart(i: Int, cs: Array[Cart], m: Array[String]): Cart = {
    if (cs.length == 1) cs.head
    else if (i == cs.length) findLastRemainingCart(0, sortCarts(cs), m)
    else {
      val movedCarts = cs.updated(i, moveCart(cs(i), m))
      val cols = collisions(movedCarts, m)
      val uncrashedCarts = movedCarts.filterNot(c => cols.contains(c.location))
      printMap(cs, m)
      findLastRemainingCart(i + 1, uncrashedCarts, m)
    }
  }

  def printMap(cs: Array[Cart], map: Array[String]): Unit = {
    println(s"Carts remaining: ${cs.length}")
    val m = removeCarts(map)
    m.indices.foreach(y => {
      m(y).indices.foreach(x => {
        val cartsHere = cs.filter(c => c.location == (y, x))
        if (cartsHere.nonEmpty) print(cartsHere.head.direction)
        else print(m(y)(x))
      })
      println()
    })
    println()
  }

  def findCrash(cs: Array[Cart], m: Array[String]): (Int, Int) = {
    val carts = sortCarts(cs)
    val movedCarts = carts.map(moveCart(_, m))
    val collisions = findCollisions(0, carts, m)
    if (collisions.nonEmpty) collisions.head
    else findCrash(movedCarts, m)
  }

  def moveCart(c: Cart, m: Array[String]): Cart = {
    val l = c.location
    val newLocation = c.direction match {
      case '<' => (l._1, l._2 - 1)
      case '>' => (l._1, l._2 + 1)
      case '^' => (l._1 - 1, l._2)
      case 'v' => (l._1 + 1, l._2)
    }
    val roadType = m(newLocation._1)(newLocation._2)
    val newTurn = if (roadType == '+') nextTurn(c.lastTurn) else c.lastTurn
    val newDirection = roadType match {
      case '/' => c.direction match {
        case '<' => 'v'
        case '>' => '^'
        case '^' => '>'
        case 'v' => '<'
      }
      case '\\' => c.direction match {
        case '<' => '^'
        case '>' => 'v'
        case '^' => '<'
        case 'v' => '>'
      }
      case '+' =>
        if (newTurn == 'l') c.direction match {
          case '<' => 'v'
          case '>' => '^'
          case '^' => '<'
          case 'v' => '>'
        } else if (newTurn == 'r') c.direction match {
          case '<' => '^'
          case '>' => 'v'
          case '^' => '>'
          case 'v' => '<'
        } else c.direction
      case _ => c.direction
    }
    Cart(newLocation, newTurn, newDirection)
  }

  def cartLocations(m: Array[String]): Array[(Int, Int)] = {
    val oneDimenMap = m.flatten
    val mapWidth = m.head.length
    oneDimenMap.indices
      .filter(i => directions.contains(oneDimenMap(i)))
      .map(i => (i % mapWidth, i / mapWidth)).toArray
  }

  def findCarts(m: Array[String]): Array[Cart] = {
    val oneDimenMap = m.flatten
    val mapWidth = m.head.length
    val locations = oneDimenMap.indices
      .filter(i => directions.contains(oneDimenMap(i)))
      .map(i => (i / mapWidth, i % mapWidth)).toArray
    locations.map(p => Cart(p, 'r', m(p._1)(p._2)))
  }

  def sortCarts(cs: Array[Cart]): Array[Cart] = {
    cs.sortBy(c => (c.location._1, c.location._2))
  }

  def readMap(filePath: String): Array[String] = {
    Source.fromFile(filePath).getLines.toArray
  }

  def removeCarts(m: Array[String]): Array[String] = {
    m.map(_.map(c => if (c == 'v' || c == '^') '|' else if (c == '<' || c == '>') '-' else c))
  }

}
