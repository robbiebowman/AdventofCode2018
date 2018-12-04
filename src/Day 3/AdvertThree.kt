import java.io.File

data class Fabric(val id: Int? = null, val x: Int, val y: Int, val width: Int, val height: Int)

fun main(args: Array<String>) {
    val fabrics = getFabricsFromFile("./src/Day 3/input")

    println(numSquaresWithOverlap(fabrics)) // Part 1 = 120408

    println(fabrics.first { f -> fabrics.minus(f).all { intersectingFabric(it, f) == null } }.id) // Part 2 = 1276
}

fun numSquaresWithOverlap(fabrics: List<Fabric>): Int {
    val squaresWithOverlappingFabric = mutableSetOf<Pair<Int, Int>>()
    val seenFabrics = mutableListOf<Fabric>()
    fabrics.forEach { f ->
        seenFabrics.forEach { squaresWithOverlappingFabric.addAll(squaresThatOverlap(it, f)) }
        seenFabrics.add(f)
    }
    return squaresWithOverlappingFabric.size
}

fun squaresThatOverlap(f1: Fabric, f2: Fabric): Set<Pair<Int, Int>> {
    val fabric = intersectingFabric(f1, f2)
    return if (fabric != null) squaresOfFabric(fabric) else setOf()
}

fun squaresOfFabric(fabric: Fabric): Set<Pair<Int, Int>> {
    return (fabric.x + 1..fabric.x + fabric.width).map { x -> (fabric.y + 1..fabric.y + fabric.height).map { y -> Pair(x, y) } }.flatten().toSet()
}

fun intersectingFabric(f1: Fabric, f2: Fabric): Fabric? {
    val x1 = Math.max(f1.x, f2.x)
    val y1 = Math.max(f1.y, f2.y)
    val x2 = Math.min(f1.x + f1.width, f2.x + f2.width)
    val y2 = Math.min(f1.y + f1.height, f2.y + f2.height)
    return if (x1 < x2 && y1 < y2) Fabric(x = x1, y = y1, width = x2 - x1, height = y2 - y1) else null
}

fun getFabricsFromFile(filePath: String): MutableList<Fabric> {
    val fabrics = mutableListOf<Fabric>()
    val fabricRegex = """#(\d+) @ (\d+),(\d+): (\d+)x(\d+)""".toRegex()
    File(filePath).forEachLine {
        val matches = fabricRegex.find(it)!!.groupValues
        val fabricValues = matches.slice(1 until matches.size).map { match -> match.toInt() }
        fabrics.add(Fabric(fabricValues[0], fabricValues[1], fabricValues[2], fabricValues[3], fabricValues[4]))
    }
    return fabrics
}