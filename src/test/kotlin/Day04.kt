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

    private val pattern = Regex("""^(\d+)-(\d+),(\d+)-(\d+)$""")

    private fun one(input: List<String>): Int = input.map {
        pattern.matchEntire(it)!!.groupValues.drop(1).map(String::toInt)
    }.count { (l1, r1, l2, r2) ->
        l1 <= l2 && r1 >= r2 || l2 <= l1 && r2 >= r1
    }

    private fun two(input: List<String>): Int = input.map {
        pattern.matchEntire(it)!!.groupValues.drop(1).map(String::toInt)
    }.count { (l1, r1, l2, r2) ->
        l2 in l1..r1 || l1 in l2..r2
    }
}

/*
Again pretty simple, though my first run failed because I forgot the `drop(1)` to remove the group[0] aka full match
from the matchresult group values.

Another approach would have been the convert the ranges into Set<Int> and then use intersect etc., but that would
be more expensive and not cleaner.
 */
