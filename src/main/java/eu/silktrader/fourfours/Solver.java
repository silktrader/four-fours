package eu.silktrader.fourfours;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

// can't finalise class due to benchmark framework
public final class Solver {

  private final HashMap<Integer, Expression> results = new HashMap<>();

  private final ConcurrentHashMap<CostResult, IOperand> operands;

  private final AtomicInteger operandCounter = new AtomicInteger(0);

  private final List<UnaryOperator> unaryOperators = List.of(
   new UnaryOperator(Math::sqrt, "sqrt", 2)
   );
  private final List<BinaryOperator> binaryOperators = List.of(
          new BinaryOperator(Double::sum, "+", true),
          new BinaryOperator((x, y) -> x - y, "-", true),
          new BinaryOperator((x, y) -> x * y, "*", false),
          new BinaryOperator((x, y) -> x / y, "/", false));

  private final int maxCost = 4;
  private final int maxRange;

  public Solver(int maxRange) {
    this.maxRange = maxRange;
    this.operands = buildOperands();
  }

  public static void main(String[] args) {
    var solver = new Solver(101);
    solver.generateAll();

    // separate activities to exclude output from benchmarking
    solver.printAll();
  }

  public ConcurrentHashMap<CostResult, IOperand> buildOperands() {
    var operands = new ConcurrentHashMap<CostResult, IOperand>();
    operands.put(new CostResult(1, 4), new IOperand() {
      public int getCost() { return 1; }
      public int getVerboseness() { return 0; }
      public double getResult() { return 4; }
      public String toString() { return "4"; }
    });
    operands.put(new CostResult(2, 44), new IOperand() {
      public int getCost() { return 2; }
      public int getVerboseness() { return 0; }
      public double getResult() { return 44; }
      public String toString() { return "44"; }
    });
    operands.put(new CostResult(2, 4.4), new IOperand() {
      public int getCost() { return 2; }
      public int getVerboseness() { return 0; }
      public double getResult() { return 4.4; }
      public String toString() { return "4.4"; }
    });
    operands.put(new CostResult(1, 0.4), new IOperand() {
      public int getCost() { return 1; }
      public int getVerboseness() { return 1; }
      public double getResult() { return 0.4; }
      public String toString() { return ".4"; }
    });
    operands.put(new CostResult(1, 24), new IOperand() {
      public int getCost() { return 1; }
      public int getVerboseness() { return 1; }
      public double getResult() { return 24; }
      public String toString() { return "4!"; }
    });
    return operands;
  }

  public void printAll() {
    var missing = new AtomicInteger();
    IntStream.range(0, maxRange).forEach(i -> Optional.ofNullable(results.get(i)).ifPresentOrElse(
            expression -> System.out.println(i + ":  " + expression.trimmedString()),
                    () -> {
              missing.incrementAndGet();
              System.out.println(i + ": missing");}
    ));

    System.out.println("Missing " + missing.get());
    System.out.println("Evaluated " + operandCounter + " expressions");
  }

  public void generateAll() {
    for (var leftOperand : operands.values()) {
      // expand each operand at least once through unary operators
      expandOperand(leftOperand);

      // reached max cost stop combining
      if (leftOperand.getCost() >= maxCost)
        continue;

      for (var rightOperand : operands.values()) {
        // when the combination's cost exceeds the max cost the expression is discarded
        // early exit prevents new objects generation and operator iteration
        final var cost = leftOperand.getCost() + rightOperand.getCost();
        if (cost > maxCost)
          continue;

        for (var operator : binaryOperators) {

          // evaluate result and trim unusable ones
          final var result = operator.compute().apply(leftOperand.getResult(), rightOperand.getResult());
          if (!Double.isFinite(result))
            continue;

          // build an expression with the operands combination and a selected binary operator
          var expression = new BinaryExpression(leftOperand, rightOperand, operator);

          // a valid combination yields an integer
          final var flooredResult = (int) Math.rint(result);
          if (cost == maxCost && Double.compare(flooredResult, result) == 0 && result >= 0) {
            var existingResult = results.get(flooredResult);
            if (existingResult == null || existingResult.getVerboseness() > expression.getVerboseness()) {
              results.put(flooredResult, expression);

              // the low verboseness of the operand is guaranteed by the previous check
              operands.put(new CostResult(cost, result), expression);
              operandCounter.incrementAndGet();
            }
          }
          // the new expression isn't a result and could be added to the expression
          updateOperand(cost, result, expression);
        }
      }
    }
  }

  // expand operands through unary operators
  public void expandOperand(IOperand operand) {

      for (var operator: unaryOperators) {
        // unary operators don't increase expressions costs
        // create an operand when it's missing, update the operand when a less verbose one is found
        final var result = operator.operate().apply(operand.getResult());
        updateOperand(operand.getCost(), result, new UnaryExpression(operand, result, operator));
      }
  }

  public void updateOperand(int cost, double result, IOperand operand) {
    final var identifier = new CostResult(cost, result);
    var matchingOperand = operands.get(identifier);
    if (matchingOperand == null || matchingOperand.getVerboseness() > operand.getVerboseness()) {
      operands.put(identifier, operand);
      operandCounter.incrementAndGet();
      }
  }
}
