import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test


class Day03 {
    private val sample = """
        vJrwpWtwJgWrhcsFMMfFFhFp
        jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
        PmmdzqPrVvPwwTWBwg
        wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
        ttgJtRGJQctTZtZT
        CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 157
        one(input) shouldBe 7766
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 70
        two(input) shouldBe 2415
    }

    private fun one(input: List<String>): Int = input
        .map { it.chunked(it.length / 2) }
        .map { (l, r) -> (l.toSet() intersect r.toSet()).first() }
        .sumOf { it.code + if (it in 'a'..'z') 1 - 'a'.code else 27 - 'A'.code }

    private fun two(input: List<String>): Int = input
        .chunked(3) { g -> g.map { it.toSet() } }
        .map { g -> g.reduce { acc, l -> acc intersect l }.first() }
        .sumOf { it.code + if (it in 'a'..'z') 1 - 'a'.code else 27 - 'A'.code }
}

/*
Much simpler than day 2 because of the easy way in Kotlin to convert strings to sets of chars and intersect sets.

Initially used `g[0] intersect g[1] intersect g[2]`, but then switched to `reduce` because that allows handling
different group sizes w/o code change of that part.
*/
