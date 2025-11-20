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
@Warmup(iterations = 2, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(1)

public class Benchmarking {

    @Param({"512", "1024"})
    int n;

    private Matrix matrix;
    private OperatingSystemMXBean os;
    private SystemInfo si;
    private GlobalMemory memory;

    long realBefore;
    long cpuBefore;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Matrix für die gegebene Größe erzeugen
        matrix = new Matrix(new Random(), n);

        // CPU Zeit Zugriff
        os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        si = new SystemInfo();
        memory = si.getHardware().getMemory();
    }


    @Setup(Level.Iteration)
    public void setupIteration() {
        realBefore = System.nanoTime();
        cpuBefore  = os.getProcessCpuTime();
    }

    @Benchmark
    public void multiply(Blackhole bh) {

        matrix.multiply();
        bh.consume(matrix.peek());
    }

    @TearDown(Level.Iteration)
    public void tearDownInvocation(ExtraMetrics x) {
        long cpuAfter  = os.getProcessCpuTime();
        long realAfter = System.nanoTime();
        //x.addCPU((double)(cpuAfter - cpuBefore) / (double)(realAfter - realBefore));
        x.CPU = (double)(cpuAfter - cpuBefore) / (double)(realAfter - realBefore);
    }
}

// cd IdeaProjects/Individual-Assignment-Marco-2
// mvn -q -DskipTests package
// java -jar target/benchmarks.jar project.Benchmarking.multiply
