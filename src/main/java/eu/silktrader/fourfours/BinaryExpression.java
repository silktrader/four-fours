package eu.silktrader.fourfours;

public final class BinaryExpression extends Expression {

    private final IOperand leftOperand;
    private final IOperand rightOperand;
    private final BinaryOperator operator;

    public BinaryExpression(
            IOperand leftOperand,
            IOperand rightOperand,
            BinaryOperator operator
    ) {
        super(leftOperand.getCost() + rightOperand.getCost(), operator
                .operation()
                .apply(leftOperand.getResult(), rightOperand.getResult()), leftOperand.getVerboseness() + rightOperand.getVerboseness());

        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = operator;
    }

    public BinaryExpression(
            IOperand leftOperand,
            IOperand rightOperand,
            BinaryOperator operator,
            int cost,
            double result
    ) {
        super(cost, result, leftOperand.getVerboseness() + rightOperand.getVerboseness());

        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = operator;
    }

    @Override
    public String toString() {

        final var base = operator.invertedOperand() == InvertedOperand.YES ?
                rightOperand.toString() + operator + leftOperand
                : leftOperand.toString() + operator + rightOperand;

        if (operator.getPrecedence() == Precedence.LOW)
            return "(" + base + ")";
        return base;
    }
}
