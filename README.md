# Benchmarks for various implementations of Kotlin readLine

```
Benchmark                          Mode  Cnt    Score   Error  Units
ReadLineBenchmark.bufferedReader   avgt   10   23.319 ± 0.112  ms/op
ReadLineBenchmark.readLine0Kotlin  avgt   10  178.099 ± 0.607  ms/op
ReadLineBenchmark.readLine1        avgt   10   94.635 ± 3.817  ms/op
ReadLineBenchmark.readLine2        avgt   10  102.588 ± 3.019  ms/op
ReadLineBenchmark.readLine3        avgt   10   88.472 ± 2.333  ms/op
ReadLineBenchmark.readLine4        avgt   10   84.426 ± 3.527  ms/op
ReadLineBenchmark.readLine5        avgt   10   64.984 ± 2.303  ms/op
```