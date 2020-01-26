package com.webcalc.billing;

import com.webcalc.user.SpringUserAdapter;
import com.webcalc.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BillingController {

  private final Billing billing;

  public BillingController(Billing billing) {
    this.billing = billing;
  }

  @GetMapping("/balance")
  public String getBalance(HttpServletRequest request) {
    Authentication auth = (Authentication) request.getUserPrincipal();
    User user = ((SpringUserAdapter) auth.getPrincipal()).getUser();
    return billing.getBalance(user.id).toPlainString();
  }
}
