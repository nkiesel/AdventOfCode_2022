import kotlin.math.max
import kotlin.math.min
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

const val debug = false

class Day14 {

    private val sample = """
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 24
        one(input) shouldBe 1406
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 93
        two(input) shouldBe 20870
    }

    class Cave {
        val map = mutableMapOf<Pair<Int, Int>, Char>()
        var dropped = 0
        var minX = Int.MAX_VALUE
        var maxX = 0
        var minY = Int.MAX_VALUE
        var maxY = 0

        fun set(x: Int, y: Int, c: Char) {
            map[x to y] = c
            minX = min(x, minX)
            maxX = max(x, maxX)
            minY = min(y, minY)
            maxY = max(y, maxY)
        }

        fun get(x: Int, y: Int): Char {
            return map[x to y] ?: '.'
        }

        fun drop(c: Char): Boolean {
            var x = 500
            for (y in 0..maxY) {
                when {
                    get(x, y) == '=' -> {
                        minX -= 1
                        maxX += 1
                        set(minX, y, '=')
                        set(maxX, y, '=')
                    }

                    get(x, y) == '.' -> continue
                    get(x - 1, y) == '.' -> { x -= 1; continue }
                    get(x + 1, y) == '.' -> { x += 1; continue }
                }
                set(x, y - 1, c)
                dropped += 1
                return false
            }
            return true
        }

        fun draw(xy: Pair<Int, Int>? = null): Int {
            if (debug) {
                println(" -- $dropped --")
                var c: Char? = null
                if (xy != null) {
                    c = get(xy.first, xy.second)
                    set(xy.first, xy.second, '*')
                }
                println("x: $minX..$maxY y: $minY..$maxY")
                for (y in minY..maxY) {
                    for (x in minX..maxX) {
                        print(get(x, y))
                    }
                    println()
                }
                if (xy != null && c != null) {
                    set(xy.first, xy.second, c)
                }
            }
            return dropped
        }
    }

    private fun parse(input: List<String>): Cave {
        val cave = Cave()
        for (line in input) {
            for ((p1, p2) in line.split(" -> ").map { point -> point.split(",").map(String::toInt) }.windowed(2)) {
                for (x in min(p1[0], p2[0])..max(p1[0], p2[0])) {
                    for (y in min(p1[1], p2[1])..max(p1[1], p2[1])) {
                        cave.set(x, y, '#')
                    }
                }
            }
        }
        return cave
    }

    private fun one(input: List<String>): Int {
        val cave = parse(input)
        cave.draw()
        while (true) {
            if (cave.drop('o')) return cave.draw()
        }
    }

    private fun two(input: List<String>): Int {
        val cave = parse(input)
        val y = cave.maxY + 2
        for (x in (cave.minX - 1)..(cave.maxX + 1)) cave.set(x, y, '=')
        cave.draw()
        while (true) {
            if (cave.drop('o') || cave.get(500, 0) == 'o') return cave.draw()
        }
    }
}
/*
Two issues today: First, I did not realize that the paths could go up again, which resulted in me
missing some rocks. Secondly, I initially created a non-sparse matrix (optimized for containing
just the rocks plus 1 empty line around the map).  This was a bit faster for part 1, but made
handling the bottom line of part 2 much harder. Switching to a sparse map solved this problem
without a big performance penalty.
 */
