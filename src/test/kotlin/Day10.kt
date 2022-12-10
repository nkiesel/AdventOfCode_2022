import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day10 {
    private val s0 = """
        noop
        addx 3
        addx -5
    """.trimIndent().lines()

    private val sample = """
        addx 15
        addx -11
        addx 6
        addx -3
        addx 5
        addx -1
        addx -8
        addx 13
        addx 4
        noop
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx -35
        addx 1
        addx 24
        addx -19
        addx 1
        addx 16
        addx -11
        noop
        noop
        addx 21
        addx -15
        noop
        noop
        addx -3
        addx 9
        addx 1
        addx -3
        addx 8
        addx 1
        addx 5
        noop
        noop
        noop
        noop
        noop
        addx -36
        noop
        addx 1
        addx 7
        noop
        noop
        noop
        addx 2
        addx 6
        noop
        noop
        noop
        noop
        noop
        addx 1
        noop
        noop
        addx 7
        addx 1
        noop
        addx -13
        addx 13
        addx 7
        noop
        addx 1
        addx -33
        noop
        noop
        noop
        addx 2
        noop
        noop
        noop
        addx 8
        noop
        addx -1
        addx 2
        addx 1
        noop
        addx 17
        addx -9
        addx 1
        addx 1
        addx -3
        addx 11
        noop
        noop
        addx 1
        noop
        addx 1
        noop
        noop
        addx -13
        addx -19
        addx 1
        addx 3
        addx 26
        addx -30
        addx 12
        addx -1
        addx 3
        addx 1
        noop
        noop
        noop
        addx -9
        addx 18
        addx 1
        addx 2
        noop
        noop
        addx 9
        noop
        noop
        noop
        addx -1
        addx 2
        addx -37
        addx 1
        addx 3
        noop
        addx 15
        addx -21
        addx 22
        addx -6
        addx 1
        noop
        addx 2
        addx 1
        noop
        addx -10
        noop
        noop
        addx 20
        addx 1
        addx 2
        addx 2
        addx -6
        addx -11
        noop
        noop
        noop
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        oneTwo(sample, false) shouldBe 13140
        oneTwo(input, false) shouldBe 13820
    }

    @Test
    fun testTwo(input: List<String>) {
        oneTwo(sample, false) shouldBe 13140
        oneTwo(input, true) shouldBe 13820
    }

    private fun oneTwo(input: List<String>, draw: Boolean): Int {
        var x = 1
        var strength = 0
        var cycle = 1
        var pixelPos = 0
        val crt = buildString {
            input.forEach { line ->
                val add = line != "noop"
                repeat(if (add) 2 else 1) {
                    if (draw) {
                        if (pixelPos % 5 == 0) append("   ")
                        append(if (x in (pixelPos - 1)..(pixelPos + 1)) '█' else ' ')
                        if (++pixelPos == 40) append("\n").also { pixelPos = 0 }
                    }

                    if ((cycle + 20) % 40 == 0) {
                        strength += cycle * x
                    }
                    cycle++
                }

                if (add) {
                    x += line.drop(5).toInt()
                }
            }
        }

        if (draw) println(crt)

        return strength
    }
}
/*
Still pretty simple. I initially misread the "2 cycles for addx" instructions, but the sample
data helped as usual. The only obstacle was that I thought the sample would also render 8 letters,
and I stared at the generated image quite a bit, trying to make sens of it.  I finally gave up and
rendered the image using the real input, and suddenly the letters became visible.  I then increased
the readability a bit more by using nicer pixels and adding some whitespace.
*/
