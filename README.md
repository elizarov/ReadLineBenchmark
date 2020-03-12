# Benchmarks for various implementations of Kotlin readLine

```
# CPU: Intel(R) Core(TM) i7-6700K CPU @ 4.00GHz
# VM version: JDK 1.8.0_181, Java HotSpot(TM) 64-Bit Server VM, 25.181-b13

Benchmark                          Mode  Cnt    Score   Error  Units
ReadLineBenchmark.bufferedReader   avgt   10   24.262 ± 1.187  ms/op
ReadLineBenchmark.readLine0Kotlin  avgt   10  181.997 ± 4.093  ms/op
ReadLineBenchmark.readLine1        avgt   10   97.824 ± 5.454  ms/op
ReadLineBenchmark.readLine2        avgt   10   98.144 ± 3.688  ms/op
ReadLineBenchmark.readLine3        avgt   10   87.376 ± 2.598  ms/op
ReadLineBenchmark.readLine4        avgt   10   89.167 ± 4.360  ms/op
ReadLineBenchmark.readLine5        avgt   10   78.017 ± 2.548  ms/op
ReadLineBenchmark.readLine6        avgt   10   61.786 ± 2.381  ms/op

# VM options: -XX:TieredStopAtLevel=1

Benchmark                          Mode  Cnt    Score     Error  Units
ReadLineBenchmark.bufferedReader   avgt   10   35.280 ±   0.314  ms/op
ReadLineBenchmark.readLine0Kotlin  avgt   10  465.802 ± 145.714  ms/op
ReadLineBenchmark.readLine1        avgt   10  229.406 ±  10.222  ms/op
ReadLineBenchmark.readLine2        avgt   10  223.688 ±   9.973  ms/op
ReadLineBenchmark.readLine3        avgt   10  203.236 ±   8.941  ms/op
ReadLineBenchmark.readLine4        avgt   10  195.250 ±  10.543  ms/op
ReadLineBenchmark.readLine5        avgt   10  134.734 ±   9.185  ms/op
ReadLineBenchmark.readLine6        avgt   10  117.175 ±   8.384  ms/op
```
