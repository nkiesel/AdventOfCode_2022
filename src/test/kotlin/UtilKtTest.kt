import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class UtilKtTest {
    @Test
    fun permutations() {
        listOf("a", "b", "c").permutations().toList() shouldHaveSize 6
    }

    @Test
    fun neighbors4() {
        val ia = Array(5) { IntArray(5) }

        ia.neighbors4(0, 0) shouldHaveSize 2
        ia.neighbors4(0, 1) shouldHaveSize 3
        ia.neighbors4(0, 4) shouldHaveSize 2
        ia.neighbors4(1, 1) shouldHaveSize 4
    }

    @Test
    fun neighbors8() {
        val ia = Array(5) { IntArray(5) }

        ia.neighbors8(0, 0) shouldHaveSize 3
        ia.neighbors8(0, 1) shouldHaveSize 5
        ia.neighbors8(0, 4) shouldHaveSize 3
        ia.neighbors8(1, 1) shouldHaveSize 8
    }

    @Test
    fun chunkedBy() {
        listOf(3, 1, 4, 1, 5, 9).chunkedBy { it % 2 == 0 } shouldBe listOf(listOf(3, 1), listOf(1, 5, 9))
    }

    @Test
    fun countingMap() {
        val map = CountingMap<String>()
        map.inc("a")
        map.inc("b")
        map.inc("c", 3L)
        map.inc("a")

        map shouldHaveSize 3
        map.count("a") shouldBe 2L
        map.count("d") shouldBe 0L
    }

    @Test
    fun countingMapWithInit() {
        val map = CountingMap(listOf("a", "b", "c", "d"))
        map.inc("a")
        map.inc("b")
        map.inc("c", 3L)
        map.inc("a")

        map shouldHaveSize 4
        map.count("a") shouldBe 3L
        map.count("d") shouldBe 1L
    }
}
