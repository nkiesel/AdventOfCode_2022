import kotlin.math.absoluteValue
import kotlin.math.sign
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day09 {
    private val sample = """
        R 4
        U 4
        L 3
        D 1
        R 4
        D 1
        L 5
        R 2
    """.trimIndent().lines()

    private val sample2 = """
        R 5
        U 8
        L 8
        D 3
        R 17
        D 10
        L 25
        U 20
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 13
        one(input) shouldBe 6087
        onetwo(input, 2) shouldBe 6087
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 1
        two(sample2) shouldBe 36
        two(input) shouldBe 2493
        onetwo(input, 10) shouldBe 2493
    }

    data class Pos(var x: Int, var y: Int) {
        fun move(direction: String) {
            when (direction) {
                "R" -> x += 1
                "L" -> x -= 1
                "U" -> y += 1
                "D" -> y -= 1
                else -> error("Invalid direction $direction")
            }
        }

        fun follow(head: Pos) {
            val dx = head.x - x
            val dy = head.y - y
            if (dx.absoluteValue == 2 || dy.absoluteValue == 2) {
                x += dx.sign
                y += dy.sign
            }
        }
    }

    private fun one(input: List<String>): Int {
        val head = Pos(0, 0)
        val tail = Pos(0, 0)
        val visited = mutableSetOf(tail.copy())

        for (line in input) {
            val cmd = line.split(" ")
            repeat(cmd[1].toInt()) {
                head.move(cmd[0])
                tail.follow(head)
                visited.add(tail.copy())
            }
        }
        return visited.size
    }

    private fun two(input: List<String>): Int {
        val rope = List(10) { Pos(0, 0) }
        val visited = mutableSetOf(rope.last().copy())

        for (line in input) {
            val cmd = line.split(" ")
            repeat(cmd[1].toInt()) {
                rope.first().move(cmd[0])
                rope.windowed(2).forEach { (h, t) -> t.follow(h) }
                visited.add(rope.last().copy())
            }
        }
        return visited.size
    }

    private fun onetwo(input: List<String>, length: Int): Int {
        val rope = List(length) { Pos(0, 0) }
        val visited = mutableSetOf(rope.last().copy())

        for (line in input) {
            val cmd = line.split(" ")
            repeat(cmd[1].toInt()) {
                rope.first().move(cmd[0])
                rope.windowed(2).forEach { (h, t) -> t.follow(h) }
                visited.add(rope.last().copy())
            }
        }
        return visited.size
    }
}

/*
I got both right on the first attempt!  I was initially worried about the negative positions
in part 2, but all worked out fine. As often, part 2 was an extension of part 1, so function
onetwo which is a version of function two with customizable rope length will solve both parts.

Having mutable data classes is often frowned upon, but they did a wonderful job here. We just
have to remember to call copy() before inserting the tail into the visited set.
 */
