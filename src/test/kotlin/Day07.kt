import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day07 {
    private val sample = """
        ${'$'} cd /
        ${'$'} ls
        dir a
        14848514 b.txt
        8504156 c.dat
        dir d
        ${'$'} cd a
        ${'$'} ls
        dir e
        29116 f
        2557 g
        62596 h.lst
        ${'$'} cd e
        ${'$'} ls
        584 i
        ${'$'} cd ..
        ${'$'} cd ..
        ${'$'} cd d
        ${'$'} ls
        4060174 j
        8033020 d.log
        5626152 d.ext
        7214296 k
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 95437L
        one(input) shouldBe 1444896L
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 24933642L
        two(input) shouldBe 404395L
    }

    data class File(val path: List<String>, val name: String, val size: Long)

    private fun parse(input: List<String>) = buildList {
        var path = listOf<String>()
        for (line in input) {
            val l = line.split(' ')
            when (l[0]) {
                "$" -> {
                    if (l[1] == "cd") {
                        path = when (l[2]) {
                            "/" -> listOf("")
                            ".." -> path.dropLast(1)
                            else -> path + l[2]
                        }
                    }
                }
                "dir" -> {}
                else -> add(File(path, l[1], l[0].toLong()))
            }
        }
    }

    private fun dirs(files: List<File>): CountingMap<String> {
        val sizes = CountingMap<String>()
        for (f in files) {
            for (i in 1..f.path.size) {
                sizes.inc(f.path.take(i).joinToString("/"), f.size)
            }
        }
        return sizes
    }

    private fun one(input: List<String>): Long {
        val dirs = dirs(parse(input))
        return dirs.entries().filter { it.value <= 100000L }.sumOf { it.value }
    }

    private fun two(input: List<String>): Long {
        val dirs = dirs(parse(input))
        val required = dirs.count("") - 40000000L
        return dirs.entries().filter { it.value >= required }.minOf { it.value }
    }
}

/*
This was nice!  I initially failed because I did not realize that the same dir name could
be used in different parents. I eventually ran
$ grep '^dir ' input/Day07 | wc -l
$ grep '^dir ' input/Day07 | sort -u |  wc -l
which pointed me to the correct solution (using dir path names instead of dir names for counting).

The solution is clearly not the most efficient (e.g. creating a new `path` list for every "cd"),
but it runs in 40ms, so no reason to optimize.
 */
