import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

/*
 Challenge for day 0: Given a list of numbers, return their sum. As an example,
 the answer for the following list is 8:
   3
   1
   4

 --- Part Two ---
 Instead of adding the numbers, multiply them. For the above example input, the answer is 12.
 */

class Day00 {
    private val sample = """
        3
        1
        4
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 8
        one(input) shouldBe 15
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 12L
        two(input) shouldBe 120L
    }

    // This should return the sum of the input
    private fun one(input: List<String>): Int = input.map(String::toInt).sum()

    // This should return the product of the input
    private fun two(input: List<String>): Long {
        return input.map(String::toLong).reduce { acc, i -> acc * i }
    }
}
