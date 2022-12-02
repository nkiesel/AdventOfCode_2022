import Day02.Shape.Paper
import Day02.Shape.Rock
import Day02.Shape.Scissors
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test


class Day02 {
    private val sample = """
        A Y
        B X
        C Z
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 15
        one(input) shouldBe 13484
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 12
        two(input) shouldBe 13433
    }

    enum class Shape(val score: Int) {
        Rock(1), Paper(2), Scissors(3)
    }

    class Round(private val opponent: Shape, private val you: Shape) {
        fun score(): Int = you.score + when (you) {
            Rock -> when (opponent) {
                Rock -> 3
                Paper -> 0
                Scissors -> 6
            }

            Paper -> when (opponent) {
                Rock -> 6
                Paper -> 3
                Scissors -> 0
            }

            Scissors -> when (opponent) {
                Rock -> 0
                Paper -> 6
                Scissors -> 3
            }
        }

        companion object {
            private fun fromLetter(l: Char) = when (l) {
                'A', 'X' -> Rock
                'B', 'Y' -> Paper
                'C', 'Z' -> Scissors
                else -> throw IllegalArgumentException()
            }

            fun one(line: String) = Round(fromLetter(line[0]), fromLetter(line[2]))

            private fun forOutcome(opponent: Shape, outcome: Char) = when (outcome) {
                'X' -> when (opponent) {
                    Rock -> Scissors
                    Paper -> Rock
                    Scissors -> Paper
                }
                'Y' -> opponent
                'Z' -> when (opponent) {
                    Rock -> Paper
                    Paper -> Scissors
                    Scissors -> Rock
                }
                else -> throw IllegalArgumentException()
            }

            fun two(line: String): Round {
                val opponent = fromLetter(line[0])
                return Round(opponent, forOutcome(opponent, line[2]))
            }
        }
    }

    private fun one(input: List<String>): Int = input.map { Round.one(it) }.sumOf { it.score() }

    private fun two(input: List<String>): Int = input.map { Round.two(it) }.sumOf { it.score() }
}

/*
This required more code than I expected (though I'm sure there will be much shorter and better solutions posted).
I still like the readability of the `when` expressions very much (and that they are expressions and not statements).
I also spent a bit of time to refactor the code after I solved the puzzles to make it a bit more readable (e.g. I
 initially used a Pair(Shape, Shape) and top-level functions instead of the class Round).

The top-level imports for Rock, Paper, and Scissors are a hack to work around a current Kotlin language limitation:
Without these imports, every Rock would have to be written as Shape.Rock (and same for Paper and Scissors).
*/
