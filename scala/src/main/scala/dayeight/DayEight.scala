package dayeight

import scala.io.Source

object DayEight {

  case class Node(children: Array[Node], metadata: Array[Int]) {
    def metadataSum: Int = {
      children.map(_.metadataSum).sum + metadata.sum
    }

    def definitionLength: Int = {
      2 + children.map(_.definitionLength).sum + metadata.length
    }
  }

  def main(args: Array[String]): Unit = {
    val numbers = Source.fromFile("./src/main/scala/dayeight/day_eight_input").getLines.next().split(' ').map(_.toInt)

    val root = getNode(numbers)
    println(root.metadataSum)
  }

  def getNode(numbers: Array[Int]): Node = {
    val numChildren = numbers(0)
    val numMetadata = numbers(1)
    if (numChildren == 0) {
      val metadataEnd = numMetadata + 2
      Node(Array(), numbers.slice(2, metadataEnd))
    } else {
      val children = getSiblings(numbers.drop(2), numChildren-1)
      val childrenDefLength = children.map(_.definitionLength).sum
      Node(children, numbers.slice(2 + childrenDefLength, 2 + childrenDefLength + numMetadata))
    }
  }

  def getSiblings(numbers: Array[Int], numSiblings: Int): Array[Node] = {
    if (numbers.isEmpty) {
      Array()
    } else if (numSiblings == 0) {
      Array(getNode(numbers))
    } else {
      val node = getNode(numbers)
      node +: getSiblings(numbers.drop(node.definitionLength), numSiblings - 1)
    }
  }

}