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

    private fun pos(input: List<String>, n: Int): Int = input[0].windowed(n).indexOfFirst { it.toSet().size == n } + n

    private fun one(input: List<String>): Int = pos(input, 4)

    private fun two(input: List<String>): Int = pos(input, 14)
}

/*
This was too easy (or Kotlin stdlib is too good)
 */
