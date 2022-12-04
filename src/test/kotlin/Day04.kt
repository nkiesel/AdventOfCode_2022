import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day04 {
    private val sample = """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 2
        one(input) shouldBe 567
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 4
        two(input) shouldBe 907
    }

    private val pattern = Regex("""\D""")

    private fun one(input: List<String>): Int = input
        .map { it.split(pattern).map(String::toInt) }
        .count { (l1, r1, l2, r2) -> l1 <= l2 && r1 >= r2 || l2 <= l1 && r2 >= r1 }

    private fun two(input: List<String>): Int = input
        .map { it.split(pattern).map(String::toInt) }
        .count { (l1, r1, l2, r2) -> l2 in l1..r1 || l1 in l2..r2 }
}

/*
Again pretty simple. I solved it first by matching the lines against a pattern, but then switched to the `split`
approach because that is a bit simpler, and we know that the input is always well-formed.

Another approach for counting would have been the convert the ranges into Set<Int> and then use intersect etc.,
but that would be more expensive and not cleaner.
 */
