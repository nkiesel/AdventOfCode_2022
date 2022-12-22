import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day22 {
    private val sample = """
        ...#
        .#..
        #...
        ....
...#.......#
........#...
..#....#....
..........#.
        ...#....
        .....#..
        .#......
        ......#.

10R5L5R10L4R5L5

    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 6032
        one(input) shouldBe 89224
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }

    data class Board(val map: List<String>, val path: String) {
        var x: Int = map[0].indexOf('.')
        var y: Int = 0
        var dx: Int = 1
        var dy: Int = 0
        val i = path.iterator()

        private fun nx(): Int {
            if (dx == 0) return x
            var nx = (x + dx).mod(map[y].length)
            while (map[y][nx] == ' ') {
                nx = (nx + dx).mod(map[y].length)
            }
            return nx
        }

        private fun ny(): Int {
            if (dy == 0) return y
            var ny = (y + dy).mod(map.size)
            while (map[ny].lastIndex < x || map[ny][x] == ' ') {
                ny = (ny + dy).mod(map.size)
            }
            return ny
        }

        private fun step(): Boolean {
            val nx = nx()
            val ny = ny()
            if (map[ny][nx] == '#') {
                return false
            }
            x = nx
            y = ny
            return true
        }

        private fun move(n: Int) {
            repeat(n) {
                if (!step()) return
            }
        }

        private fun turn(c: Char) {
            when (c) {
                'L' -> when (dx to dy) {
                    1 to 0 -> { dx = 0; dy = -1 }
                    -1 to 0 -> { dx = 0; dy = 1}
                    0 to 1 -> { dx = 1; dy = 0 }
                    0 to -1 -> { dx = -1 ; dy = 0 }
                }
                'R' -> when (dx to dy) {
                    1 to 0 -> { dx = 0; dy = 1 }
                    -1 to 0 -> { dx = 0; dy = -1}
                    0 to 1 -> { dx = -1; dy = 0 }
                    0 to -1 -> { dx = 1 ; dy = 0 }
                }
            }
        }

        fun followPath() {
            var steps = 0
            while (i.hasNext()) {
                when (val c = i.next()) {
                    'L', 'R' -> { move(steps); p(); steps = 0; turn(c) }
                    else -> { steps = steps * 10 + c.digitToInt() }
                }
            }
            move(steps)
            p()
        }

        private fun p() {
//            println("x=${x + 1} y = ${y + 1} dx = $dx dy = $dy")
        }
    }

    private fun parse(input: List<String>) : Board {
        val (b, p) = input.chunkedBy { it.isEmpty() }
        return Board(b, p.first())
    }

    private fun one(input: List<String>): Int {
        val board = parse(input)
        board.followPath()
        return 1000 * (board.y + 1) + 4 * (board.x + 1) + when (board.dx to board.dy) {
            1 to 0 -> 0
            0 to 1 -> 1
            -1 to 0 -> 2
            0 to -1 -> 3
            else -> error("Invalid board")
        }
    }

    private fun two(input: List<String>): Int {
        return 0
    }
}
