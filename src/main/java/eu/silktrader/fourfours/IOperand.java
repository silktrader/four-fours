package eu.silktrader.fourfours;

public interface IOperand {
  int getCost();
  int getVerboseness();
  double getResult();
  String toString();

  default String trimmedString() {
    final var s = this.toString();
    return (s.charAt(0) == '(' && s.charAt(s.length()-1) == ')') ? s.substring(1, s.length() - 1) : s;
  }
  //String asFormula();
}
