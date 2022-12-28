import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import org.junit.jupiter.api.Test

class Day13JSON {
    val sample = """
        [1,1,3,1,1]
        [1,1,5,1,1]

        [[1],[2,3,4]]
        [[1],4]

        [9]
        [[8,7,6]]

        [[4,4],4,4]
        [[4,4],4,4,4]

        [7,7,7,7]
        [7,7,7]

        []
        [3]

        [[[]]]
        [[]]

        [1,[2,[3,[4,[5,6,7]]]],8,9]
        [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent().lines()

    sealed interface Item : Comparable<Item>

    data class Single(val v: Int) : Item {
        constructor(j: JsonPrimitive) : this(j.int)

        override fun compareTo(other: Item): Int {
            return when (other) {
                is Single -> v compareTo other.v
                is Multi -> Multi(this) compareTo other
            }
        }
    }

    data class Multi(val v: List<Item>) : Item {
        constructor(j: JsonArray) : this(j.map { it.toItem() })
        constructor(s: Single) : this(listOf(s))

        override fun compareTo(other: Item): Int {
            return when (other) {
                is Single -> this compareTo Multi(other)
                is Multi -> v.zip(other.v) { l, r -> l compareTo r }.find { it != 0 } ?: (v.size compareTo other.v.size)
            }
        }
    }

    private fun String.toItem(): Item = Json.decodeFromString<JsonArray>(this).toItem()

    @Test
    fun testOne() {
        sample
            .chunked(3)
            .mapIndexed { index, (l, r) -> if (l.toItem() compareTo r.toItem() < 0) index + 1 else 0 }
            .sum() shouldBe 13
    }
}

private fun JsonElement.toItem(): Day13JSON.Item {
    return when (this) {
        is JsonPrimitive -> Day13JSON.Single(this)
        is JsonArray -> Day13JSON.Multi(this)
        else -> error("Unsupported")
    }
}
