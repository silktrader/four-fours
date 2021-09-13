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
                .compute()
                .apply(leftOperand.getResult(), rightOperand.getResult()), leftOperand.getVerboseness() + rightOperand.getVerboseness());

        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = operator;
    }

    @Override
    public String toString() {
        if (operator.getBrackets())
            return "(" + leftOperand + operator + rightOperand + ")";
        return leftOperand.toString() + operator + rightOperand.toString();
    }
}
