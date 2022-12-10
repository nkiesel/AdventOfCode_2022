import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day10Extra {

    @Test
    fun testOne() {
        multiply("123", "45") shouldBe (123 * 45).toString()
        multiply("123", "1") shouldBe (123).toString()
        multiply("-123", "0") shouldBe (0).toString()
        multiply("123", "-45") shouldBe (123 * -45).toString()
    }

    private fun multiply(num1: String, num2: String): String {
        val (sa, sb) = listOf(num1, num2).map { it.first() == '-' }
        val (ra, rb) = listOf(num1, num2).map { l -> l.dropWhile { it == '-' }.map { it.digitToInt() }.reversed() }
        val rows = ra.map { a -> rb.map { a * it } }
        var carry = 0
        val final = (0..ra.size * rb.size)
            .map { i -> rows.mapIndexed { index, row -> if (i - index in row.indices) row[i - index] else 0 }.sum() }
            .map { val s = it + carry; carry = s / 10; s % 10 }
            .dropLastWhile { it == 0 }
            .reversed()
        return if (final.isEmpty()) "0" else final.joinToString("", prefix = if (sa == sb) "" else "-")
    }

}
