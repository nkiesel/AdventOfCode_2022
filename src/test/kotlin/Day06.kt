import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day06 {
    private val sample = """bvwbjplbgvbhsrlpgdmjqwftvncz""".trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 5
        one(input) shouldBe 1582
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 23
        two(input) shouldBe 3588
    }

    private fun solve(input: List<String>, n: Int): Int = input[0].windowedSequence(n).indexOfFirst { it.toSet().size == n } + n

    private fun one(input: List<String>): Int = solve(input, 4)

    private fun two(input: List<String>): Int = solve(input, 14)
}

/*
This was too easy (or Kotlin stdlib is too good). Only improvements after earning the 2 stars was to use a single
implementation function, and using `windowedSequence` instead of `windowed` as a tiny optimization.
 */
