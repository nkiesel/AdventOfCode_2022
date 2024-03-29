import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day08 {
    private val sample = """
        30373
        25512
        65332
        33549
        35390
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 21
        one(input) shouldBe 1688
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 8
        two(input) shouldBe 410400
    }

    data class Tree(val height: Int, var visible: Boolean = false) {
        fun visible(h: Int) = if (height > h) height.also { visible = true } else h
    }

    private fun parse(input: List<String>) = input
        .map { line -> line.map { Tree(it.digitToInt()) }.toTypedArray() }
        .toTypedArray()

    private fun one(input: List<String>): Int {
        val forest = parse(input)

        fun look(p1: IntProgression, p2: IntProgression, swap: Boolean) {
            for (i in if (swap) p2 else p1) {
                var h = -1
                for (j in if (swap) p1 else p2) {
                    h = forest[if (swap) j else i][if (swap) i else j].visible(h)
                }
            }
        }

        look(forest.indices, forest[0].indices, false)
        look(forest.indices, forest[0].indices.reversed(), false)
        look(forest.indices, forest[0].indices, true)
        look(forest.indices.reversed(), forest[0].indices, true)

        return forest.sumOf { row -> row.count { it.visible } }
    }

    private fun two(input: List<String>): Int {
        val forest = parse(input)

        fun look(sr: Int, sc: Int, dr: Int, dc: Int): Int {
            val height = forest[sr][sc].height
            var r = sr + dr
            var c = sc + dc
            var count = 0
            while (r in forest.indices && c in forest[0].indices) {
                count++
                if (forest[r][c].height >= height) break
                r += dr
                c += dc
            }
            return count
        }

        var maxScore = 0
        for (r in 1 until forest.lastIndex) {
            for (c in 1 until forest[0].lastIndex) {
                maxScore = maxOf(maxScore, look(r, c, 1, 0) * look(r, c, -1, 0) * look(r, c, 0, 1) * look(r, c, 0, -1))
            }
        }
        return maxScore
    }
}

/*
Not proud of the solutions, but too tired now to continue working on better approaches.
*/
