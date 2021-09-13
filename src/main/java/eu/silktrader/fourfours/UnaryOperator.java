package eu.silktrader.fourfours;

import java.util.function.Function;

record UnaryOperator(Function<Double, Double> operate, String string, int verboseness) {

    @Override
    public String toString() {
      return string;
    }
}
