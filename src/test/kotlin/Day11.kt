import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day11 {
    private val sample = """
        Monkey 0:
          Starting items: 79, 98
          Operation: new = old * 19
          Test: divisible by 23
            If true: throw to monkey 2
            If false: throw to monkey 3

        Monkey 1:
          Starting items: 54, 65, 75, 74
          Operation: new = old + 6
          Test: divisible by 19
            If true: throw to monkey 2
            If false: throw to monkey 0

        Monkey 2:
          Starting items: 79, 60, 97
          Operation: new = old * old
          Test: divisible by 13
            If true: throw to monkey 1
            If false: throw to monkey 3

        Monkey 3:
          Starting items: 74
          Operation: new = old + 3
          Test: divisible by 17
            If true: throw to monkey 0
            If false: throw to monkey 1
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 10605L
        one(input) shouldBe 112815L
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 2713310158L
        two(input) shouldBe 25738411485L
    }

    private class Operation(val op: String, val arg: Long?) {
        fun eval(n: Long) = when (op) {
            "+" -> n + (arg ?: n)
            "*" -> n * (arg ?: n)
            else -> error("Invalid op $op")
        }
    }

    private class Monkey(
        val items: MutableList<Long>,
        val op: Operation,
        val divisor: Long,
        val trueIndex: Int,
        val falseIndex: Int,
    ) {
        var inspections: Long = 0L

        companion object {
            fun of(lines: List<String>) = Monkey(
                lines[1].substringAfter(" items: ").split(", ").map(String::toLong).toMutableList(),
                lines[2].substringAfter(" old ").split(" ").let { (op, num) -> Operation(op, num.toLongOrNull()) },
                lines[3].substringAfter(" by ").toLong(),
                lines[4].substringAfter(" monkey ").toInt(),
                lines[5].substringAfter(" monkey ").toInt(),
            )
        }
    }

    private fun parse(input: List<String>) = input.chunkedBy { it.isEmpty() }.map { Monkey.of(it) }

    private fun one(input: List<String>) = oneTwo(parse(input), 20) { it / 3L }

    private fun two(input: List<String>): Long {
        val monkeys = parse(input)
        val divProd = monkeys.map { it.divisor }.reduce(Long::times)
        return oneTwo(monkeys, 10000) { it % divProd }
    }

    private fun oneTwo(monkeys: List<Monkey>, rounds: Int, mitigation: (Long) -> Long): Long {
        repeat(rounds) {
            for (monkey in monkeys) {
                for (item in monkey.items) {
                    val new = mitigation(monkey.op.eval(item))
                    val next = if (new % monkey.divisor == 0L) monkey.trueIndex else monkey.falseIndex
                    monkeys[next].items += new
                }
                monkey.inspections += monkey.items.size
                monkey.items.clear()
            }
        }
        return monkeys.map { it.inspections }.sortedDescending().take(2).reduce(Long::times)
    }
}

/*
First Int overflow puzzle! Switching to Long was an obvious choice, but even that was not good enough.
So first thought I could still brute-force it by switching to BigInteger, but that did not terminate
within 30 seconds.  I knew I had to keep the worry levels somehow under control.  The "next monkey" test
always uses "divisible by ...", so thought I might apply modulo the product of all the divisors.  That works
for the part 2, but not part 1! Still no real idea why that happens, but for now I cheated and apply this
limitation logic only for part 2 (using "rounds > 100").

The other cheat is with the operation parsing: I only coded for the actual input.

Update: after seeing some other solution, I realized that my part1 did not work with the "% divProd" because
I could not apply both worry mitigations ("/ 3" and "% divProd"). So changed the code to pass the mitigation
function, and now I'm reasonable happy!
*/
