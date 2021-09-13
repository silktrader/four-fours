package eu.silktrader.fourfours;

import java.util.function.BiFunction;

record BinaryOperator(BiFunction<Double, Double, Double> operation, String string, Precedence precedence, InvertedOperand invertedOperand) {

  public Precedence getPrecedence() {
    return precedence;
  }

  @Override
  public String toString() {
    return string;
  }

  public Double compute(IOperand left, IOperand right) {
    return operation().apply(left.getResult(), right.getResult());
  }
}

enum InvertedOperand {
  YES,
  NO
}