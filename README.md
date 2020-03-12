# Benchmarks for various implementations of Kotlin readLine

The test set operation consists of parsing 1M short lines containing consecutive integers starting from 1,
where every 1000th integer is replaced with "long line" of 1000 characters. 
The `xxxBuf1024` benchmarks show the effect of processing every line via the fastest available path in the code 
(when the whole line fits into the buffer).

```
# CPU: Intel(R) Core(TM) i7-6700K CPU @ 4.00GHz
# VM version: JDK 1.8.0_181, Java HotSpot(TM) 64-Bit Server VM, 25.181-b13

Benchmark                           Mode  Cnt    Score   Error  Units
ReadLineBenchmark.bufferedReader    avgt   10   23.271 ± 0.175  ms/op
ReadLineBenchmark.readLine0Kotlin   avgt   10  198.832 ± 0.626  ms/op
ReadLineBenchmark.readLine1         avgt   10   97.584 ± 6.108  ms/op
ReadLineBenchmark.readLine2         avgt   10   97.331 ± 5.124  ms/op
ReadLineBenchmark.readLine3         avgt   10   91.872 ± 5.214  ms/op
ReadLineBenchmark.readLine4         avgt   10   82.428 ± 4.486  ms/op
ReadLineBenchmark.readLine5         avgt   10   65.602 ± 4.337  ms/op
ReadLineBenchmark.readLine6         avgt   10   64.728 ± 3.724  ms/op
ReadLineBenchmark.readLine6Buf1024  avgt   10   58.832 ± 3.199  ms/op
ReadLineBenchmark.readLine7         avgt   10   58.653 ± 3.481  ms/op

# VM options: -XX:TieredStopAtLevel=1

Benchmark                           Mode  Cnt    Score    Error  Units
ReadLineBenchmark.bufferedReader    avgt   10   35.355 ±  0.489  ms/op
ReadLineBenchmark.readLine0Kotlin   avgt   10  424.146 ± 10.130  ms/op
ReadLineBenchmark.readLine1         avgt   10  216.586 ± 10.122  ms/op
ReadLineBenchmark.readLine2         avgt   10  221.682 ± 10.315  ms/op
ReadLineBenchmark.readLine3         avgt   10  203.571 ± 10.565  ms/op
ReadLineBenchmark.readLine4         avgt   10  196.537 ± 13.462  ms/op
ReadLineBenchmark.readLine5         avgt   10  134.433 ±  9.343  ms/op
ReadLineBenchmark.readLine6         avgt   10  115.893 ±  7.359  ms/op
ReadLineBenchmark.readLine6Buf1024  avgt   10  113.296 ±  8.826  ms/op
ReadLineBenchmark.readLine7         avgt   10   87.552 ±  4.142  ms/op

# VM version: JDK 11.0.6, Java HotSpot(TM) 64-Bit Server VM, 11.0.6+8-LTS

Benchmark                           Mode  Cnt    Score   Error  Units
ReadLineBenchmark.bufferedReader    avgt   10   27.170 ± 0.129  ms/op
ReadLineBenchmark.readLine0Kotlin   avgt   10  211.938 ± 0.139  ms/op
ReadLineBenchmark.readLine1         avgt   10  115.319 ± 0.219  ms/op
ReadLineBenchmark.readLine2         avgt   10  116.285 ± 0.178  ms/op
ReadLineBenchmark.readLine3         avgt   10  102.462 ± 0.390  ms/op
ReadLineBenchmark.readLine4         avgt   10   95.076 ± 0.575  ms/op
ReadLineBenchmark.readLine5         avgt   10   76.679 ± 0.189  ms/op
ReadLineBenchmark.readLine6         avgt   10   62.578 ± 0.127  ms/op
ReadLineBenchmark.readLine6Buf1024  avgt   10   61.511 ± 0.138  ms/op
ReadLineBenchmark.readLine7         avgt   10   51.822 ± 0.095  ms/op
```
