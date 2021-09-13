package eu.silktrader.fourfours;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public final class Solver {

  private final HashMap<Integer, Expression> results = new HashMap<>();

  private final ConcurrentHashMap<CostResult, IOperand> operands;

  private final List<UnaryOperator> unaryOperators = List.of(
   new UnaryOperator(Math::sqrt, "sqrt", 2)
   );
  private final List<BinaryOperator> binaryOperators = List.of(
          new BinaryOperator(Double::sum, "+", Precedence.LOW, InvertedOperand.NO),
          new BinaryOperator((x, y) -> x - y, "-", Precedence.LOW, InvertedOperand.NO),
          new BinaryOperator((x, y) -> y - x, "-", Precedence.LOW, InvertedOperand.YES),
          new BinaryOperator((x, y) -> x * y, "*", Precedence.HIGH, InvertedOperand.NO),
          new BinaryOperator((x, y) -> y / x, "/", Precedence.HIGH, InvertedOperand.YES),
          new BinaryOperator((x, y) -> x / y, "/", Precedence.HIGH, InvertedOperand.NO));

  private final int maxCost = 4;
  private final int maxRange;

  public Solver(int maxRange) {
    this.maxRange = maxRange;
    this.operands = buildOperands();
  }

  public static void main(String[] args) {
    var solver = new Solver(101);
    solver.solve();

    // separate activities to exclude output from benchmarking
    solver.printAll();
  }

  private ConcurrentHashMap<CostResult, IOperand> buildOperands() {
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
    operands.put(new CostResult(1, 4/9.0), new IOperand() {
      public int getCost() { return 1; }
      public int getVerboseness() { return 2; }
      public double getResult() { return 4/9.0; }
      public String toString() { return "ā"; }
    });
    return operands;
  }

  private void printAll() {
    var missing = new AtomicInteger();
    IntStream.range(0, maxRange).forEach(i -> Optional.ofNullable(results.get(i)).ifPresentOrElse(
            expression -> System.out.println(i + ":  " + expression.trimmedString()),
                    () -> {
              missing.incrementAndGet();
              System.out.println(i + ": missing");}
    ));

    System.out.println("Missing " + missing.get());
  }

  private Collection<IOperand> expand(IOperand operand) {

    // unary operators don't increase expressions costs
    // create an operand when it's missing, update the operand when a less verbose one is found
    var newOperands = new ArrayList<IOperand>();
    for (var operator: unaryOperators) {
      final var result = operator.operate().apply(operand.getResult());
      final var identifier = new CostResult(operand.getCost(), result);
      var matchingOperand = operands.get(identifier);

      // the new operand triggers a new value and should be evaluated in future combinations
      if (matchingOperand == null || matchingOperand.getVerboseness() > operand.getVerboseness()) {
        final var newOperand = new UnaryExpression(operand, result, operator);
        operands.put(identifier, newOperand);
        newOperands.add(newOperand);
      }
    }
    return newOperands;
  }

  private List<IOperand> combine(Iterable<IOperand> leftOperands, Iterable<IOperand> rightOperands) {

    final var newOperands = new ArrayList<IOperand>();

    for (var leftOperand: leftOperands) {
      for (var rightOperand: rightOperands) {

        // no matter what the operator is, the final cost exceeds the available budget in digits
        final var cost = leftOperand.getCost() + rightOperand.getCost();
        if (cost > maxCost)
          continue;

        for (var operator: binaryOperators) {
          // evaluate result and trim unusable ones
          final var result = operator.compute(leftOperand, rightOperand);
          if (!Double.isFinite(result))
            continue;

          // build an expression with the operands combination and a selected binary operator
          var expression = new BinaryExpression(leftOperand, rightOperand, operator);

          // TK if there's an operand with the same cost and result, but lower verboseness, substitute it

          // a valid combination yields an integer
          final var flooredResult = (int) Math.rint(result);
          if (cost == maxCost && Double.compare(flooredResult, result) == 0 && result >= 0) {
            var existingResult = results.get(flooredResult);
            if (existingResult == null || existingResult.getVerboseness() > expression.getVerboseness())
              results.put(flooredResult, expression);
          }

          // add the operand, even when the cost amounts to the max — as it can be augmented by unary operators
          newOperands.add(expression);
        }
      }
    }
    return newOperands;
  }

  public void solve() {

    // expand initial operands with unary operators
    final var unaryOperands = new ArrayList<>(operands.values());
    for (var o: operands.values()) {
      unaryOperands.addAll(expand(o));
    }

    // 1 + 1 = 2
    final var binaryOperands = combine(unaryOperands, unaryOperands);

    // 2 + 1 = 3
    final var threeOperands = combine(unaryOperands, binaryOperands);

    // 2 + 2 = 4
    final var fourOperands = combine(binaryOperands, binaryOperands);

    // 3 + 1 = 4
    // makes sense to stop as each operand's cost is at least one
    final var finalOperands = combine(unaryOperands, threeOperands);
  }

}
