package com.webcalc.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Function;

public class Calculator {

  static final int DEFAULT_MAX_FRACTION_DIGITS = 2;

  private final NumberFormat formatter;

  private CalculatorObserver observer;

  private Map<String, Function<EvaluationContext, BigDecimal>> customFunctions = new HashMap<>();

  public Calculator() {
    formatter = DecimalFormat.getNumberInstance(Locale.GERMANY);
    if (formatter instanceof DecimalFormat)
      ((DecimalFormat) formatter).setParseBigDecimal(true);
  }

  public String eval(UUID userId, String input, int maxFractionDigits) {
    String[] tokens = input.trim().split(" ");
    var ctx = new EvaluationContext(new Stack<>());
    var result = eval(userId, ctx, tokens, maxFractionDigits);
    return format(result, maxFractionDigits);
  }

  private BigDecimal eval(UUID userId, EvaluationContext ctx, String[] tokens, int maxFractionDigits) {
    for (String token : tokens) {
      try {
        var value = parse(token);
        ctx.stack.push(value);
      } catch (Exception e) {
        var f = function(token, maxFractionDigits);
        ctx.stack.push(f.apply(ctx));
        if (observer != null)
          observer.evaluated(userId, token);
      }
    }
    return ctx.stack.pop();
  }

  private Function<EvaluationContext, BigDecimal> function(String function, int maxFractionDigits) {
    switch (function) {
      case "+":
        return ctx -> ctx.stack.pop().add(ctx.stack.pop());
      case "-":
        return ctx -> {
          BigDecimal a = ctx.stack.pop();
          BigDecimal b = ctx.stack.pop();
          return b.subtract(a);};
      case "*":
        return ctx -> ctx.stack.pop().multiply(ctx.stack.pop());
      case "/":
        return ctx -> {
          var a = ctx.stack.pop();
          var b = ctx.stack.pop();
          return b.divide(a, maxFractionDigits, RoundingMode.HALF_UP);
        };
      case "^":
        return ctx -> {
          var a = ctx.stack.pop();
          var b = ctx.stack.pop();
          return BigDecimal.valueOf(Math.pow(b.doubleValue(), a.doubleValue()));
        };
      case "^2":
        return ctx -> ctx.stack.pop().pow(2);
      case "π":
        return ctx -> BigDecimal.valueOf(Math.PI);
      default:
        if (customFunctions.containsKey(function))
          return customFunctions.get(function);
        else
          throw new RuntimeException("Unsupported function: " + function);
    }
  }

  public void defineCustomFunction(String definition) {
    String[] tokens = definition.trim().split(" ");
    if (tokens[0].equals("e")) {
      customFunctions.put(tokens[0], ctx -> parse(tokens[1]));
    } else {
      // "2 *"
      customFunctions.put(tokens[0], ctx -> {
        var value = parse(tokens[1]);
        ctx.stack.push(value);
        var f = function(tokens[2], DEFAULT_MAX_FRACTION_DIGITS);
        return f.apply(ctx);
      });
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

  private static class EvaluationContext {
    public final Stack<BigDecimal> stack;

    public EvaluationContext(Stack<BigDecimal> stack) {
      this.stack = stack;
    }
  }
}
