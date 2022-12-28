import java.lang.instrument.ClassFileTransformer
import java.util.Collections
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

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

fun Array<CharArray>.neighbors4(xy: Pair<Int, Int>) = neighbors4(xy.first, xy.second)

fun Array<CharArray>.neighbors4(x: Int, y: Int): List<Pair<Int, Int>> =
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

fun <T> List<T>.chunkedBy(predicate: (T) -> Boolean): List<List<T>> =
    fold(mutableListOf(mutableListOf<T>())) { acc, item ->
        if (predicate(item)) {
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

    fun entries() = m.mapValues { it.value.value }.entries

    override fun toString(): String {
        return entries.joinToString(", ", prefix = "[", postfix = "]") { (key, count) -> "$key: ${count.value}" }
    }
}

/**
 * greatest common divisor of 2 Int values
 */
tailrec fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

/**
 * greatest common divisor of 2 Long values
 */
tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

/**
 * least common multiple of 2 Int values
 */
fun lcm(a: Int, b: Int): Int = a / gcd(a, b) * b

/**
 * least common multiple of 2 Long values
 */
fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

fun manhattanDistance(x1: Int, y1: Int, x2: Int, y2: Int) = (x1 - x2).absoluteValue + (y1 - y2).absoluteValue

fun manhattanDistance(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) = (x1 - x2).absoluteValue + (y1 - y2).absoluteValue + (z1 - z2).absoluteValue

fun manhattanDistance(p1: IntArray, p2: IntArray) = p1.zip(p2).sumOf { (it.first - it.second).absoluteValue }

fun manhattanDistance(x1: Long, y1: Long, x2: Long, y2: Long) = (x1 - x2).absoluteValue + (y1 - y2).absoluteValue

fun manhattanDistance(x1: Long, y1: Long, z1: Long, x2: Long, y2: Long, z2: Long) = (x1 - x2).absoluteValue + (y1 - y2).absoluteValue + (z1 - z2).absoluteValue

fun manhattanDistance(p1: LongArray, p2: LongArray) = p1.zip(p2).sumOf { (it.first - it.second).absoluteValue }

fun <T> Collection<T>.powerSet(): Set<Set<T>> = powerSet(this, setOf(emptySet()))

private tailrec fun <T> powerSet(left: Collection<T>, acc: Set<Set<T>>): Set<Set<T>> {
    return if (left.isEmpty()) {
        acc
    } else {
        powerSet(left.drop(1), acc + acc.map { it + left.first() })
    }
}

fun Collection<Int>.minMax(): IntArray {
    return fold(intArrayOf(Int.MAX_VALUE, Int.MIN_VALUE)) { acc, i -> acc[0] = min(acc[0], i); acc[1] = max(acc[1], i); acc }
}

fun Collection<Long>.minMax(): LongArray {
    return fold(longArrayOf(Long.MAX_VALUE, Long.MIN_VALUE)) { acc, i -> acc[0] = min(acc[0], i); acc[1] = max(acc[1], i); acc }
}

fun Collection<Double>.minMax(): DoubleArray {
    return fold(doubleArrayOf(Double.MAX_VALUE, Double.MIN_VALUE)) { acc, i -> acc[0] = min(acc[0], i); acc[1] = max(acc[1], i); acc }
}

fun Collection<Float>.minMax(): FloatArray {
    return fold(floatArrayOf(Float.MAX_VALUE, Float.MIN_VALUE)) { acc, i -> acc[0] = min(acc[0], i); acc[1] = max(acc[1], i); acc }
}

fun Collection<Int>.multiFold(start: List<Int>, transformers: List<(Int, Int) -> Int>): List<Int> {
    require(start.size == transformers.size)
    return fold(start) { acc, i -> acc.mapIndexed { index, a -> transformers[index](i, a) } }
}

fun Collection<Int>.multiReduce(vararg transformers: (Int, Int) -> Int): List<Int> {
    require(transformers.isNotEmpty()) { "transformers must not be empty"}
    if (isEmpty()) return emptyList()
    val start = first().let { f -> List(transformers.size) { f } }
    return drop(1).fold(start) { acc, i -> acc.mapIndexed { index, a -> transformers[index](i, a) } }
}
