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
            .mapIndexed { i, (l, r) -> if (parse(l) compareTo parse(r) < 0) i + 1 else 0 }
            .sum()
    }

    private fun two(input: List<String>): Int {
        val d = listOf(2, 6).map { parse("[[$it]]") }
        val l = (input.filter { it.isNotEmpty() }.map { parse(it) } + d).sorted()
        return (l.indexOf(d[0]) + 1) * (l.indexOf(d[1]) + 1)
    }

    private sealed interface Item : Comparable<Item>

    private data class Single(var v: Int) : Item {
        override fun compareTo(other: Item): Int = when (other) {
            is Single -> v compareTo other.v
            is Multi -> Multi(this) compareTo other
        }
    }

    private data class Multi(val v: MutableList<Item> = mutableListOf()) : Item {
        constructor(v: Item) : this(mutableListOf(v))

        override fun compareTo(other: Item): Int = when (other) {
            is Single -> this compareTo Multi(other)
            is Multi -> v.zip(other.v, Item::compareTo).find { it != 0 } ?: (v.size compareTo other.v.size)
        }
    }

    private fun parse(line: String): Item {
        val stack = ArrayDeque<Multi>()
        var multi = Multi()
        var number: Int? = null
        for (c in line.drop(1)) {
            when (c) {
                '[' -> {
                    stack += multi
                    multi = Multi()
                }

                ']' -> {
                    if (number != null) {
                        multi.v += Single(number)
                        number = null
                    }
                    if (stack.isNotEmpty()) {
                        val top = stack.removeLast()
                        top.v += multi
                        multi = top
                    }
                }

                ',' -> {
                    if (number != null) {
                        multi.v += Single(number)
                        number = null
                    }
                }

                else -> {
                    val i = c.digitToInt()
                    number = if (number != null) number * 10 + i else i
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

Update: realized that I could push the "compareTo" implementation into the "Single" and "Multi" classes, which simplified
the code a bit more.
*/
