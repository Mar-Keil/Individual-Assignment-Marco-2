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

    @Param({"512", "1024"})
    int n;

    private Matrix matrix;
    private OperatingSystemMXBean os;
    private SystemInfo si;

    private long cpuTimeBefore;
    private long realTimeBefore;
    private double sumCpuTime = 0;
    private int iterations = 0;

    private long trialUsedMem;
    private long iterationUsedMem;
    private long maxUsedMem;
    private long totalMem;

    @AuxCounters(AuxCounters.Type.EVENTS)
    @State(Scope.Thread)
    public static class ExtraMetrics {
        public long sumCpuShareTimes1000 = 0;
        public long cpuSamples = 0;
        public long peakRamMb = 0;
    }

    @Setup(Level.Trial)
    public void setupTrial() {
        matrix = new Matrix(new Random(), n);

        si = new SystemInfo();

        // Memory trial measurement
        var proc = si.getOperatingSystem().getCurrentProcess();
        trialUsedMem = (proc == null ? 0L : proc.getResidentSetSize() / (1024 * 1024));
        maxUsedMem = trialUsedMem;

        // Memory global measurement
        GlobalMemory mem = si.getHardware().getMemory();
        totalMem = mem.getTotal() / (1024 * 1024);

        os = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);

        System.out.printf(
                "TRIAL START  RAM Baseline=%d MB  TotalRAM=%d MB%n",
                trialUsedMem, totalMem
        );
    }

    @Setup(Level.Iteration)
    public void setupInvocation() {
        realTimeBefore = System.nanoTime();
        cpuTimeBefore  = os.getProcessCpuTime();
        iterationUsedMem = 0;
    }

    @Benchmark
    public void multiply(Blackhole bh) {
        matrix.multiply();
        bh.consume(matrix.peek());
    }

    @TearDown(Level.Invocation)
    public void tearDownInvocation() {
        long tempMem;
        var proc = si.getOperatingSystem().getCurrentProcess();
        tempMem = (proc == null ? 0L : proc.getResidentSetSize() / (1024 * 1024));
        iterationUsedMem = Math.max(tempMem, iterationUsedMem);
    }

    @TearDown(Level.Iteration)
    public void tearDownIteration() {
        long cpuAfter  = os.getProcessCpuTime();
        long realAfter = System.nanoTime();

        long cpuTimeDelta = cpuAfter - cpuTimeBefore;
        long realTimeDelta = realAfter - realTimeBefore;
        double usedCPU = (double) cpuTimeDelta / (double) realTimeDelta;
        sumCpuTime += usedCPU;
        iterations++;

        maxUsedMem = Math.max(iterationUsedMem, maxUsedMem);

        System.out.printf("CPU Anteil=%.3f  IterationMaxRam=%d MB%n",
                usedCPU, iterationUsedMem);
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() {
        double avgCPU = (iterations > 0) ? (sumCpuTime / iterations) : 0.0;

        System.out.printf(
                "TRIAL END    avg CPU Anteil=%.3f  MaxRam=%d MB  TrialRam=%d MB  TotalRam=%d MB%n",
                avgCPU, maxUsedMem, trialUsedMem, totalMem
        );
    }
}

// cd IdeaProjects/Individual-Assignment-Marco-2
// mvn -q -DskipTests package
// java -jar target/benchmarks.jar project.Benchmarking.multiply
