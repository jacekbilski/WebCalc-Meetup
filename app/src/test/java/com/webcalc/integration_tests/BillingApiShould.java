package com.webcalc.integration_tests;

import com.webcalc.app.WebCalcApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(classes = WebCalcApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BillingApiShould {

  private static final String USER1_NAME = "username1";
  private static final String USER1_PASS = "password1";

  private static final String USER2_NAME = "username2";
  private static final String USER2_PASS = "password2";

  @Test
  void returnsZeroWhenNoCalculationsDone() {
    given()
        .auth().preemptive().basic(USER1_NAME, USER1_PASS)
    .when()
        .get("/balance")
    .then()
        .body(equalTo("0"));
  }

  @Test
  void chargeUser1_when1IsCalculating() {
    given()
        .auth().preemptive().basic(USER1_NAME, USER1_PASS)
        .body("1 2 +")
    .when()
        .post("/eval");

    given()
        .auth().preemptive().basic(USER1_NAME, USER1_PASS)
    .when()
        .get("/balance")
    .then()
        .body(equalTo("1"));
  }

  @Test
  void chargeOnlyUser1_when1IsCalculating() {
    given()
        .auth().preemptive().basic(USER1_NAME, USER1_PASS)
        .body("1 2 +")
    .when()
        .post("/eval");

    given()
        .auth().preemptive().basic(USER1_NAME, USER1_PASS)
    .when()
        .get("/balance")
    .then()
        .body(equalTo("1"));

    given()
        .auth().preemptive().basic(USER2_NAME, USER2_PASS)
    .when()
        .get("/balance")
    .then()
        .body(equalTo("0"));
  }
}
