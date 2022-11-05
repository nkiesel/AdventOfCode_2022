fun <T> List<T>.permutations(): Sequence<List<T>> = sequence {
    val indices = IntArray(size) { it }
    while (true) {
        yield(indices.map { this@permutations[it] })
        val i = (indices.size - 2 downTo 0).firstOrNull { indices[it] < indices[it + 1] } ?: break
        var j = i + 1
        for (k in i + 2 until indices.size) if (indices[k] in indices[i]..indices[j]) j = k
        indices[i] = indices[j].also { indices[j] = indices[i] }
        indices.reverse(i + 1, indices.size)
    }
}

fun <T> Array<Array<T>>.neighbors4(x: Int, y: Int): List<Pair<Int, Int>> =
    listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
        .map { (dx, dy) -> x + dx to y + dy }
        .filter { (cx, cy) -> cx in this[0].indices && cy in this.indices }

fun Array<IntArray>.neighbors4(x: Int, y: Int): List<Pair<Int, Int>> =
    listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
        .map { (dx, dy) -> x + dx to y + dy }
        .filter { (cx, cy) -> cx in this[0].indices && cy in this.indices }

fun <T> Array<Array<T>>.neighbors8(x: Int, y: Int): List<Pair<Int, Int>> =
    listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
        .map { (dx, dy) -> x + dx to y + dy }
        .filter { (cx, cy) -> cx in this[0].indices && cy in this.indices }

fun Array<IntArray>.neighbors8(x: Int, y: Int): List<Pair<Int, Int>> =
    listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
        .map { (dx, dy) -> x + dx to y + dy }
        .filter { (cx, cy) -> cx in this[0].indices && cy in this.indices }

fun <T> List<T>.chunkedBy(selector: (T) -> Boolean): List<List<T>> =
    fold(mutableListOf(mutableListOf<T>())) { acc, item ->
        if (selector(item)) {
            acc.add(mutableListOf())
        } else {
            acc.last().add(item)
        }
        acc
    }

class CountingMap<T>(
    l: List<T> = emptyList(),
    private val m: MutableMap<T, MutableLong> = mutableMapOf()
) : MutableMap<T, CountingMap.MutableLong> by m {
    init {
        l.forEach { inc(it) }
    }

    class MutableLong(var value: Long)

    fun inc(k: T, amount: Long = 1L) {
        m.getOrPut(k) { MutableLong(0L) }.value += amount
    }

    fun count(k: T) = m[k]?.value ?: 0L

    override fun toString(): String {
        return entries.joinToString(", ", prefix = "[", postfix = "]") { (key, count) -> "$key: ${count.value}" }
    }
}
