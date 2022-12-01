import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test


class Day01 {
    private val sample = """
        1000
        2000
        3000

        4000

        5000
        6000

        7000
        8000
        9000

        10000
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 24000
        one(input) shouldBe 70764
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 45000
        two(input) shouldBe 203905
    }

    private fun one(input: List<String>): Int = input.chunkedBy { it.isEmpty() }.maxOf { it.sumOf(String::toInt) }

    private fun two(input: List<String>): Int = input.chunkedBy { it.isEmpty() }.map { it.sumOf(String::toInt) }.sorted().takeLast(3).sum()
}

/*
As usual, AoC starts very simple. I still had to remember that sorted sorts naturally and I thus had to use `takeLast` and not `take`.

Also, as usual we could now replace `one` with `two` by adding a `, n: Int = 1` parameter and use that in `takeLast`
 */
