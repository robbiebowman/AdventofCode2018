import java.io.File

fun main(args: Array<String>) {
    val input = mutableListOf<String>()
    val seenSums = mutableSetOf<Int>()
    var sum = 0
    File("./src/Day 1/input").forEachLine { input.add(it) }
    val ints = input.map { it.toInt() }

    println(ints.sum()) // Part 1 = 505

    var i = 0
    while (true) {
        sum += ints.get(i)
        if (seenSums.contains(sum)) {
            println(sum) // Part 2 = 72330
            break
        } else {
            seenSums.add(sum)
        }
        if (i == ints.size - 1) {
            i = 0
        } else {
            i++
        }
    }
}
