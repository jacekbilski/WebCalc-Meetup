package com.webcalc.app;

import com.webcalc.billing.Billing;
import com.webcalc.calculator.Calculator;
import com.webcalc.user.WebCalcUserDetailsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootApplication
@ComponentScan("com.webcalc")
public class WebCalcApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebCalcApplication.class, args);
  }

  @Bean
  public Calculator calculator() {
    return new Calculator();
  }

  @Bean
  public Billing billing(Calculator calculator) {
    Billing billing = new Billing();
    calculator.addObserver(billing);
    return billing;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return new WebCalcUserDetailsService();
  }

  @Configuration
  protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.authorizeRequests()
          .anyRequest().fullyAuthenticated()
          .and().httpBasic()
          .and().csrf().disable();
    }
  }
}
