import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day10Extra {

    @Test
    fun testOne() {
        multiply("123", "45") shouldBe (123 * 45).toString()
        multiply("123", "1") shouldBe (123).toString()
        multiply("123", "0") shouldBe (0).toString()
        val l = 999999999L
        multiply(l.toString(), l.toString()) shouldBe (l * l).toString()
    }

    private fun multiply(num1: String, num2: String): String {
        // handle some corner cases
        if (num1 == "0" || num2 == "0") return "0"
        if (num1 == "1") return num2
        if (num2 == "1") return num1
        // convert both strings to reversed lists of their digits (e.g. "123" -> listOf(3, 2, 1))
        val (n1, n2) = listOf(num1, num2).map { num -> num.map { it.digitToInt() }.reversed() }
        // create a list of lists of all numbers of n2 multiplied with the numbers of n1
        val rows = n1.map { a -> n2.map { a * it } }
        var carry = 0
        return (0..n1.size + n2.size + 1)
            // add up the numbers to compute the sum.  We have to shift the rows by their index
            .map { i -> rows.mapIndexed { index, row -> if (i - index in row.indices) row[i - index] else 0 }.sum() }
            // reduce every number in the result to a single digit
            .map { val s = it + carry; carry = s / 10; s % 10 }
            // reverse to that the digits are back in the correct order again
            .reversed()
            // remove leading 0s
            .dropWhile { it == 0 }
            // join all digits to a string
            .joinToString("")
    }

}
