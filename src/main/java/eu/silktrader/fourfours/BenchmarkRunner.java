package eu.silktrader.fourfours;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkRunner {

  public static void main(String[] args) throws RunnerException {
    var options = new OptionsBuilder()
      .include(BenchmarkRunner.class.getSimpleName())
      .build();

    new Runner(options).run();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Fork(value = 3, warmups = 1)
  public void benchmarkAll() {
    var solver = new Solver(20);
    solver.solve();
  }
}
