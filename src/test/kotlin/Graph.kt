// from https://github.com/alexhwoods/alexhwoods.com/blob/master/kotlin-algorithms/src/main/kotlin/com/alexhwoods/graphs/datastructures/Graph.kt

fun <T> List<Pair<T, T>>.getUniqueValuesFromPairs(predicate: (T) -> Boolean): Set<T> = this
    .flatMap { (a, b) -> listOf(a, b) }
    .filter(predicate)
    .toSet()

data class Graph<T>(
    val vertices: Set<T>,
    val edges: Map<T, Set<T>>,
    val weights: Map<Pair<T, T>, Int>
) {
    constructor(weights: Map<Pair<T, T>, Int>) : this(
        vertices = weights.keys.toList().getUniqueValuesFromPairs { true },
        edges = weights.keys
            .groupBy { it.first }
            .mapValues { it.value.getUniqueValuesFromPairs { x -> x != it.key } }
            .withDefault { emptySet() },
        weights = weights
    )
}

fun <T> dijkstra(graph: Graph<T>, start: T): Map<T, T?> {
    val s = mutableSetOf<T>() // a subset of vertices, for which we know the true distance

    val delta = graph.vertices.associateWith { Int.MAX_VALUE }.toMutableMap()
    delta[start] = 0

    val previous: MutableMap<T, T?> = graph.vertices.associateWith { null }.toMutableMap()

    while (s != graph.vertices) {
        val v: T = delta
            .filter { !s.contains(it.key) }
            .minBy { it.value }
            .key

        graph.edges.getValue(v).minus(s).forEach { neighbor ->
            val newPath = delta.getValue(v) + graph.weights.getValue(Pair(v, neighbor))

            if (newPath < delta.getValue(neighbor)) {
                delta[neighbor] = newPath
                previous[neighbor] = v
            }
        }

        s.add(v)
    }

    return previous
}

fun <T> shortestPath(shortestPathTree: Map<T, T?>, start: T, end: T): List<T> {
    fun pathTo(start: T, end: T): List<T> {
        val t = shortestPathTree[end] ?: return listOf(end)
        return listOf(pathTo(start, t), listOf(end)).flatten()
    }

    return pathTo(start, end)
}

// copied from https://github.com/ephemient/aoc2022/blob/main/kt/src/commonMain/kotlin/com/github/ephemient/aoc2022/Day12.kt
fun <T> bfs(start: T, next: (T) -> Iterable<T>): Sequence<IndexedValue<T>> = sequence {
    val seen = mutableSetOf(start)
    val queue = ArrayDeque(listOf(IndexedValue(0, start)))
    while (queue.isNotEmpty()) {
        val a = queue.removeFirst()
        yield(a)
        for (b in next(a.value)) {
            if (seen.add(b)) {
                queue.add(IndexedValue(a.index + 1, b))
            }
        }
    }
}

fun <T> dfs(start: T, next: (T) -> Iterable<T>): Sequence<IndexedValue<T>> = sequence {
    val seen = mutableSetOf(start)
    val queue = ArrayDeque(listOf(IndexedValue(0, start)))
    while (queue.isNotEmpty()) {
        val a = queue.removeFirst()
        yield(a)
        for (b in next(a.value)) {
            if (seen.add(b)) {
                queue.addFirst(IndexedValue(a.index + 1, b))
            }
        }
    }
}
