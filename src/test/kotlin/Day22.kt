import kotlin.math.sqrt
import Day22.Facing.D
import Day22.Facing.L
import Day22.Facing.R
import Day22.Facing.U
import Day22.Side.S1
import Day22.Side.S2
import Day22.Side.S3
import Day22.Side.S4
import Day22.Side.S5
import Day22.Side.S6
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
        two(sample, true) shouldBe 5031
        two(input, false) shouldBe 136182
    }

    class Board1(val map: List<String>, path: String) {
        private var x: Int = map[0].indexOf('.')
        private var y: Int = 0
        private var dx: Int = 1
        private var dy: Int = 0
        private val i = path.iterator()

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
                'L' -> if (dx == 0) {
                    dx = dy
                    dy = 0
                } else {
                    dy = -dx
                    dx = 0
                }

                'R' -> if (dx == 0) {
                    dx = -dy
                    dy = 0
                } else {
                    dy = dx
                    dx = 0
                }
            }
        }

        fun followPath(): Int {
            var steps = 0
            while (i.hasNext()) {
                when (val c = i.next()) {
                    'L', 'R' -> {
                        move(steps); p(); steps = 0; turn(c)
                    }

                    else -> {
                        steps = steps * 10 + c.digitToInt()
                    }
                }
            }
            move(steps)
            p()
            return 1000 * (y + 1) + 4 * (x + 1) + when (dx to dy) {
                1 to 0 -> 0
                0 to 1 -> 1
                -1 to 0 -> 2
                0 to -1 -> 3
                else -> error("Invalid board")
            }
        }

        private fun p() {
            //            println("x=${x + 1} y = ${y + 1} dx = $dx dy = $dy")
        }
    }

    enum class Facing { U, D, L, R }

    enum class Side { S1, S2, S3, S4, S5, S6 }

    class Board2(map: List<String>, path: String, val sample: Boolean) {
        private val cs = sqrt((map.maxOf { it.length } * map.size / 2 / 6).toFloat()).toInt()
        private val range = 0 until cs
        private var side = S1
        private var x: Int = 0
        private var y: Int = 0
        private var facing = R
        private val moves = Regex("""\d+""").findAll(path).map { it.value.toInt() }
        private val turns = Regex("""[LR]""").findAll(path).map { it.value[0] }.iterator()
        private val cMap = if (sample) mapOf(
            S1 to (2 to 0),
            S2 to (0 to 1),
            S3 to (1 to 1),
            S4 to (2 to 1),
            S5 to (2 to 2),
            S6 to (3 to 2),
        ) else mapOf(
            S1 to (1 to 0),
            S2 to (2 to 0),
            S3 to (1 to 1),
            S4 to (1 to 2),
            S5 to (0 to 2),
            S6 to (0 to 3),
        )
        private val cube = cube(map)

        private fun cube(map: List<String>): Map<Side, List<String>> {
            return cMap.mapValues { (_, v) ->
                map
                    .slice(v.second * cs until (v.second + 1) * cs)
                    .map { it.slice(v.first * cs until (v.first + 1) * cs) }
            }
        }

        private fun coordinates() = with(cMap[side]!!) { x + first * cs to y + second * cs }

        class Attempt(val side: Side, private val facing: Facing, val x: Int, val y: Int) {
            fun set(board: Board2) {
                board.side = side
                board.facing = facing
                board.x = x
                board.y = y
            }
        }

        private fun nextSide(nx: Int, ny: Int): Attempt {
            val x = nx.mod(cs)
            val y = ny.mod(cs)
            val lx = range.last
            val rx = range.first
            val ix = range.last - x
            val uy = range.last
            val dy = range.first
            val iy = range.last - y
            return when (side) {
                S1 -> if (sample) when (facing) {
                    R -> Attempt(S6, L, lx, ix)
                    L -> Attempt(S3, D, y, dy)
                    U -> Attempt(S2, D, ix, dy)
                    D -> Attempt(S4, D, x, 0)
                } else when(facing) {
                    R -> Attempt(S2, R, rx, y)
                    L -> Attempt(S5, R, rx, iy)
                    U -> Attempt(S6, R, rx, x)
                    D -> Attempt(S3, D, x, dy)
                }

                S2 -> if (sample) when (facing) {
                    R -> Attempt(S3, R, rx, y)
                    L -> Attempt(S6, U, iy, uy)
                    D -> Attempt(S5, U, ix, uy)
                    U -> Attempt(S1, D, ix, dy)
                } else when (facing) {
                    R -> Attempt(S4, L, lx, iy)
                    L -> Attempt(S1, L, lx, y)
                    D -> Attempt(S3, L, lx, x)
                    U -> Attempt(S6, U, x, uy)
                }

                S3 -> if (sample) when (facing) {
                    R -> Attempt(S4, R, rx, y)
                    L -> Attempt(S2, L, lx, y)
                    D -> Attempt(S5, R, rx, ix)
                    U -> Attempt(S1, R, rx, x)
                } else when (facing) {
                    R -> Attempt(S2, U, y, uy)
                    L -> Attempt(S5, D, y, dy)
                    D -> Attempt(S4, D, x, dy)
                    U -> Attempt(S1, U, x, uy)
                }

                S4 -> if (sample) when (facing) {
                    R -> Attempt(S6, D, iy, dy)
                    L -> Attempt(S3, L, lx, y)
                    D -> Attempt(S5, D, x, dy)
                    U -> Attempt(S1, U, x, uy)
                } else when (facing) {
                    R -> Attempt(S2, L, lx, iy)
                    L -> Attempt(S5, L, lx, y)
                    D -> Attempt(S6, L, lx, x)
                    U -> Attempt(S3, U, x, uy)
                }

                S5 -> if (sample) when (facing) {
                    R -> Attempt(S6, R, rx, y)
                    L -> Attempt(S3, U, ix, uy)
                    D -> Attempt(S2, U, ix, uy)
                    U -> Attempt(S4, U, x, uy)
                } else when (facing) {
                    R -> Attempt(S4, R, rx, y)
                    L -> Attempt(S1, R, rx, iy)
                    D -> Attempt(S6, D, x, dy)
                    U -> Attempt(S3, R, rx, x)
                }

                S6 -> if (sample) when (facing) {
                    R -> Attempt(S1, L, lx, ix)
                    L -> Attempt(S5, L, lx, y)
                    D -> Attempt(S2, R, rx, ix)
                    U -> Attempt(S4, L, lx, ix)
                } else when (facing) {
                    R -> Attempt(S4, U, y, uy)
                    L -> Attempt(S1, D, y, dy)
                    D -> Attempt(S2, D, x, dy)
                    U -> Attempt(S5, U, x, uy)
                }
            }
        }

        private fun step(): Boolean {
            val nx = when (facing) {
                R -> x + 1
                L -> x - 1
                else -> x
            }
            val ny = when (facing) {
                U -> y - 1
                D -> y + 1
                else -> y
            }
            val a = if (nx in range && ny in range) {
                Attempt(side, facing, nx, ny)
            } else {
                nextSide(nx, ny)
            }
            if (cube[a.side]!![a.y][a.x] == '#') {
                return false
            }
            a.set(this)
            return true
        }

        private fun move(n: Int) {
            repeat(n) {
                if (!step()) return
            }
        }

        private fun turn(c: Char) {
            facing = when (c) {
                'L' -> when (facing) {
                    L -> D
                    R -> U
                    U -> L
                    D -> R
                }

                'R' -> when (facing) {
                    L -> U
                    R -> D
                    U -> R
                    D -> L
                }

                else -> error("Unexpected turn")
            }
        }

        fun followPath(): Int {
            for (steps in moves) {
                move(steps)
                if (turns.hasNext()) turn(turns.next())
            }
            val (fx, fy) = coordinates()
            return 1000 * (fy + 1) + 4 * (fx + 1) + when (facing) {
                R -> 0
                D -> 1
                L -> 2
                U -> 3
            }
        }
    }

    private fun one(input: List<String>): Int {
        val (b, p) = input.chunkedBy { it.isEmpty() }
        val board = Board1(b, p.first())
        return board.followPath()
    }

    private fun two(input: List<String>, sample: Boolean): Int {
        val (b, p) = input.chunkedBy { it.isEmpty() }
        val board = Board2(b, p.first(), sample)
        return board.followPath()
    }
}
/*
Oh man, this took a very long time (especially part 2), and involved assembling
multiple paper cubes to get the transitions to work. Another issue was that the
cube layout in the input data was different from the layout in the sample, which
added another round of coding the transitions.  The one useful simplification for
part 2 I came up with was that I converted the coordinates for every side of the
cube to start with [0;0] in the top left corner.
*/
