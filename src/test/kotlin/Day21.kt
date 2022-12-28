import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day21 {
    private val sample = """
        root: pppw + sjmn
        dbpl: 5
        cczh: sllz + lgvd
        zczc: 2
        ptdq: humn - dvpt
        dvpt: 3
        lfqf: 4
        humn: 5
        ljgn: 2
        sjmn: drzm * dbpl
        sllz: 4
        pppw: cczh / lfqf
        lgvd: ljgn * ptdq
        drzm: hmdt - zczc
        hmdt: 32
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 152L
        one(input) shouldBe 364367103397416L
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 301L
        two(input) shouldBe 3782852515583L
    }

    private fun one(input: List<String>): Long {
        return two(input, true)
    }

    sealed class Node(val name: String) {
        abstract fun eval(): Long
    }

    class Numeric(name: String, private var value: Long) : Node(name) {
        override fun eval(): Long = value
    }

    class Expression(name: String, val left: Node, val op: Char, val right: Node) : Node(name) {
        override fun eval(): Long {
            val l = left.eval()
            val r = right.eval()
            return when (op) {
                '+' -> l + r
                '-' -> l - r
                '*' -> l * r
                '/' -> l / r
                else -> error("Invalid operator")
            }
        }

        fun leftOrRight(name: String): Node {
            val inLeft = dfs(left) {
                when (it) {
                    is Expression -> listOf(it.left, it.right)
                    is Numeric -> emptyList()
                }
            }.find { it.value.name == name }
            return if (inLeft == null) right else left
        }
    }

    data class Line(val name: String, val n: Int?, val left: String, val op: Char, val right: String)

    private fun two(input: List<String>, part1: Boolean = false): Long {
        val re = Regex("""(\w+): (?:(\d+)|(\w+) (.) (\w+))""")
        val lines = input.map { line ->
            val g = re.matchEntire(line)!!.groupValues.drop(1)
            Line(g[0], g[1].toIntOrNull(), g[2], g[3].firstOrNull() ?: '.', g[4])
        }

        val nodes = mutableMapOf<String, Node>()
        val (numeric, expression) = lines.partition { it.n != null }
        numeric.forEach { nodes[it.name] = Numeric(it.name, it.n!!.toLong()) }
        val remaining = expression.map { it.name }.toMutableSet()
        while (remaining.isNotEmpty()) {
            for (l in lines.filter { it.name in remaining }) {
                val left = nodes[l.left]
                val right = nodes[l.right]
                if (left != null && right != null) {
                    nodes[l.name] = Expression(l.name, left, l.op, right)
                }
            }
            remaining -= nodes.keys
        }

        val root = nodes["root"]!! as Expression
        if (part1) return root.eval()

        val me = "humn"
        var n = root.leftOrRight(me) as Expression
        var required = if (n == root.right) root.left.eval() else root.right.eval()
        while (true) {
            val mt = n.leftOrRight(me)
            if (mt == n.right) {
                val l = n.left.eval()
                required = when (n.op) {
                    '+' -> required - l
                    '-' -> l - required
                    '*' -> required / l
                    '/' -> l / required
                    else -> error("Invalid operator")
                }
            } else {
                val r = n.right.eval()
                required = when (n.op) {
                    '+' -> required - r
                    '-' -> required + r
                    '*' -> required / r
                    '/' -> required * r
                    else -> error("Invalid operator")
                }
            }
            if (mt.name == me) return required
            n = mt as Expression
        }
    }
}

/*
This was pretty simple, compared to the last few days.  I initially for part 1 never built
an explicit tree and instead just looped over the lines and computed the value if possible.
Then after part 2 required an explicit tree, I recoded part 1 as simply calling part 2 with
a flag.
 */
