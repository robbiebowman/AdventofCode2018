import java.io.File

fun main(args: Array<String>) {
    var inputPolymer = ""
    File("./src/Day 5/input").forEachLine { inputPolymer = it }

    println(reducePolymer(inputPolymer).length) // Part 1 = 9686

    println(('a'..'z').map { reducePolymer(inputPolymer.filter { c -> c.toLowerCase() != it }).length }.min()) // Part 2 = 5524
}

fun reducePolymer(polymer: String): String {
    var p = polymer
    while (true) {
        val reaction = (0 until p.length)
            .firstOrNull { i ->
                i != p.length - 1 && mersReact(p[i], p[i + 1])
            } ?: break
        p = p.slice(0 until reaction).plus(p.slice(reaction + 2 until p.length))
    }
    return p
}

fun mersReact(first: Char, second: Char): Boolean {
    return if (first.isUpperCase()) {
        second.isLowerCase() && second.toUpperCase() == first
    } else {
        second.isUpperCase() && first.toUpperCase() == second
    }
}
