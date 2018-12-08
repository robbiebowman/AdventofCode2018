import scala.io.Source

object DaySeven {

  def main(args: Array[String]): Unit = {
    val preReqs = Source.fromFile("./src/main/scala/day_seven_input").getLines.map(lineToPreReq)
    val stepsWithPreReq = preReqs.foldLeft(Map[Char, List[Char]]()) {
      (map, preReq) =>
        if (map.contains(preReq.subject))
          map + (preReq.subject -> (map(preReq.subject) :+ preReq.req))
        else
          map + (preReq.subject -> List[Char](preReq.req))
    }
    val allSteps = stepsWithNoPreReqs(stepsWithPreReq).foldLeft(stepsWithPreReq){ (map, c) => map + (c -> List[Char]())}

    println(orderedSteps(allSteps)) // Part 1 = GLMVWXZDKOUCEJRHFAPITSBQNYGLMVWXZDKOUCEJRHFAPITSBQNY
  }

  def orderedSteps(stepsLeft: Map[Char, List[Char]]): String = {
    if (stepsLeft.keys.nonEmpty) {
      val first = firstStep(stepsLeft)
      print(first)
      first + orderedSteps(completeStep(first, stepsLeft))
    } else ""
  }

  def completeStep(step: Char, steps: Map[Char, List[Char]]): Map[Char, List[Char]] = {
    (steps - step).map(f => f._1 -> f._2.filterNot(c => c == step))
  }

  def stepsWithNoPreReqs(stepsWithPreReq: Map[Char, List[Char]]): Iterable[Char] = {
    val allSteps = (stepsWithPreReq.values.flatten.toList ++ stepsWithPreReq.keys.toList).distinct
    allSteps.filterNot(stepsWithPreReq.keys.toList.contains)
  }

  def possibleSteps(steps: Map[Char, List[Char]]): Iterable[Char] = {
    (steps.values.flatten ++ steps.keys).toList.distinct.filter(c => steps(c).isEmpty)
  }

  def firstStep(steps: Map[Char, List[Char]]): Char = {
    possibleSteps(steps).min
  }

  def lineToPreReq(l: String): PreReq = {
    val stepDef = "Step (\\w) must be finished before step (\\w) can begin." r
    val matches = stepDef findAllIn l
    PreReq(matches.group(2).charAt(0), matches.group(1).charAt(0))
  }
}

case class PreReq(subject: Char, req: Char)