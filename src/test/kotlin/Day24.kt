import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day24 {
    private val sample = """
        #.######
        #>>.<^<#
        #.<..<<#
        #>v.><>#
        #<^v^^>#
        ######.#
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 18
        one(input) shouldBe 297
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 54
        two(input) shouldBe 856
    }

    private class Pos(
        var north: Boolean = false,
        var south: Boolean = false,
        var east: Boolean = false,
        var west: Boolean = false,
    ) {
        constructor(c: Char) : this(north = c == '^', south = c == 'v', east = c == '>', west = c == '<')

        fun isClear() = !north && !south && !east && !west

        fun char() = if (north) '^' else if (south) 'v' else if (east) '>' else if (west) '<' else '.'
    }

    private data class Elf(val x: Int, val y: Int)

    private class Valley(val startPos: Elf, val endPos: Elf, val cols: Int, val rows: Int, val map: List<Pos>)

    private class Travel(val cols: Int, val rows: Int, map: List<Pos>) {
        private val maxBlizzards = lcm(rows, cols)

        private val maps = generateSequence(map) { advance(it) }.iterator()
        private val blizzards = mutableMapOf(0 to maps.next())
        private var lastBlizzard = 0

        private fun valley(i: Int): List<Pos> {
            val k = i % maxBlizzards
            if (k > lastBlizzard) {
                for (j in (lastBlizzard + 1)..k) blizzards[j] = maps.next()
                lastBlizzard = k
            }
            return blizzards[k]!!
        }

        private fun List<Pos>.pos(x: Int, y: Int): Pos {
            return get(x.mod(cols) + y.mod(rows) * cols)
        }

        fun p(i: Int) {
            fun c(b: Boolean) = if (b) 1 else 0
            with(valley(i)) {
                println("----- $i ------")
                for (y in 0 until rows) {
                    for (x in 0 until cols) {
                        val p = pos(x, y)
                        val c = c(p.north) + c(p.south) + c(p.east) + c(p.west)
                        print(if (c > 1) c else p.char())
                    }
                    println()
                }
                println()
            }
        }

        private fun advance(l: List<Pos>): List<Pos> {
            val next = List(rows * cols) { Pos() }
            for (x in 0 until cols) {
                for (y in 0 until rows) {
                    val p = l.pos(x, y)
                    if (p.north) next.pos(x, y - 1).north = true
                    if (p.south) next.pos(x, y + 1).south = true
                    if (p.east) next.pos(x + 1, y).east = true
                    if (p.west) next.pos(x - 1, y).west = true
                }
            }
            return next
        }

        fun move(start: Int, startPos: Elf, endPos: Elf): Int {
            data class Candidate(val minute: Int, val elf: Elf)

            val r = bfs(Candidate(start, startPos)) { candidate ->
                val next = candidate.minute + 1
                val valley = valley(next)
                listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1, 0 to 0).map { (dx, dy) -> Elf(candidate.elf.x + dx, candidate.elf.y + dy) }
                    .filter {
                        it == startPos || it == endPos ||
                            (it.x in 0 until cols && it.y in 0 until rows && valley.pos(it.x, it.y).isClear())
                    }.sortedBy { manhattanDistance(it.x, it.y, endPos.x, endPos.y) }.map { Candidate(next, it) }
            }.find { it.value.elf == endPos }
            return r?.value?.minute ?: -1
        }
    }

    private fun parse(input: List<String>): Valley {
        val cols = input[0].length - 2
        val rows = input.size - 2
        return Valley(
            Elf(input.first().indexOf('.') - 1, -1),
            Elf(input.last().indexOf('.') - 1, rows),
            cols,
            rows,
            input.drop(1).dropLast(1).flatMap { l -> l.drop(1).dropLast(1).map { Pos(it) } },
        )
    }


    private fun one(input: List<String>): Int {
        val valley = parse(input)
        val travel = Travel(valley.cols, valley.rows, valley.map)
        return travel.move(0, valley.startPos, valley.endPos)
    }

    private fun two(input: List<String>): Int {
        val valley = parse(input)
        val travel = Travel(valley.cols, valley.rows, valley.map)
        val move1 = travel.move(0, valley.startPos, valley.endPos)
        val move2 = travel.move(move1, valley.endPos, valley.startPos)
        return travel.move(move2, valley.startPos, valley.endPos)
    }
}

/*
Part 1 was pretty simple. But I struggled with part 2, mostly because I had 1-off
bugs. I finally solved it by simplifying the state for bfs to just include the
minute and the elf position, and keeping the valley maps in a global variable.
A sneak peek at Slack then also made me realize that there is a maximum number of
possible maps.
*/
