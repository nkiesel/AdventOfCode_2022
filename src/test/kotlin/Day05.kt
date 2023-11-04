import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day05 {
    private val sample = """
            [D]    
        [N] [C]    
        [Z] [M] [P]
         1   2   3 

        move 1 from 2 to 1
        move 3 from 1 to 3
        move 2 from 2 to 1
        move 1 from 1 to 2
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe "CMZ"
        one(input) shouldBe "HNSNMTLHQ"
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe "MCD"
        two(input) shouldBe "RNLFDJMCT"
    }

    class Move(val count: Int, val source: Int, val dest: Int)

    class Parsed {
        val stacks = mutableMapOf<Int, ArrayDeque<Char>>()
        val moves = mutableListOf<Move>()

        fun top() = stacks.entries.sortedBy { it.key }.map { (_, s) -> s.last() }.joinToString("")
    }

    private fun parse(input: List<String>): Parsed {
        val cr = Regex("""\[(.)\]""")
        val mr = Regex("""move (\d+) from (\d+) to (\d+)""")
        val p = Parsed()
        for (line in input) {
            cr.findAll(line).forEach { r -> p.stacks.getOrPut(r.range.first / 4 + 1) { ArrayDeque() }.addFirst(r.groupValues[1][0]) }
            mr.find(line)?.let { r -> p.moves += Move(r.groupValues[1].toInt(), r.groupValues[2].toInt(), r.groupValues[3].toInt()) }
        }
        return p
    }

    private fun one(input: List<String>): String {
        val p = parse(input)
        for (move in p.moves) {
            val s = p.stacks[move.source]!!
            val d = p.stacks[move.dest]!!
            repeat(move.count) { d += s.removeLast() }
        }
        return p.top()
    }

    private fun two(input: List<String>): String {
        val p = parse(input)
        for (move in p.moves) {
            val s = p.stacks[move.source]!!
            val d = p.stacks[move.dest]!!
            val c = move.count
            d += s.takeLast(c)
            repeat(c) { s.removeLast() }
        }
        return p.top()
    }
}

/*
Nice one! I first thought that parsing the input data would be tricky, but after realizing that I could use the `range` to
get the index of a crate in the line, it was pretty straight-forward to parse the crate stacks. For simplicity, I try to parse
each line as a crate line or a move, instead of parsing in stages.

The only mistake I made was to use global variables for the stacks and moves, because I forgot to clear between the tests,
which screwed up the result once I ran more than one test.  I then created the `Parsed` class, and all went well.  Global
variables are like the "goto" of data structures: sometimes very efficient, but generally not recommended!

Part 2 could be done with a single `repeat` like part 1 by using a clever `removeAt`, (something like
`removeAt(move.source.lastIndex - move.count + it)`), but I stayed away form this because it would make the
code less readable.
*/
