This directory contains the input files for the different days.

If the input file has the same name as the test class which uses it, you can simply create tests like

```kotlin
@Test
fun myTest(input: List<String>) {
    TODO("use [input] in this test")
}
```

and your test will automatically be called with the lines of the matching file as a `List<String>`.
