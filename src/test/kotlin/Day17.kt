import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day17 {
    private val sample = """>>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>""".trimIndent().lines()


    sealed class Shape(val name: String, val num: Long) {
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


    private class Minus(num: Long) : Shape("minus", num) {
        override val lines = listOf(
            line(2..5),
        )
    }

    private class Plus(num: Long) : Shape("plus", num) {
        override val lines: List<BooleanArray> = listOf(
            line(3..3),
            line(2..4),
            line(3..3),
        )
    }

    private class InvertedL(num: Long) : Shape("inverted L", num) {
        override val lines: List<BooleanArray> = listOf(
            line(4..4),
            line(4..4),
            line(2..4),
        )
    }

    private class Line(num: Long) : Shape("line", num) {
        override val lines: List<BooleanArray> = listOf(
            line(2..2),
            line(2..2),
            line(2..2),
            line(2..2),
        )
    }

    private class Square(num: Long) : Shape("square", num) {
        override val lines: List<BooleanArray> = listOf(
            line(2..3),
            line(2..3),
        )
    }

    private val shapes = sequence {
        var id = 0L
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
        private var discarded = 0L
        private var skipped = 0L
        var gasServed = 0L

        init {
            chamber.add(BooleanArray(7) { true })
        }

        val getGas = sequence {
            while (true) {
                yieldAll(gas.asSequence())
            }
        }.iterator()

        fun fingerPrint(n: Int) = chamber.take(n).joinToString("") { row -> row.joinToString("") { if (it) "1" else "0" } }

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
                gasServed++
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

        fun skipForward(num: Long) {
            skipped += num
        }

        fun size(): Long {
            return discarded + skipped + chamber.size - 1
        }

        fun shrink() {
            repeat(chamber.size - 1000) {
                discarded++
                chamber.removeLast()
            }
            return
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 3068
        one(input) shouldBe 3235
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 1514285714288L
        two(input) shouldBe 1591860465110L
    }

    private fun one(input: List<String>): Long {
        val chamber = Chamber(input[0])
        //        chamber.draw()
        shapes.forEach {
            chamber.add(it)
            if (it.num == 2022L) return chamber.size()
            //            chamber.draw()
        }
        //        chamber.draw()
        return chamber.size()
    }

    private fun two(input: List<String>): Long {
        val chamber = Chamber(input[0])
        //        chamber.draw()
        val numShapes = 5L
        val numGas = chamber.gas.length.toLong()
        val seen = mutableMapOf<Pair<Long, String>, Pair<Long, Long>>()
        val wanted = 1_000_000_000_000L
        var remainingShapes = wanted
        var lookingForCycle = true
        shapes.forEach {
            chamber.add(it)
            remainingShapes -= 1
            if (lookingForCycle && it.num % numShapes == 0L) {
                // cycle detection. Not sure why, but seems I only have to finger-print the top 8 rows
                val gasOffset = chamber.gasServed % numGas
                val chamberTop = chamber.fingerPrint(8)
                val key = gasOffset to chamberTop
                val previous = seen[key]
                if (previous == null) {
                    seen[key] = it.num to chamber.size()
                } else {
                    // cycle!!!
                    val cycleShapes = it.num - previous.first
                    val cycleHeight = chamber.size() - previous.second
                    val skipShapes = remainingShapes / cycleShapes
                    remainingShapes -= skipShapes * cycleShapes
                    chamber.skipForward(skipShapes * cycleHeight)
                    println("skipped $skipShapes, remaining are $remainingShapes")
                    lookingForCycle = false
                }
            }

            if (remainingShapes == 0L) return chamber.size()
            if (it.num % 10_000_000L == 0L) {
                chamber.shrink()
                println("shapes: ${it.num}, remaining: total: ${chamber.size()}")
            }
            if (it.num == wanted) return chamber.size()
            //            chamber.draw()
        }
        //        chamber.draw()
        return chamber.size()
    }
}
