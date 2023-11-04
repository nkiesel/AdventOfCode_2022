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
        one(input) shouldBe 67027
        two(input, 1) shouldBe 67027
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 45000
        two(input) shouldBe 197291
    }

    private fun parse(input: List<String>): List<List<Int>> = input.chunkedBy { it.isEmpty() }.map { it.map(String::toInt) }

    private fun one(input: List<String>): Int = parse(input).maxOf { it.sum() }

    private fun two(input: List<String>, n: Int = 3): Int = parse(input).map { it.sum() }.sorted().takeLast(n).sum()
}

/*
As usual, AoC starts very simple. I still had to remember that sorted sorts naturally and I thus had to use `takeLast` and not `take`.

Also, as usual we could now replace `one` with `two` by adding a `, n: Int = 1` parameter and use that in `takeLast`
 */
