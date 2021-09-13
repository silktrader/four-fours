package eu.silktrader.fourfours;

final class UnaryExpression extends Expression {

    private final IOperand operand;
    private final UnaryOperator operator;

    public UnaryExpression(IOperand operand, double result, UnaryOperator operator) {
    super(
        operand.getCost(),
        result,
        operand.getVerboseness() + operator.verboseness());

        this.operand = operand;
        this.operator = operator;
    }

    public String toString() {
        return operator + "(" + operand + ")";
    }
}
