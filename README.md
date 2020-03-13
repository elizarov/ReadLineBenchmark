# Benchmarks for various implementations of Kotlin readLine

The test set operation consists of parsing 1M short lines containing consecutive integers starting from 1,
where every 1000th integer is replaced with "long line" of 1000 characters. 
The `xxxBuf1024` benchmarks show the effect of processing every line via the fastest available path in the code 
(when the whole line fits into the buffer).

```
# CPU: Intel(R) Core(TM) i7-6700K CPU @ 4.00GHz
# VM version: JDK 1.8.0_181, Java HotSpot(TM) 64-Bit Server VM, 25.181-b13

Benchmark                           Mode  Cnt    Score   Error  Units
ReadLineBenchmark.bufferedReader    avgt   20   23.285 ± 0.130  ms/op
ReadLineBenchmark.readLine0Kotlin   avgt   20  178.876 ± 0.966  ms/op
ReadLineBenchmark.readLine1         avgt   20   96.622 ± 3.371  ms/op
ReadLineBenchmark.readLine2         avgt   20   98.148 ± 1.983  ms/op
ReadLineBenchmark.readLine3         avgt   20   88.033 ± 2.541  ms/op
ReadLineBenchmark.readLine4         avgt   20   85.007 ± 1.711  ms/op
ReadLineBenchmark.readLine5         avgt   20   65.842 ± 2.082  ms/op
ReadLineBenchmark.readLine6         avgt   20   60.248 ± 1.165  ms/op
ReadLineBenchmark.readLine6Buf1024  avgt   20   58.167 ± 1.188  ms/op
ReadLineBenchmark.readLine6NoLV     avgt   20   59.149 ± 1.833  ms/op
ReadLineBenchmark.readLine7         avgt   20   60.639 ± 1.249  ms/op
ReadLineBenchmark.readLine7NoLV     avgt   20   62.213 ± 2.303  ms/op

# VM options: -XX:TieredStopAtLevel=1

Benchmark                           Mode  Cnt    Score   Error  Units
ReadLineBenchmark.bufferedReader    avgt   20   35.094 ± 0.080  ms/op
ReadLineBenchmark.readLine0Kotlin   avgt   20  420.412 ± 3.703  ms/op
ReadLineBenchmark.readLine1         avgt   20  219.640 ± 5.752  ms/op
ReadLineBenchmark.readLine2         avgt   20  221.053 ± 4.746  ms/op
ReadLineBenchmark.readLine3         avgt   20  199.308 ± 3.844  ms/op
ReadLineBenchmark.readLine4         avgt   20  193.526 ± 3.407  ms/op
ReadLineBenchmark.readLine5         avgt   20  128.935 ± 3.483  ms/op
ReadLineBenchmark.readLine6         avgt   20  115.163 ± 2.447  ms/op
ReadLineBenchmark.readLine6Buf1024  avgt   20  111.091 ± 2.535  ms/op
ReadLineBenchmark.readLine6NoLV     avgt   20  114.116 ± 2.359  ms/op
ReadLineBenchmark.readLine7         avgt   20   86.925 ± 1.420  ms/op
ReadLineBenchmark.readLine7NoLV     avgt   20   86.485 ± 1.393  ms/op

# VM version: JDK 11.0.6, Java HotSpot(TM) 64-Bit Server VM, 11.0.6+8-LTS

Benchmark                           Mode  Cnt    Score   Error  Units
ReadLineBenchmark.bufferedReader    avgt   20   26.583 ± 0.386  ms/op
ReadLineBenchmark.readLine0Kotlin   avgt   20  209.667 ± 0.448  ms/op
ReadLineBenchmark.readLine1         avgt   20  110.523 ± 2.173  ms/op
ReadLineBenchmark.readLine2         avgt   20  107.620 ± 0.215  ms/op
ReadLineBenchmark.readLine3         avgt   20  101.324 ± 1.396  ms/op
ReadLineBenchmark.readLine4         avgt   20   94.983 ± 0.079  ms/op
ReadLineBenchmark.readLine5         avgt   20   76.532 ± 0.167  ms/op
ReadLineBenchmark.readLine6         avgt   20   63.583 ± 0.483  ms/op
ReadLineBenchmark.readLine6Buf1024  avgt   20   57.776 ± 0.550  ms/op
ReadLineBenchmark.readLine6NoLV     avgt   20   61.892 ± 0.483  ms/op
ReadLineBenchmark.readLine7         avgt   20   51.050 ± 0.191  ms/op
ReadLineBenchmark.readLine7NoLV     avgt   20   53.463 ± 0.056  ms/op
```
