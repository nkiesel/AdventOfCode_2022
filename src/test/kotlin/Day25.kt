import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day25 {
    private val sample = """
        1=-0-2
        12111
        2=0=
        21
        2=01
        111
        20012
        112
        1=-1=
        1-12
        12
        1=
        122
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe "2=-1=0"
        one(input) shouldBe "2-2=21=0021=-02-1=-0"
    }

    private fun Long.toSnafu(): String {
        val res = mutableListOf<Char>()
        var n = this
        var carry = 0
        while (n != 0L) {
            when (n.mod(5) + carry) {
                0 -> res.add('0').also { carry = 0 }
                1 -> res.add('1').also { carry = 0 }
                2 -> res.add('2').also { carry = 0 }
                3 -> res.add('=').also { carry = 1 }
                4 -> res.add('-').also { carry = 1 }
                5 -> res.add('0').also { carry = 1 }
            }
            n /= 5
        }
        if (carry != 0) res.add('1')
        return res.reversed().joinToString("").ifEmpty { "0" }
    }

    private fun String.fromSnafu(): Long {
        return fold(0L) { acc, c ->
            acc * 5 + when (c) {
                '=' -> -2
                '-' -> -1
                else -> c.digitToInt()
            }
        }
    }

    private fun one(input: List<String>): String {
        return input.sumOf { it.fromSnafu() }.toSnafu()
    }
}
/*
Happy holidays!
*/
