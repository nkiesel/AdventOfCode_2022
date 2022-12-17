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

        fun mergeInto(chamber: ArrayDeque<BooleanArray>) {
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
            repeat(3 + shape.lines.size) { chamber.addFirst(BooleanArray(7)) }
            do {
                when (getGas.next()) {
                    '<' -> shape.left(chamber)
                    '>' -> shape.right(chamber)
                }
                gasServed++
            } while (shape.down(chamber))
            shape.mergeInto(chamber)
            while (chamber[0].none { it }) chamber.removeFirst()
        }

        fun skipForward(num: Long) {
            skipped += num
        }

        fun size(): Long {
            // -1 because of the bottom line we added
            return skipped + chamber.size - 1
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 3068
        one(input) shouldBe 3235
        two(sample, 2022L) shouldBe 3068
        two(input, 2022L) shouldBe 3235
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 1514285714288L
        two(input) shouldBe 1591860465110L
    }

    private fun one(input: List<String>): Long {
        val chamber = Chamber(input[0])
        shapes.forEach {
            chamber.add(it)
            if (it.num == 2022L) return chamber.size()
        }
        return -1
    }

    private fun two(input: List<String>, wanted: Long = 1_000_000_000_000L): Long {
        val chamber = Chamber(input[0])
        val numShapes = 5L
        val numGas = chamber.gas.length.toLong()
        val seen = mutableMapOf<Pair<Long, String>, Pair<Long, Long>>()
        var remainingShapes = wanted
        var lookingForCycle = true
        shapes.forEach {
            chamber.add(it)
            remainingShapes -= 1
            if (lookingForCycle && it.num % numShapes == 0L && chamber.size() >= 8) {
                // cycle detection. Not sure why, but seems I only have to finger-print the top 8 rows
                val key = chamber.gasServed % numGas to chamber.fingerPrint(8)
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
        }

        return -1L
    }
}

/*
Part one was straight-forward, although I likely coded too many lines.  One question
was how to encode the shapes and chamber.  I went with BooleanArray which worked nicely
in some places, but then had to switch to a different encoding for the fingerprinting
used in the cycle detection for part 2 because arrays in Java and Kotlin do not have
a usable "equals".

One nagging problem is that the cycle detection only looks at the top 8 rows of the
chamber. This "8" fell from the sky (or actually: from the AoC Slack channel), but
I have not yet heard a solid justification for why this works.  My guess is that it
works for the input data provided, but will not work for all inputs.  Someone else
coded the fingerprint by looking at the outline of the chamber.  Seems more robust
but also much more complicated (e.g. a zigzag on the right side would require to add
all indentations to the outline).
 */
