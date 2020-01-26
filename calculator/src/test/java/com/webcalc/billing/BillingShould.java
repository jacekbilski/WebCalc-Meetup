package com.webcalc.billing;

import com.webcalc.calculator.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.UUID;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

class BillingShould {

  private final Billing billing = new Billing();
  private final Calculator calculator = new Calculator();

  private UUID userA = UUID.randomUUID();
  private UUID userB = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    calculator.addObserver(billing);
  }

  @Test
  void returnBalanceZero_whenNoCalculationsWereDone() {
    BigDecimal balance = billing.getBalance(userA);
    assertThat(balance).isEqualByComparingTo(ZERO);
  }

  @Test
  void returnBalanceOfOne_afterCalculatingOneAddition() {
    calculator.eval(userA, "1 2 +", 0);
    BigDecimal balance = billing.getBalance(userA);
    assertThat(balance).isEqualByComparingTo(ONE);
  }

  @Test
  void returnBalanceOfTwo_afterCalculatingTwoAdditions() {
    calculator.eval(userA, "1 2 +", 0);
    calculator.eval(userA, "1 2 +", 0);
    BigDecimal balance = billing.getBalance(userA);
    assertThat(balance).isEqualByComparingTo(new BigDecimal("2"));
  }

  @Test
  void returnBalanceOfFive_afterCalculatingOneMultiplication() {
    calculator.eval(userA, "3 2 *", 0);
    BigDecimal balance = billing.getBalance(userA);
    assertThat(balance).isEqualByComparingTo(new BigDecimal("5"));
  }

  @Test
  void returnBalanceOfOne_afterCalculatingOneSubtraction() {
    calculator.eval(userA, "3 3 -", 0);
    BigDecimal balance = billing.getBalance(userA);
    assertThat(balance).isEqualByComparingTo(ONE);
  }

  @Test
  void returnBalanceOfTen_afterCalculatingOneDivision() {
    calculator.eval(userA, "5 2.5 /", 2);
    BigDecimal balance = billing.getBalance(userA);
    assertThat(balance).isEqualByComparingTo(TEN);
  }

  @DisplayName("Bill complex operations")
  @ParameterizedTest(name = "input: ''{0}'', expected balance: ''{1}''")
  @CsvSource({
      "1 2 *, 5",
      "1 3 0 * +, 6",
      "-1 -1 12 + -, 2",
      "3 6 2 / *, 15",
      "2 3 + ; 8 2 2 / /, 21",
  })
  void complexBilling(String input, BigDecimal expectedBalance) {
    for (String i : input.split(";")) {
      calculator.eval(userA, i, 2);
    }
    BigDecimal balance = billing.getBalance(userA);
    assertThat(balance).isEqualByComparingTo(expectedBalance);
  }

  @Test
  void billOnlyUserA_whenAIsCalculating() {
    calculator.eval(userA, "1 2 +", 0);
    assertThat(billing.getBalance(userA)).isEqualByComparingTo(ONE);
    assertThat(billing.getBalance(userB)).isEqualByComparingTo(ZERO);
  }
}
