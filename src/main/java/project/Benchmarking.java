package project;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import com.sun.management.OperatingSystemMXBean;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(value = 1)

public class Benchmarking {

    @Param({"1024"})
    int n;

    private Matrix matrix;
    private OperatingSystemMXBean os;
    private SystemInfo si;
    private GlobalMemory mem;

    private long cpuTimeBefore;
    private long realTimeBefore;
    private long freeMemBefore;
    private long totalMem;

    @Setup(Level.Trial)
    public void setupTrial() {
        matrix = new Matrix(new Random(), n);
        os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        mem = si.getHardware().getMemory();
        totalMem = mem.getTotal();
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        matrix.clearC();
        freeMemBefore = mem.getAvailable();
        cpuTimeBefore = ManagementFactory.getRuntimeMXBean().getStartTime();
        realTimeBefore = System.nanoTime();
    }

    @Benchmark
    public void multiply(Blackhole bh) {
        matrix.multiply();
        bh.consume(matrix.peek());
    }

    @TearDown(Level.Iteration)
    public void tearDownIteration() {
        long freeAfter = mem.getAvailable();
        long cpuAfter  = os.getProcessCpuTime();
        long realAfter = System.nanoTime();

        long cpuTimeDelta  = cpuAfter - cpuTimeBefore;
        long realTimeDelta = realAfter - realTimeBefore;
        double usedCPU     = (double) cpuTimeDelta / (double) realTimeDelta;

        long usedDeltaMB = ((totalMem - freeAfter) - (totalMem - freeMemBefore)) / (1024 * 1024);
        long maxRamMB    = totalMem / (1024 * 1024);

        System.out.printf("CPU Anteil=%.3f  RAM Delta=%d MB  MaxRAM=%d MB%n",
                usedCPU, usedDeltaMB, maxRamMB);
    }
}