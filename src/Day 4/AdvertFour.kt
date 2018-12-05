import java.io.File
import java.util.*

data class Record(val date: Date, val guardId: Int, val action: GuardAction)

enum class GuardAction {
    WakesUp, BeginsShift, FallsAsleep
}

fun main(args: Array<String>) {
    val records = getRecordsFromFile("./src/Day 4/input")

    val id = guardWhoSleepsMost(records)
    val time = sleepiestMinute(id, records)
    println(id * time!!) // Part 1 = 19830

    val mostConsistentlySleepingGuardInfo = records.groupBy { it.guardId }.map {
        val sleepiestMinute = sleepiestMinute(it.key, it.value)
        Triple(it.key, sleepiestMinute, timesAsleepByMinute(it.key, it.value).get(sleepiestMinute))
    }.maxBy { it.third ?: 0 }!!

    println(mostConsistentlySleepingGuardInfo.first * mostConsistentlySleepingGuardInfo.second!!) // Part 2 = 43695
}

fun sleepiestMinute(sleepyGuard: Int, records: List<Record>): Int? {
    return timesAsleepByMinute(sleepyGuard, records).maxBy { it.value }?.key
}

fun timesAsleepByMinute(guardId: Int, records: List<Record>): Map<Int, Int> { //Map<Minute, NumTimesAsleep>
    val timesAsleepAtMinute = HashMap<Int, Int>()
    val sleepyGuardRecords = records.filter { it.guardId == guardId }
    var i = 0
    sleepyGuardRecords.forEach {
        if (it.action == GuardAction.WakesUp) {
            (sleepyGuardRecords[i - 1].date.minutes until sleepyGuardRecords[i].date.minutes).forEach { min ->
                timesAsleepAtMinute[min] = (timesAsleepAtMinute[min] ?: 0) + 1
            }
        }
        i++
    }
    return timesAsleepAtMinute
}

fun guardWhoSleepsMost(records: List<Record>): Int {
    val sleepTimes = HashMap<Int, Int>()
    var i = 0
    records.forEach {
        if (it.action == GuardAction.WakesUp) {
            sleepTimes[it.guardId] = (sleepTimes[it.guardId] ?: 0) + (it.date.minutes - records[i - 1].date.minutes)
        }
        i++
    }
    return sleepTimes.maxBy { it.value }!!.key
}

fun getRecordsFromFile(filePath: String): List<Record> {
    val recordRegex = """\[(\d+)-(\d+)-(\d+) (\d+):(\d+)] ([\w #\d]+)""".toRegex()
    val lines = mutableListOf<String>()
    File(filePath).forEachLine { lines.add(it) }
    var currentGuardId = 0
    return lines.sorted().map {
        val matches = recordRegex.find(it)!!.groupValues.drop(1)
        val date =
            Date(matches[0].toInt(), matches[1].toInt(), matches[2].toInt(), matches[3].toInt(), matches[4].toInt())
        val action = when (matches[5]) {
            "wakes up" -> GuardAction.WakesUp
            "falls asleep" -> GuardAction.FallsAsleep
            else -> GuardAction.BeginsShift.also {
                currentGuardId = """Guard #(\d+)""".toRegex().find(matches[5])!!.groupValues.drop(1).first().toInt()
            }
        }
        Record(date, currentGuardId, action)
    }
}