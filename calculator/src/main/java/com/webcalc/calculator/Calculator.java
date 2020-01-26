package com.webcalc.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Stack;
import java.util.UUID;
import java.util.function.BinaryOperator;

public class Calculator {

  static final int DEFAULT_MAX_FRACTION_DIGITS = 2;

  private final NumberFormat formatter;

  private CalculatorObserver observer;

  public Calculator() {
    formatter = DecimalFormat.getNumberInstance(Locale.GERMANY);
    if (formatter instanceof DecimalFormat)
      ((DecimalFormat) formatter).setParseBigDecimal(true);
  }

  public String eval(UUID userId, String input, int maxFractionDigits) {
    String[] tokens = input.trim().split(" ");
    var stack = new Stack<BigDecimal>();
    for (String token : tokens) {
      try {
        var value = parse(token);
        stack.push(value);
      } catch (Exception e) {
        var f = function(token, maxFractionDigits);
        var a = stack.pop();
        var b = stack.pop();
        stack.push(f.apply(b, a));
        if (observer != null)
          observer.evaluated(userId, token);
      }
    }
    return format(stack.pop(), maxFractionDigits);
  }

  private BinaryOperator<BigDecimal> function(String function, int maxFractionDigits) {
    switch (function) {
      case "+":
        return BigDecimal::add;
      case "-":
        return BigDecimal::subtract;
      case "*":
        return BigDecimal::multiply;
      case "/":
        return (v1, v2) -> v1.divide(v2, maxFractionDigits, RoundingMode.HALF_UP);
      default:
        throw new RuntimeException("Unsupported function: " + function);
    }
  }

  private BigDecimal parse(String string) {
    try {
      return (BigDecimal) formatter.parse(string);
    } catch (ParseException e) {
      throw new RuntimeException("Cannot recognize a number: '" + string + "'", e);
    }
  }

  private String format(BigDecimal result, int maxFractionDigits) {
    NumberFormat formatter = (NumberFormat) this.formatter.clone();
    formatter.setMaximumFractionDigits(maxFractionDigits);
    return formatter.format(result);
  }

  public void addObserver(CalculatorObserver observer) {
    this.observer = observer;
  }
}
