import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day23 {
    private val sample = """
        ....#..
        ..###.#
        #...#.#
        .#...##
        #.###..
        ##.#.##
        .#..#..
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 110
        one(input) shouldBe 4075
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 20
        two(input) shouldBe 950
    }

    data class Elf(var x: Int, var y: Int) {
        var px = x
        var py = y

        fun neighbors(elves: Set<Elf>): Map<Char, Set<Elf>> {
            val n8 = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
                .map { (dx, dy) -> Elf(x + dx, y + dy) }
                .filter { it in elves }
            if (n8.isEmpty()) return emptyMap()
            return mapOf(
                'n' to n8.filter { it.y == y - 1 }.toSet(),
                's' to n8.filter { it.y == y + 1 }.toSet(),
                'e' to n8.filter { it.x == x + 1 }.toSet(),
                'w' to n8.filter { it.x == x - 1 }.toSet(),
            )
        }
    }

    private fun parse(input: List<String>): Set<Elf> {
        return buildSet {
            input.forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    if (c == '#') add(Elf(x, y))
                }
            }
        }
    }

    private fun round(elves: Set<Elf>, di: Int): Boolean {
        val dirs = "nswe".toCharArray()
        elves.forEach { it.px = it.x; it.py = it.y }
        for (elf in elves) {
            val n8 = elf.neighbors(elves)
            if (n8.isEmpty()) continue
            val d = dirs.indices.map { dirs[(it + di).mod(dirs.size)] }.find { n8[it]!!.isEmpty() } ?: continue
            when (d) {
                'n' -> elf.py--
                's' -> elf.py++
                'e' -> elf.px++
                'w' -> elf.px--
                else -> error("Invalid direction")
            }
        }
        var stable = true
        for (elf in elves) {
            if ((elf.x != elf.px || elf.y != elf.py) && elves.count { it.px == elf.px && it.py == elf.py } == 1) {
                elf.x = elf.px
                elf.y = elf.py
                stable = false
            }
        }
        return stable
    }

    private fun one(input: List<String>): Int {
        var elves = parse(input).toSet()
        for (i in 0..9) {
            round(elves, i)
            // recompute the set based on updated elves
            elves = elves.toSet()
        }
        val minX = elves.minOf { it.x }
        val maxX = elves.maxOf { it.x }
        val minY = elves.minOf { it.y }
        val maxY = elves.maxOf { it.y }
        return (maxX - minX + 1) * (maxY - minY + 1) - elves.size
    }

    private fun two(input: List<String>): Int {
        var elves = parse(input).toSet()
        for (i in 0..Int.MAX_VALUE) {
            if (round(elves, i)) return i + 1
            // recompute the set based on updated elves
            elves = elves.toSet()
        }
        return -1
    }

}
/*
Again relatively simple, and again I shot myself in the foot multiple times.  First set of
issues was that I changed px instead of py for n/s.  That was relatively simple to find by
comparing the sample maps.  The bigger issue was that I use a Set<Elf>, but then update the
elves after they were inserted into the set.  This then produces wrong results for "in" tests
because these are based on the hashCode of the original x/y.  I added a stupid fix for this,
but this is clearly a lesson learned!
 */
