package com.webcalc.billing;

import com.webcalc.calculator.CalculatorObserver;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;

public class Billing implements CalculatorObserver {

  private Map<UUID, BigDecimal> balance = new HashMap<>();

  public BigDecimal getBalance(UUID userId) {
    return balance.getOrDefault(userId, ZERO);
  }

  @Override
  public void evaluated(UUID userId, String function) {
    balance.putIfAbsent(userId, ZERO);
    switch (function) {
      case "+":
      case "-":
        balance.merge(userId, ONE, BigDecimal::add);
        break;
      case "*":
        balance.merge(userId, new BigDecimal("5"), BigDecimal::add);
        break;
      case "/":
        balance.merge(userId, TEN, BigDecimal::add);
        break;
    }
  }
}
