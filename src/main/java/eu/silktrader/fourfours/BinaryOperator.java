package eu.silktrader.fourfours;

import java.util.function.BiFunction;

record BinaryOperator(BiFunction<Double, Double, Double> compute, String string, Boolean brackets) implements IOperator {

//  BiFunction<Double, Double, Double> getCompute() {
//    return compute;
//  }

  public Boolean getBrackets() {
    return brackets;
  }

  @Override
  public String toString() {
    return string;
  }
}
