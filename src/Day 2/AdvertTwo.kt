import java.io.File

fun main(args: Array<String>) {
    val input = mutableListOf<String>()
    File("./src/Day 2/input").forEachLine { input.add(it) }

    val pairs = input.count { containsSameLetters(it, 2) }
    val triplets = input.count { containsSameLetters(it, 3) }

    println(pairs * triplets) // Part 1 = 6944

    val idsOfOneDiff = input.map { code -> input.filter { distance(it, code) == 1 } }.flatten()
    val commonChars = idsOfOneDiff.first()
        .zip(idsOfOneDiff.last())
        .filter { it.first == it.second }
        .map { it.first }
        .fold("") { acc, c -> acc.plus(c) }
    println(commonChars) // Part 2 = srijafjzloguvlntqmphenbkd
}

fun containsSameLetters(input: String, numLetters: Int): Boolean {
    val chars = input.toCharArray()
    chars.forEach { c -> if (chars.count { it == c } == numLetters) return true }
    return false
}

fun distance(first: String, second: String): Int = first.zip(second).map { if (it.first == it.second) 0 else 1 }.sum()