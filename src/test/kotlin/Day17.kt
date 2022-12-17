import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day17 {
    private val sample = """>>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>""".trimIndent().lines()


    sealed class Shape(val name: String, val num: Int) {
        abstract val lines: List<BooleanArray>
        private var topLineIndex = 0
        fun line(rocks: IntRange) = BooleanArray(7).apply { rocks.forEach { set(it, true) } }

        fun left(chamber: ArrayDeque<BooleanArray>) {
            if (lines.withIndex().all { left(it, chamber, true) }) {
                lines.withIndex().forEach { left(it, chamber, false) }
            }
        }

        fun right(chamber: ArrayDeque<BooleanArray>) {
            if (lines.withIndex().all { right(it, chamber, true) }) {
                lines.withIndex().forEach { right(it, chamber, false) }
            }
        }

        fun down(chamber: ArrayDeque<BooleanArray>): Boolean {
            if (lines.withIndex().all { down(it, chamber, true) }) {
                lines.withIndex().forEach { down(it, chamber, false) }
                topLineIndex += 1
                return true
            }
            return false
        }

        fun add(chamber: ArrayDeque<BooleanArray>) {
            lines.withIndex().forEach { add(it, chamber) }
        }

        private fun right(line: IndexedValue<BooleanArray>, chamber: ArrayDeque<BooleanArray>, dry: Boolean): Boolean {
            val cl = chamber[line.index + topLineIndex]
            if (dry) {
                for (i in 0..6) {
                    if (line.value[i] && (i == 6 || cl[i + 1])) return false
                }
                return true
            }
            for (i in 6 downTo 1) line.value[i] = line.value[i - 1]
            line.value[0] = false
            return true
        }

        private fun left(line: IndexedValue<BooleanArray>, chamber: ArrayDeque<BooleanArray>, dry: Boolean): Boolean {
            val cl = chamber[line.index + topLineIndex]
            if (dry) {
                for (i in 0..6) {
                    if (line.value[i] && (i == 0 || cl[i - 1])) return false
                }
                return true
            }
            for (i in 0 until 6) line.value[i] = line.value[i + 1]
            line.value[6] = false
            return true
        }

        private fun down(line: IndexedValue<BooleanArray>, chamber: ArrayDeque<BooleanArray>, dry: Boolean): Boolean {
            val cl = chamber[line.index + topLineIndex + 1]
            if (dry) {
                for (i in 0..6) {
                    if (line.value[i] && cl[i]) return false
                }
                return true
            }
            return true
        }

        private fun add(line: IndexedValue<BooleanArray>, chamber: ArrayDeque<BooleanArray>) {
            val cl = chamber[line.index + topLineIndex]
            for (i in 0..6) {
                if (line.value[i]) cl[i] = true
            }
        }
    }


    private class Minus(num: Int) : Shape("minus", num) {
        override val lines = listOf(
            line(2..5),
        )
    }

    private class Plus(num: Int) : Shape("plus", num) {
        override val lines: List<BooleanArray> = listOf(
            line(3..3),
            line(2..4),
            line(3..3),
        )
    }

    private class InvertedL(num: Int) : Shape("inverted L", num) {
        override val lines: List<BooleanArray> = listOf(
            line(4..4),
            line(4..4),
            line(2..4),
        )
    }

    private class Line(num: Int) : Shape("line", num) {
        override val lines: List<BooleanArray> = listOf(
            line(2..2),
            line(2..2),
            line(2..2),
            line(2..2),
        )
    }

    private class Square(num: Int) : Shape("square", num) {
        override val lines: List<BooleanArray> = listOf(
            line(2..3),
            line(2..3),
        )
    }

    private val shapes = sequence {
        var id = 0
        while (true) {
            yield(Minus(++id))
            yield(Plus(++id))
            yield(InvertedL(++id))
            yield(Line(++id))
            yield(Square(++id))
        }
    }

    private class Chamber(val gas: String) {
        val chamber = ArrayDeque<BooleanArray>()

        init {
            chamber.add(BooleanArray(7) { true })
        }

        val getGas = sequence {
            while (true) {
                yieldAll(gas.asSequence())
            }
        }.iterator()

        fun draw() {
            println("-------")
            for (i in chamber.indices) {
                println(chamber[i].joinToString("") { if (it) "#" else " " })
            }
            println("-------")
        }

        fun add(shape: Shape) {
            val l = shape.lines
            val c = l.size
            addEmptyLines(3 + c)
            while (true) {
                when (getGas.next()) {
                    '<' -> shape.left(chamber)
                    '>' -> shape.right(chamber)
                }
                if (!shape.down(chamber)) {
                    break
                }
            }
            shape.add(chamber)
            while (chamber[0].none { it }) chamber.removeFirst()
        }

        fun addEmptyLines(num: Int) {
            repeat(num) { chamber.addFirst(BooleanArray(7) { false }) }
        }

        fun size(): Int {
            return chamber.size
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 3068
        one(input) shouldBe 3235
    }

    @Test
    fun testTwo(input: List<String>) {
//                two(sample) shouldBe 1514285714288L
        //        two(input) shouldBe 0
    }

    private fun one(input: List<String>): Int {
        val chamber = Chamber(input[0])
        //        chamber.draw()
        shapes.forEach {
            chamber.add(it)
            if (it.num == 2022) return chamber.size() - 1
            //            chamber.draw()
        }
        //        chamber.draw()
        return chamber.size()
    }

    private fun two(input: List<String>): Int {
        return 0
    }
}
