package benchmark

import org.openjdk.jmh.annotations.*
import java.io.*
import java.util.concurrent.*

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 6, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class ReadLineBenchmark {
    private val charset = Charsets.UTF_8
    private val n = 1_000_000
    private val text = (1..n).joinToString("\r\n") {
        if (it % 1000 == 0) "x".repeat(1000) else it.toString() // periodic long string
    }
    private val bytes = text.toByteArray(charset)
    private val baselineHash = bufferedReader()

    private fun input() = ByteArrayInputStream(bytes)

    @Benchmark
    fun bufferedReader(): Int {
        val reader = input().bufferedReader(charset)
        var h = 0
        while (true) {
            val line = reader.readLine() ?: break
            h += line.hashCode()
        }
        return h
    }

    @Benchmark
    fun readLine0Kotlin(): Int {
        System.setIn(input())
        var h = 0
        while (true) {
            val line = readLine() ?: break
            h += line.hashCode()
        }
        check(h == baselineHash)
        return h
    }

    @Benchmark
    fun readLine1(): Int {
        val input = input()
        var h = 0
        while (true) {
            val line = LineReader1.readLine(input, charset) ?: break
            h += line.hashCode()
        }
        check(h == baselineHash)
        return h
    }

    @Benchmark
    fun readLine2(): Int {
        val input = input()
        var h = 0
        while (true) {
            val line = LineReader2.readLine(input, charset) ?: break
            h += line.hashCode()
        }
        check(h == baselineHash)
        return h
    }

    @Benchmark
    fun readLine3(): Int {
        val input = input()
        var h = 0
        while (true) {
            val line = LineReader3.readLine(input, charset) ?: break
            h += line.hashCode()
        }
        check(h == baselineHash)
        return h
    }

    @Benchmark
    fun readLine4(): Int {
        val input = input()
        var h = 0
        while (true) {
            val line = LineReader4.readLine(input, charset) ?: break
            h += line.hashCode()
        }
        check(h == baselineHash)
        return h
    }

    @Benchmark
    fun readLine5(): Int {
        val input = input()
        var h = 0
        while (true) {
            val line = LineReader5.readLine(input, charset) ?: break
            h += line.hashCode()
        }
        check(h == baselineHash)
        return h
    }

    @Benchmark
    fun readLine6(): Int {
        val input = input()
        var h = 0
        while (true) {
            val line = LineReader6.readLine(input, charset) ?: break
            h += line.hashCode()
        }
        check(h == baselineHash)
        return h
    }

    @Benchmark
    fun readLine6Buf1024(): Int {
        val input = input()
        var h = 0
        while (true) {
            val line = LineReader6Buf1024.readLine(input, charset) ?: break
            h += line.hashCode()
        }
        check(h == baselineHash)
        return h
    }

    @Benchmark
    fun readLine6NoLV(): Int {
        val input = input()
        var h = 0
        while (true) {
            val line = LineReader6NoLV.readLine(input, charset) ?: break
            h += line.hashCode()
        }
        check(h == baselineHash)
        return h
    }

    @Benchmark
    fun readLine7(): Int {
        val input = input()
        var h = 0
        while (true) {
            val line = LineReader7.readLine(input, charset) ?: break
            h += line.hashCode()
        }
        check(h == baselineHash)
        return h
    }

    @Benchmark
    fun readLine7NoLV(): Int {
        val input = input()
        var h = 0
        while (true) {
            val line = LineReader7NoLV.readLine(input, charset) ?: break
            h += line.hashCode()
        }
        check(h == baselineHash)
        return h
    }
}