package com.webcalc.calculator;

import com.webcalc.user.SpringUserAdapter;
import com.webcalc.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class CalculatorController {

  private static final String MAX_FRACTION_DIGITS = "maxFractionDigits";

  private final Calculator calculator;

  public CalculatorController(Calculator calculator) {
    this.calculator = calculator;
  }

  @PostMapping("/eval")
  public String calculate(@RequestBody String body, HttpSession session, HttpServletRequest request) {
    if (session.getAttribute(MAX_FRACTION_DIGITS) == null)
      session.setAttribute(MAX_FRACTION_DIGITS, Calculator.DEFAULT_MAX_FRACTION_DIGITS);
    Authentication auth = (Authentication) request.getUserPrincipal();
    User user = ((SpringUserAdapter) auth.getPrincipal()).getUser();
    return calculator.eval(user.id, body, (Integer) session.getAttribute(MAX_FRACTION_DIGITS));
  }

  @PutMapping("/maxFractionDigits")
  public void setMaxFractionDigits(@RequestBody String body, HttpSession session) {
    session.setAttribute(MAX_FRACTION_DIGITS, Integer.parseInt(body));
  }
}
