package eu.silktrader.fourfours;

import java.util.function.Function;

record UnaryOperator(Function<Double, Double> operate, String string, int verboseness) implements IOperator {

    @Override
    public Boolean getBrackets() {
        return false;
    }

    @Override
    public String toString() {
      return string;
    }
}
