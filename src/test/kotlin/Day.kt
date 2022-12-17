import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day {
    private val sample = """""".trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 0
//        one(input) shouldBe 0
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }

    private fun one(input: List<String>): Int {
        return 0
    }

    private fun two(input: List<String>): Int {
        return 0
    }
}
