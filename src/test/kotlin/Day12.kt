import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day12 {
    private val sample = """
        Sabqponm
        abcryxxl
        accszExk
        acctuvwj
        abdefghi
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 31
        one(input) shouldBe 391
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 29
        two(input) shouldBe 386
    }

    class Parsed(val graph: Map<String, List<String>>, val start: String, val end: String, val lowest: List<String>)

    private fun parse(input: List<String>, ps: Char = 'S'): Parsed {
        val map = input.map { it.toCharArray() }.toTypedArray()
        val neighbors = mutableMapOf<String, List<String>>()
        val lowest = mutableListOf<String>()
        var start = ""
        var end = ""

        data class Square(val point: Pair<Int, Int>) {
            val name = "${point.first}|${point.second}"
            val char = map[point.second][point.first]
            val height = when (char) {
                'S' -> 'a'.also { start = name }
                'E' -> 'z'.also { end = name}
                else -> char
            }
        }

        for (x in input[0].indices) {
            for (y in input.indices) {
                val p = Square(x to y)
                if (p.height == ps) lowest += p.name
                neighbors[p.name] = map
                    .neighbors4(p.point)
                    .map { Square(it) }
                    .filter { it.height != ps && it.height - p.height <= 1 }
                    .map { it.name }
            }
        }

        return Parsed(neighbors, start, end, lowest)
    }

    private fun one(input: List<String>): Int {
        val g = parse(input)
        return bfs(g.start) { g.graph.getValue(it) }.firstOrNull { it.value == g.end }?.index ?: Int.MAX_VALUE
    }

    private fun two(input: List<String>): Int {
        val g = parse(input, 'a')
        return g.lowest.minOf { s -> bfs(s) { g.graph.getValue(it) }.firstOrNull { it.value == g.end }?.index ?: Int.MAX_VALUE }
    }
}

/*
I _knew_ a "shortest path" would again be required, so I should have been better prepared.
I spent some time finding a nice Kotlin implementation on the net, but turned out that the
implementation was slightly buggy (used `!==` instead of `!=` which does not work for strings
in the intended way).

The second part I for now simply brut-forced. Ran for nearly 2 minutes, but produced the
correct result.  I guess one optimization is that any path which contains an additional 'a'
(apart from the start) can be abandoned, because it cannot be the shortest path. However,
I don't think that my current implementation allows to use this insight.

Update: I switched the implementation from Dijkstra to breadth-first search (aka bfs) because
we don't have weighted graphs. Together with implementing the "avoid paths containing 'a'" as
described above, this brought down the execution time to about 100 ms.  Another solution would
be to search backwards from 'E' to 'a' which would be much faster, but that would require changing
my "convert map into edges" approach.
 */
