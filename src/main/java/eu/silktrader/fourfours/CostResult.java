package eu.silktrader.fourfours;

import java.util.Objects;

record CostResult(int cost, double result) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CostResult that = (CostResult) o;
        // watch out for double equalities, use delta
        return cost == that.cost && Double.compare(that.result, result) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost, result);
    }
}
