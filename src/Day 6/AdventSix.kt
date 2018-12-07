import java.io.File

fun main(args: Array<String>) {
    val coordinates = mutableListOf<Pair<Int, Int>>()
    File("./src/Day 6/input").forEachLine {
        coordinates.add(
            Pair(
                it.split(", ").first().toInt(),
                it.split(", ").last().toInt()
            )
        )
    }

    val maxX = coordinates.maxBy { it.first }!!.first
    val minX = coordinates.minBy { it.first }!!.first
    val maxY = coordinates.maxBy { it.second }!!.second
    val minY = coordinates.minBy { it.second }!!.second

    val squares = (minX..maxX).map { x -> (minY..maxY).map { y -> Pair(x, y) } }.flatten()
    val areas = squares.groupBy { closest(it, coordinates) }.filterNot { it.key == null }
    val finiteAreas = areas.filterNot {
        it.value.any { p ->
            p.first == minX || p.first == maxX || p.second == minY || p.second == maxY
        }
    }
    val biggestArea = finiteAreas.maxBy { it.value.count() }
    println("x: ${biggestArea!!.key!!.first}, y: ${biggestArea.key!!.second}, area: ${biggestArea.value.size}") // Part 1 = 4166

    println(squares.filter { s -> coordinates.map { c -> dist(c, s) }.sum() < 10000 }.size) // Part 2 = 42250
}

fun closest(p: Pair<Int, Int>, ps: Collection<Pair<Int, Int>>): Pair<Int, Int>? {
    val firstMin = ps.minBy { dist(it, p) }!!
    val secondMin = ps.minus(firstMin).minBy { dist(it, p) }!!
    return if (dist(firstMin, p) == dist(secondMin, p)) null else firstMin
}

fun dist(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Int {
    return Math.abs(p1.first - p2.first) + Math.abs(p1.second - p2.second)
}