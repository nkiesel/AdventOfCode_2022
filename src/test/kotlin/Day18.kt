import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day18 {
    private val sample = """
        2,2,2
        1,2,2
        3,2,2
        2,1,2
        2,3,2
        2,2,1
        2,2,3
        2,2,4
        2,2,6
        1,2,5
        3,2,5
        2,1,5
        2,3,5
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(listOf("1,1,1", "2,1,1")) shouldBe 10
        one(sample) shouldBe 64
        one(input) shouldBe 4418
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 58
        two(input) shouldBe 2486
    }

    data class Point(val x: Int, val y: Int, val z: Int) {
        fun neighbors() = setOf(
            Point(x + 1, y + 0, z + 0),
            Point(x - 1, y + 0, z + 0),
            Point(x + 0, y + 1, z + 0),
            Point(x + 0, y - 1, z + 0),
            Point(x + 0, y + 0, z + 1),
            Point(x + 0, y + 0, z - 1),
        )
    }

    private fun parse(input: List<String>): Set<Point> = input
        .map { l -> l.split(",").map(String::toInt).let { Point(it[0], it[1], it[2]) } }
        .toSet()

    private fun one(input: List<String>): Int {
        val points = parse(input)
        return points.sumOf { (it.neighbors() - points).size }
    }

    private fun two(input: List<String>): Int {
        val lava = parse(input)
        val rx = lava.minOf(Point::x)..lava.maxOf(Point::x)
        val ry = lava.minOf(Point::y)..lava.maxOf(Point::y)
        val rz = lava.minOf(Point::z)..lava.maxOf(Point::z)
        val water = mutableSetOf<Point>()
        for (x in rx) for (y in ry) for (z in rz) {
            val c = Point(x, y, z)
            if (c in lava) continue
            if (
                lava.none { it.x < x && it.y == y && it.z == z } ||
                lava.none { it.x > x && it.y == y && it.z == z } ||
                lava.none { it.x == x && it.y < y && it.z == z } ||
                lava.none { it.x == x && it.y > y && it.z == z } ||
                lava.none { it.x == x && it.y == y && it.z < z } ||
                lava.none { it.x == x && it.y == y && it.z > z }) {
                water += c
            }
        }

        var added = true
        while (added) {
            added = false
            for (x in rx) for (y in ry) for (z in rz) {
                val c = Point(x, y, z)
                if (c in lava || c in water) continue
                if ((c.neighbors() intersect water).isNotEmpty()) {
                    water += c
                    added = true
                }
            }
        }

        val air = buildSet {
            for (x in rx) for (y in ry) for (z in rz) {
                val c = Point(x, y, z)
                if (c !in lava && c !in water) add(c)
            }
        }

        return lava.sumOf { (it.neighbors() - lava).size } - air.sumOf { (it.neighbors() - air).size }
    }
}
