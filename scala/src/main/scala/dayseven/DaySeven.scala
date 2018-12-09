package dayseven

import scala.io.Source

object DaySeven {

  // Puzzle constants
  val singleTaskTime = 60
  val workerCount = 5

  def main(args: Array[String]): Unit = {
    val allSteps = readSteps("./src/main/scala/dayseven/day_seven_input")

    println(orderedSteps(allSteps).map(p => p._1).mkString("")) // Part 1 = GLMVWXZDKOUCEJRHFAPITSBQNY

    val rota = Map[Char, Int]()
    println(totalTaskTime(allSteps, rota)) // Part 2 = 1105
  }

  def totalTaskTime(stepsLeft: Map[Char, List[Char]], rota: Map[Char, Int]): Int = {
    if (stepsLeft.isEmpty) {
      0
    } else {
      val workableSteps = possibleSteps(stepsLeft withInProgress rota)

      if (workableSteps.isEmpty || !rota.hasAvailableWorkers) { // Wait for next completion
        val soonest = rota.minBy(_._2)
        val newRota = rota withCompleted soonest
        rota(soonest._1) + totalTaskTime(
          stepsLeft withCompleted soonest._1,
          newRota
        )
      } else { // Assign new worker
        val nextStep = workableSteps.keys.min
        totalTaskTime(
          stepsLeft,
          rota + (nextStep -> (singleTaskTime + (nextStep.toInt - 64)))
        )
      }
    }
  }

  def orderedSteps(stepsLeft: Map[Char, List[Char]]): List[(Char, List[Char])] = {
    if (stepsLeft.nonEmpty) {
      val first = nextStep(stepsLeft)
      first +: orderedSteps(stepsLeft withCompleted first._1)
    } else List()
  }

  def nextStep(steps: Map[Char, List[Char]]): (Char, List[Char]) = {
    possibleSteps(steps).minBy(s => s._1)
  }

  def possibleSteps(steps: Map[Char, List[Char]]): Map[Char, List[Char]] = {
    steps.filter(s => s._2.isEmpty)
  }

  implicit class StepMap(steps: Map[Char, List[Char]]) {
    def withCompleted(step: Char): Map[Char, List[Char]] = {
      (steps - step).map(f => f._1 -> f._2.filterNot(c => c == step))
    }

    def withInProgress(rota: Map[Char, Int]): Map[Char, List[Char]] = {
      steps.filterKeys(k => !rota.keys.exists(_ == k))
    }
  }

  implicit class RotaMap(rota: Map[Char, Int]) {
    def hasAvailableWorkers: Boolean = {
      rota.keys.size < workerCount
    }

    def withCompleted(assignment: (Char, Int)): Map[Char, Int] = {
      val taskTime = rota(assignment._1)
      rota.map(e => e._1 -> (e._2 - taskTime)).filter(p => p._2 > 0)
    }
  }

  def readSteps(filePath: String): Map[Char, List[Char]] = {
    val preReqs = Source.fromFile(filePath).getLines.map(lineToPreReq)
    val stepsWithPreReq = preReqs.foldLeft(Map[Char, List[Char]]()) {
      (map, preReq) =>
        if (map.contains(preReq.subject))
          map + (preReq.subject -> (map(preReq.subject) :+ preReq.req))
        else
          map + (preReq.subject -> List[Char](preReq.req))
    }
    stepsWithNoPreReqs(stepsWithPreReq).foldLeft(stepsWithPreReq) { (map, c) => map + (c -> List[Char]()) }
  }

  def stepsWithNoPreReqs(stepsWithPreReq: Map[Char, List[Char]]): Iterable[Char] = {
    val allSteps = (stepsWithPreReq.values.flatten.toList ++ stepsWithPreReq.keys.toList).distinct
    allSteps.filterNot(stepsWithPreReq.keys.toList.contains)
  }

  def lineToPreReq(l: String): PreReq = {
    val stepDef = "Step (\\w) must be finished before step (\\w) can begin." r
    val matches = stepDef findAllIn l
    PreReq(matches.group(2).charAt(0), matches.group(1).charAt(0))
  }

}

case class PreReq(subject: Char, req: Char)