import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day15 {
    private val sample = """
        Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        Sensor at x=9, y=16: closest beacon is at x=10, y=16
        Sensor at x=13, y=2: closest beacon is at x=15, y=3
        Sensor at x=12, y=14: closest beacon is at x=10, y=16
        Sensor at x=10, y=20: closest beacon is at x=10, y=16
        Sensor at x=14, y=17: closest beacon is at x=10, y=16
        Sensor at x=8, y=7: closest beacon is at x=2, y=10
        Sensor at x=2, y=0: closest beacon is at x=2, y=10
        Sensor at x=0, y=11: closest beacon is at x=2, y=10
        Sensor at x=20, y=14: closest beacon is at x=25, y=17
        Sensor at x=17, y=20: closest beacon is at x=21, y=22
        Sensor at x=16, y=7: closest beacon is at x=15, y=3
        Sensor at x=14, y=3: closest beacon is at x=15, y=3
        Sensor at x=20, y=1: closest beacon is at x=15, y=3
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample, 10) shouldBe 26
        one(input, 2000000) shouldBe 5108096
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample, 20) shouldBe 56000011
        two(input, 4000000) shouldBe 10553942650264L
    }

    private data class Sensor(val sx: Int, val sy: Int, val bx: Int, val by: Int) {
        val scope = manhattanDistance(sx, sy, bx, by)
        val xRange = (sx - scope)..(sx + scope)

        fun noBeacon(x: Int, y: Int): Boolean {
            return !(x == bx && y == by) && ((x == sx && y == sy) || manhattanDistance(sx, sy, x, y) <= scope)
        }

        fun plusOne(): Set<Pair<Int, Int>> {
            return buildSet {
                for (r in 0..scope + 1) {
                    add(sx + r to sy + scope + 1 - r)
                    add(sx - r to sy - scope - 1 + r)
                    add(sx + scope + 1 - r to sy + r)
                    add(sx - scope - 1 + r to sy - r)
                }
            }
        }

    }

    private fun parse(input: List<String>): List<Sensor> {
        val re = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
        return input.map { l -> re.matchEntire(l)!!.groupValues.drop(1).map(String::toInt).let { Sensor(it[0], it[1], it[2], it[3]) } }
    }

    private fun one(input: List<String>, y: Int): Int {
        val sensors = parse(input)
        val minX = sensors.minOf { it.xRange.first }
        val maxX = sensors.maxOf { it.xRange.last }
        return (minX..maxX).count { x -> sensors.any { it.noBeacon(x, y) } }
    }

    private fun two(input: List<String>, f: Int): Long {
        val sensors = parse(input)
        val knownBeacons = sensors.map { it.bx to it.by }.toSet()
        val candidates = sensors.flatMap { it.plusOne() }.filter { it.first in 0..f && it.second in 0..f }.toSet() - knownBeacons
        val beacon = candidates.first { c -> sensors.none { it.noBeacon(c.first, c.second) } }
        return beacon.first.toLong() * 4_000_000L + beacon.second.toLong()
    }
}
/*
Part 2 was hard.  I _finally_ realized that if there is exactly one possible position for the beacon,
then it must be just on the outside of all the sensor ranges. Therefore, the candidates can be reduced to
the set of positions of these rims/circles.  Still runs for 30 seconds, so there is likely more
optimizations possible. However, getting too late now...
 */
