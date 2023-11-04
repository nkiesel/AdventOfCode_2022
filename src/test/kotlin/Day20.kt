import kotlin.math.absoluteValue
import kotlin.math.sign
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day20 {
    private val sample = """
        1
        2
        -3
        3
        -2
        0
        4
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        solve(sample) shouldBe 3L
        solve(input) shouldBe 13289L
    }

    @Test
    fun testTwo(input: List<String>) {
        solve(sample, 811589153L, 10) shouldBe 1623178306L
        solve(input, 811589153L, 10) shouldBe 2865721299243L
    }

    private fun solve(input: List<String>, key: Long = 1L, mix: Int = 1): Long {
        val data = input.map { it.toLong() * key }.withIndex().toList().toTypedArray()
        val size = data.size
        fun swap(a: Int, b: Int): Int {
            data[b] = data[a].also { data[a] = data[b] }
            return b
        }
        repeat(mix) {
            for (k in data.indices) {
                var i = data.indexOfFirst { it.index == k }
                val v = data[i].value
                if (v != 0L) {
                    val s = v.sign
                    repeat(v.absoluteValue.mod(size - 1)) {
                        i = swap(i, (i + s).mod(size))
                    }
                }
            }
        }
        val zi = data.indexOfFirst { it.value == 0L }
        return listOf(1000, 2000, 3000).sumOf { data[(zi + it).mod(size)].value }
    }
}
/*
Oh man, I struggled with this one.  Overall looked very easy, but I for the longest time missed
that the wraparound logic has to use mod(size - 1) and not mod(size).  I initially calculated
the final index, but then switched to really moving one step at a time when I could not get the
correct result.  Now that the code is working, I'm pretty sure that computing the target index
should work (and be faster).  But it's working and I have other stuff to do, so calling it a day!
 */
