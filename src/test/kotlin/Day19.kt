import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day19 {

    private val sample = """
        Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
        Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        part1(sample) shouldBe 33
        part1(input) shouldBe 1958
    }

    @Test
    fun testTwo(input: List<String>) {
        part2(sample) shouldBe 56 * 62
        part2(input) shouldBe 4257
    }

    data class Blueprint(
        val id: Int,
        val oreRobotOreCost: Int,
        val clayRobotOreCost: Int,
        val obsidianRobotOreCost: Int,
        val obsidianRobotClayCost: Int,
        val geodeRobotOreCost: Int,
        val geodeRobotObsidianCost: Int
    ) {
        val maxOreCost = maxOf(oreRobotOreCost, obsidianRobotOreCost, geodeRobotOreCost, clayRobotOreCost)
    }

    data class State(
        val blueprint: Blueprint,
        val stepsLeft: Int,
        val ore: Int = 0,
        val clay: Int = 0,
        val obsidian: Int = 0,
        val geode: Int = 0,
        val oreRobots: Int = 0,
        val clayRobots: Int = 0,
        val obsidianRobots: Int = 0,
        val geodeRobots: Int = 0,
        val addedRobot: Boolean = false,
    ) {
        fun canBuildOreRobot() = ore >= blueprint.oreRobotOreCost

        fun canBuildClayRobot() = ore >= blueprint.clayRobotOreCost

        fun canBuildObsidianRobot() = ore >= blueprint.obsidianRobotOreCost && clay >= blueprint.obsidianRobotClayCost

        fun canBuildGeodeRobot() = ore >= blueprint.geodeRobotOreCost && obsidian >= blueprint.geodeRobotObsidianCost

        fun buildGeodeRobot() = copy(
            ore = ore - blueprint.geodeRobotOreCost,
            obsidian = obsidian - blueprint.geodeRobotObsidianCost,
            geodeRobots = geodeRobots + 1,
            addedRobot = true,
        )

        fun buildOreRobot() = copy(
            ore = ore - blueprint.oreRobotOreCost,
            oreRobots = oreRobots + 1,
            addedRobot = true,
        )

        fun buildClayRobot() = copy(
            ore = ore - blueprint.clayRobotOreCost,
            clayRobots = clayRobots + 1,
            addedRobot = true,
        )

        fun buildObsidianRobot() = copy(
            ore = ore - blueprint.obsidianRobotOreCost,
            clay = clay - blueprint.obsidianRobotClayCost,
            obsidianRobots = obsidianRobots + 1,
            addedRobot = true,
        )

        fun nextStep(state: State) = copy(
            stepsLeft = stepsLeft - 1,
            ore = ore + state.oreRobots,
            clay = clay + state.clayRobots,
            obsidian = obsidian + state.obsidianRobots,
            geode = geode + state.geodeRobots,
        )
    }

    private val regex = Regex("""\d+""")
    private fun allInt(line: String) = regex.findAll(line).toList().map { it.value.toInt() }

    private fun solve(input: List<String>, steps: Int) = input.map { allInt(it) }
        .map { Blueprint(it[0], it[1], it[2], it[3], it[4], it[5], it[6]) }
        .map { solve(null, State(it, steps, oreRobots = 1)) }

    private fun solve(prevState: State?, state: State): Int {
        if (state.stepsLeft == 0) {
            return state.geode
        }
        return buildList {
            if (state.canBuildGeodeRobot()) {
                add(state.buildGeodeRobot())
            } else {
                add(state.copy(addedRobot = false))
                if (
                    // no point building another robot if we already produce enough ore to build any robot we want
                    state.oreRobots < state.blueprint.maxOreCost &&
                    // make sure we have enough ore
                    state.canBuildOreRobot() &&
                    // no point building another robot if we could have built it in the previous step but did not
                    (state.addedRobot || prevState?.canBuildOreRobot() != true)
                ) {
                    add(state.buildOreRobot())
                }
                if (state.clayRobots < state.blueprint.obsidianRobotClayCost &&
                    state.canBuildClayRobot() &&
                    (state.addedRobot || prevState?.canBuildClayRobot() != true)
                ) {
                    add(state.buildClayRobot())
                }
                if (state.obsidianRobots < state.blueprint.geodeRobotObsidianCost &&
                    state.canBuildObsidianRobot() &&
                    (state.addedRobot || prevState?.canBuildObsidianRobot() != true)
                ) {
                    add(state.buildObsidianRobot())
                }
            }
        }
            .map { it.nextStep(state) }
            .maxOf { solve(state, it) }
    }

    private fun part1(input: List<String>) = solve(input, 24).withIndex().sumOf { (it.index + 1) * it.value }
    private fun part2(input: List<String>) = solve(input.take(3), 32).reduce(Int::times)
}

/*
My only cheating for this year.  I had a more complicated approach, but could not make that work for part 2.
I finally gave up and looked at hints in Slack, where I found the "avoid adding a state with an added robot
if the previous step could have done that already but decided not to add any robots", which reduces the search
state enough to finish within a reasonable time (5 seconds for me).
*/
