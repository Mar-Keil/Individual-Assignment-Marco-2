package project;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@Fork(value = 1)

public class Benchmarking {

    @Param({"128", "256"})
    int n;

    private Matrix matrix;

    @Setup(Level.Trial)
    public void setupTrial() {
        matrix = new Matrix(new Random(), n);
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        matrix.clearC();
    }

    @Benchmark
    public void multiply(Blackhole bh) {
        matrix.multiply();
        bh.consume(matrix.peek());
    }
}