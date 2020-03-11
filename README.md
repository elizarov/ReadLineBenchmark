# Benchmarks for various implementations of Kotlin readLine

```
Intel(R) Core(TM) i7-6700K CPU @ 4.00GHz

java version "1.8.0_181"
Java(TM) SE Runtime Environment (build 1.8.0_181-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)

Benchmark                          Mode  Cnt    Score   Error  Units
ReadLineBenchmark.bufferedReader   avgt   10   24.262 ± 1.187  ms/op
ReadLineBenchmark.readLine0Kotlin  avgt   10  181.997 ± 4.093  ms/op
ReadLineBenchmark.readLine1        avgt   10   97.824 ± 5.454  ms/op
ReadLineBenchmark.readLine2        avgt   10   98.144 ± 3.688  ms/op
ReadLineBenchmark.readLine3        avgt   10   87.376 ± 2.598  ms/op
ReadLineBenchmark.readLine4        avgt   10   89.167 ± 4.360  ms/op
ReadLineBenchmark.readLine5        avgt   10   78.017 ± 2.548  ms/op
ReadLineBenchmark.readLine6        avgt   10   61.786 ± 2.381  ms/op
```