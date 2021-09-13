package eu.silktrader.fourfours;

abstract class Expression implements IOperand {

  private final int cost;
  private final double result;
  private final int verboseness;

  public Expression(int cost, double result, int verboseness) {
    this.cost = cost;
    this.result = result;
    this.verboseness = verboseness;
  }

  public int getCost() {
    return cost;
  }

  public double getResult() {
    return result;
  }

  public int getVerboseness() {
    return verboseness;
  }
}


