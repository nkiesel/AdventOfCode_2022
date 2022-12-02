import Day02.Outcome.Loose
import Day02.Outcome.Win
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
        Rock(1), Paper(2), Scissors(3);

        companion object {
            fun of(c: Char) = when (c) {
                'A', 'X' -> Rock
                'B', 'Y' -> Paper
                'C', 'Z' -> Scissors
                else -> error("Invalid letter $c")
            }
        }
    }

    class Round(private val opponent: Shape, private val you: Shape) {
        fun score() = you.score + when (opponent to you) {
            Rock to Paper -> 6
            Rock to Scissors -> 0
            Paper to Rock -> 0
            Paper to Scissors -> 6
            Scissors to Rock -> 6
            Scissors to Paper -> 0
            else -> 3
        }
    }

    enum class Outcome {
        Loose, Draw, Win;

        companion object {
            fun of(c: Char) = when (c) {
                'X' -> Loose
                'Y' -> Draw
                'Z' -> Win
                else -> error("Invalid letter $c")
            }
        }
    }

    private fun forOutcome(opponent: Shape, outcome: Outcome) = when (opponent to outcome) {
        Rock to Loose -> Scissors
        Rock to Win -> Paper
        Paper to Loose -> Rock
        Paper to Win -> Scissors
        Scissors to Loose -> Paper
        Scissors to Win -> Rock
        else -> opponent
    }

    private fun one(input: List<String>): Int = input.map {
        Round(Shape.of(it[0]), Shape.of(it[2]))
    }.sumOf { it.score() }

    private fun two(input: List<String>): Int = input.map {
        val opponent = Shape.of(it[0])
        Round(opponent, forOutcome(opponent, Outcome.of(it[2])))
    }.sumOf { it.score() }
}

/*
This required more code than I expected (though I'm sure there will be much shorter and better solutions posted).
I still like the readability of the `when` expressions very much (and that they are expressions and not statements).
I also spent a bit of time to refactor the code after I solved the puzzles to make it a bit more readable (e.g. I
 initially used a Pair(Shape, Shape) instead of the class Round).

The top-level imports for Rock, Paper, and Scissors are a hack to work around a current Kotlin language limitation:
Without these imports, every Rock would have to be written as Shape.Rock (and same for Paper and Scissors and Win and Loose).
*/
