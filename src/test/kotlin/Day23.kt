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
        one(input) shouldBe 4195
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 20
        two(input) shouldBe 1069
    }

    data class Elf(val x: Int, val y: Int) {
        var px = 0
        var py = 0

        fun reset() {
            px = x
            py = y
        }

        fun neighbors(elves: Set<Elf>): Map<Char, Boolean> {
            val n8 = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
                .map { (dx, dy) -> Elf(x + dx, y + dy) }
                .filter { it in elves }
            return with(n8) {
                if (isEmpty()) emptyMap() else
                    mapOf(
                        'n' to any { it.y == y - 1 },
                        's' to any { it.y == y + 1 },
                        'e' to any { it.x == x + 1 },
                        'w' to any { it.x == x - 1 },
                    )
            }
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

    private fun round(elves: Set<Elf>, di: Int): Set<Elf> {
        val dirs = "nswe".toCharArray()
        elves.forEach { it.reset() }
        for (elf in elves) {
            val n = elf.neighbors(elves)
            if (n.isEmpty()) continue
            val d = dirs.indices.map { dirs[(it + di).mod(dirs.size)] }.find { n[it] == false } ?: continue
            when (d) {
                'n' -> elf.py--
                's' -> elf.py++
                'e' -> elf.px++
                'w' -> elf.px--
            }
        }
        return elves.map { elf ->
            with (elf) {
                if (x == px && y == py || elves.count { it.px == px && it.py == py } != 1) {
                    this
                } else {
                    Elf(px, py)
                }
            }
        }.toSet()
    }

    private fun one(input: List<String>): Int {
        var elves = parse(input)
        repeat(10) { r ->
            elves = round(elves, r)
        }
        return (elves.maxOf { it.x } - elves.minOf { it.x } + 1) * (elves.maxOf { it.y } - elves.minOf { it.y } + 1) - elves.size
    }

    private fun two(input: List<String>): Int {
        var elves = parse(input)
        repeat(Int.MAX_VALUE) { r ->
            val next = round(elves, r)
            if (next == elves) return r + 1
            elves = next
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

Update: Made elves immutable, and simplified `neighbors` to a Map to Boolean instead of a
Set<Elf>. I initially anticipated that we would have to handle these differently in part 2;
classical over-engineering.
*/
