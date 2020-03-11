# Benchmarks for various implementations of Kotlin readLine

```
Benchmark                          Mode  Cnt    Score   Error  Units
ReadLineBenchmark.bufferedReader   avgt   10   21.920 ? 0.112  ms/op
ReadLineBenchmark.readLine0Kotlin  avgt   10  158.992 ? 0.411  ms/op
ReadLineBenchmark.readLine1        avgt   10   99.055 ? 5.390  ms/op
ReadLineBenchmark.readLine2        avgt   10   92.635 ? 4.664  ms/op
ReadLineBenchmark.readLine3        avgt   10   88.479 ? 5.064  ms/op
ReadLineBenchmark.readLine4        avgt   10   91.147 ? 5.003  ms/op
```