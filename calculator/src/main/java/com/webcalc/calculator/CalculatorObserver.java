package com.webcalc.calculator;

import java.util.UUID;

public interface CalculatorObserver {
  void evaluated(UUID userId, String function);
}
