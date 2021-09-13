package eu.silktrader.fourfours;
public record Unit(int cost, double result, String string) implements IOperand {

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public int getVerboseness() {
        return 0;
    }

    @Override
    public double getResult() {
        return result;
    }

    @Override
    public String toString() {
        return string;
    }
}
