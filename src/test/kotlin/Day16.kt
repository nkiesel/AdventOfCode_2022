import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day16 {
    private val sample = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 1651
        one(input) shouldBe 2183
    }

    @Test
    fun testTwo(input: List<String>) {
        //        two(sample) shouldBe 0
        //        two(input) shouldBe 0
    }

    private data class Valve(val name: String, val flowRate: Int, val next: List<String>)

    private fun parse(input: List<String>): List<Valve> {
        val re = Regex("""Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? (.+)""")
        return input.map { line ->
            re.matchEntire(line)!!.groupValues.let { Valve(it[1], it[2].toInt(), it[3].split(", ")) }
        }
    }

    private fun one(input: List<String>): Int {
        val valves = parse(input)
        val start = "AA"
        val tunnels = valves.associateBy { it.name }
        val flowRates = valves.filter { it.flowRate > 0 }.associate { it.name to it.flowRate }
        val targets = flowRates.keys
        val costs = (targets + start).flatMap { t -> (targets - t).map { Pair(t, it) to pathLength(t, it, tunnels) + 1 } }.toMap()

        fun flowOf(
            start: String,
            minute: Int,
            remaining: Set<String>,
            flow: Int
        ): Int {
            return remaining
                .mapNotNull { next ->
                    (next to minute + costs[start to next]!!).takeIf { it.second < 30 }
                }.maxOfOrNull { (start, minute) ->
                    flowOf(start, minute, remaining - start, flow + flowRates[start]!! * (30 - minute))
                } ?: flow
        }

        return flowOf(start, 0, targets, 0)
    }

    private fun pathLength(a: String, b: String, tunnels: Map<String, Valve>): Int {
        return bfs(a) { tunnels[it]!!.next }.find { it.value == b }?.index ?: Int.MAX_VALUE
    }


    private fun two(input: List<String>): Int {
        return 0
    }
}

/*
Oh man, this was tough!  I tried numerous ways until I found a solution for part 1. One of my earlier idea was to
simply compute all permutations of paths (limited to values with positive flow rate). That worked nicely for the
sample input with 6! permutations, but would have taken waay to long for 15! permutations.  I finally realized
that the real input must avoid a huge set of these permutations because the paths are much longer there than in
the sample, and we thus often run out of time.  This finally lead to the recursive approach above.
 */
