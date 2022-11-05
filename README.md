# Advent Of Code 2022

This is the code developed for [Advent of Code 2022](https://adventofcode.com/2022).  My primary goals are:
  - have fun
  - learn something new about Kotlin
  - don't spend more than 2 hours each day
 
I thus favor short and readable code over optimized code. As a rule of thumb, any code which finishes in under 1 minute
is "good enough".  I also will not try to handle possible corner cases if the provided input file does not contain them.
 
AoC provides 2 coding challenges for every day in December until Christmas. Every daily challenge also has one file
containing the data for the challenges.


# Principles

This project setup uses a unit test class per day to provide the solution code. Every class will contain 2 unit tests
(one per challenge), and one or more functions which implement the solution. I chose the unit test approach because that
allows to code the expected/correct result in a standardized way, and because IntelliJ can directly execute either all
or individual tests.

To simplify the test setup, this project provides a Junit5 extension which automates the passing the content of the
daily input file to the unit tests. For this to work, the input files must be named exactly like the classes which
contain the test which consume them. A straight-forward approach is thus to create an input file `input/Day12` and a
test class `Day12` for the challenge of day 12.

Given the day range, it is advised to use 0-padded 2 digits for the day number (e.g. the input for file day 3 should be
named `Day03`).


# Implementation Details

The input test data handling is implemented using a Junit5 extension for test parameters, implemented in
`InputParameterResolver`. To avoid having to annotate test cases, this extension is automatically enabled by using the
files `resources/META-INF/services/org.junit.jupiter.api.extension.Extension` which lists the extension and
`resources/junit-platform.properties` which enables the auto-detection of extensions.


# Example / Template

This repository contains the input file `input/Day00` and the solution file `src/test/kotlin/Day00.kt` as an example.
This `Day00.kt` is a good candidate to be copied for the daily challenges.

- `input/Day00`
```
1
2
3
4
5
```

- `src/test/kotlin/Day00.kt`
```kotlin
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

/*
 Challenge for day 0: given a list of numbers, return their sum. As an example,
 the answer for the following list is 8:
   3
   1
   4

 --- Part Two ---
 Instead of adding the numbers, multiply them. For the above example input, the answer is 12.
 */

class Day00 {
    private val sample = """
        3
        1
        4
    """.trimIndent().lines()

    @Test
    fun testOne(input: List<String>) {
        // provide explicit lists for testing other cases than the actual test input
        one(sample) shouldBe 8
        one(input) shouldBe 15
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 12L
        two(input) shouldBe 120L
    }

    // This should return the sum of the input
    private fun one(input: List<String>): Int = input.map(String::toInt).sum()

    // This should return the product of the input
    private fun two(input: List<String>): Long {
        return input.map(String::toLong).reduce { acc, i -> acc * i }
    }
}
```
