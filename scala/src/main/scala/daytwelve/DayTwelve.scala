package daytwelve

import java.util

import scala.collection.convert.Wrappers.MutableMapWrapper
import scala.io.Source
import scala.language.postfixOps

object DayTwelve {

  var indexOffset = 0
  private val seenGenerations: util.ArrayList[String] = new util.ArrayList[String]()

  def main(args: Array[String]): Unit = {
    val (state, rules) = readRulesAndInitialState("./src/main/scala/daytwelve/day_twelve_input")

    val twentiethGen = getState(state, rules, 111)
    println(twentiethGen)
    println(s"Index offset: $indexOffset")

    val partOneAnswer = twentiethGen.indices.filter(twentiethGen(_) == '#').map(_ + indexOffset).sum
    println(partOneAnswer) // Part 1 = 1733

    val firstRep = "#....#.....#......#....#....#.....#.......#.......#.......#....#....#....#.......#.......#........#.........#....#.....#.......#"
    // gen 111 -> ind offset = 76
    // (50000000000 - 111) = 49999999889
    // final offset = 49999999965
    println(firstRep.indices.filter(firstRep(_) == '#').map(_ + 49999999965L).sum) // Part 2 = 1000000000508
  }

  def getState(state: String, rules: Map[String, Char], generation: Long): String = {
    if (generation == 0) state
    else {
      val next = nextGen(state, rules)
      if (seenGenerations.contains(next)) println(s"Seen $next at gen $generation")
      seenGenerations.add(next)
      getState(next, rules, generation - 1)
    }
  }

  def nextGen(state: String, rules: Map[String, Char]): String = {
    val ruleLength = rules.keys.head.length
    val bufferedState = "....." + state + "....."
    val bufferedNextGen = (0 until bufferedState.length - ruleLength).map(i => {
      val key = (i until ruleLength + i).map(bufferedState(_)).mkString
      if (rules.contains(key)) rules(key) else '.'
    }).mkString
    val newIndexOffset = bufferedNextGen.indexOf('#')
    indexOffset += (newIndexOffset - 3)
    bufferedNextGen.slice(newIndexOffset, bufferedNextGen.lastIndexOf('#') + 1)
  }

  def readRulesAndInitialState(filePath: String): (String, Map[String, Char]) = {
    val input = Source.fromFile(filePath).getLines.toArray
    val initialState = ("""initial state: ([#.]+)""".r findAllIn input.head).group(1)
    val rules = input.slice(2, input.length)
    val mappedRules = rules.map(r => {
      val matches = """([#.]+) => ([#.])""".r findAllIn r
      val pattern = matches.group(1)
      val result = matches.group(2).head
      pattern -> result
    }).toMap
    (initialState, mappedRules)
  }

}