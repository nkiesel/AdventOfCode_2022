import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day13 {
    private val sample = """
        [1,1,3,1,1]
        [1,1,5,1,1]

        [[1],[2,3,4]]
        [[1],4]

        [9]
        [[8,7,6]]

        [[4,4],4,4]
        [[4,4],4,4,4]

        [7,7,7,7]
        [7,7,7]

        []
        [3]

        [[[]]]
        [[]]

        [1,[2,[3,[4,[5,6,7]]]],8,9]
        [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 13
        one(input) shouldBe 5366
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 140
        two(input) shouldBe 23391
    }

    private fun one(input: List<String>): Int {
        return input
            .chunkedBy { it.isEmpty() }
            .mapIndexed { i, l -> if (parse(l[0]) compareTo parse(l[1]) == -1) i + 1 else 0 }
            .sum()
    }

    private fun two(input: List<String>): Int {
        val v2 = parse("[[2]]")
        val v6 = parse("[[6]]")
        val l = (input.filter { it.isNotEmpty() }.map { parse(it) } + listOf(v2, v6)).sorted()
        return (l.indexOf(v2) + 1) * (l.indexOf(v6) + 1)
    }

    private sealed interface Item : Comparable<Item> {
        override fun compareTo(other: Item): Int = when {
            this is Single && other is Single -> this.v compareTo other.v
            this is Multi && other is Multi -> this.v.zip(other.v).fold(0) { acc, (l, r) -> if (acc == 0) l compareTo r else acc }.takeIf { it != 0 } ?: this.v.size compareTo other.v.size
            this is Single -> Multi(mutableListOf(this)) compareTo other
            else -> this compareTo Multi(mutableListOf(other))
        }
    }

    private data class Single(var v: Int) : Item

    private data class Multi(val v: MutableList<Item>) : Item

    private fun parse(line: String): Item {
        val stack = ArrayDeque<Multi>()
        var multi = Multi(mutableListOf())
        var single: Single? = null
        for (c in line.drop(1)) {
            when (c) {
                '[' -> {
                    stack.addLast(multi)
                    multi = Multi(mutableListOf())
                }

                ']' -> {
                    if (single != null) {
                        multi.v += single
                        single = null
                    }
                    if (stack.isNotEmpty()) {
                        stack.last().v += multi
                        multi = stack.removeLast()
                    }
                }

                ',' -> {
                    if (single != null) {
                        multi.v += single
                        single = null
                    }
                }

                else -> {
                    val i = c.digitToInt()
                    if (single != null) {
                        single.v = single.v * 10 + i
                    } else {
                        single = Single(i)
                    }
                }
            }
        }
        return multi
    }
}

/*
Again got the code correct in the first attempt! Actually, not 100% true: I had some bugs in the initial "parse" implementation.
However, after these were fixed, the rest fell into place. The only change for part 2 was that I initially used a "rightOrder"
function and that "Item" did not implement "Comparable<Item>".  I then changed this and used "compareTo" for part 1 as well
(which I in hindsight should have done anyway).
 */
