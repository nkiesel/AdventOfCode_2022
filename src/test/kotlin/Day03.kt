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
        one(input) shouldBe 7737
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 70
        two(input) shouldBe 2697
    }

    private fun one(input: List<String>): Int = input
        .map { line -> line.chunked(line.length / 2) { it.toSet() } }
        .map { (l, r) -> (l intersect r).single() }
        .sumOf { it - if (it.isLowerCase()) ('a' - 1) else ('A' - 27) }

    private fun two(input: List<String>): Int = input
        .chunked(3) { g -> g.map { it.toSet() } }
        .map { g -> g.reduce { acc, l -> acc intersect l }.single() }
        .sumOf { it - if (it.isLowerCase()) ('a' - 1) else ('A' - 27) }
}

/*
Much simpler than day 2 because of the easy way to convert strings to sets of chars and intersect sets.

Initially used `g[0] intersect g[1] intersect g[2]`, but then switched to `reduce` because that allows handling
different group sizes w/o code change of that part.

Also, I first forgot that `chunked` has an optional transformation function and thus initially used `chunked` + `map`.
Final optimization was realizing that `'b' - 'a'` is the same as `'b'.code - 'a'.code` and that I thus could
remove the `.code`.
*/
