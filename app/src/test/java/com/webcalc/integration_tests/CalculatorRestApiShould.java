package com.webcalc.integration_tests;

import com.webcalc.app.WebCalcApplication;
import io.restassured.filter.session.SessionFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(classes = WebCalcApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CalculatorRestApiShould {

  private static final String DEFAULT_USERNAME = "username1";
  private static final String DEFAULT_PASSWORD = "password1";

  @Test
  void returnResult() {
    assertEvalResult("1 2 +", "3");
  }

  @Test
  void setMaxFractionDigits() {
    SessionFilter session = new SessionFilter();
    setMaxFractionDigitsForSession(5, session);
    assertEvalResultForSession("8 7 /", "1,14286", session);
  }

  @Test
  void setMaxFractionDigitsOnlyForCurrentSession() {
    SessionFilter session1 = new SessionFilter();
    SessionFilter session2 = new SessionFilter();
    String expression = "8 7 /";

    setMaxFractionDigitsForSession(3, session1);
    setMaxFractionDigitsForSession(5, session2);

    assertEvalResultForSession(expression, "1,143", session1);
    assertEvalResultForSession(expression, "1,14286", session2);
  }

  @Test
  void doComplexCalculations() {
    assertEvalResult("1 2 3 + +", "6");
  }

  @Test
  void preventAnonymousUsersFromPerformingCalculations() {
    given()
        .body("1 2 +")
    .when()
        .post("/eval")
    .then()
        .statusCode(401);
  }

  @Test
  void canDefineAndExecuteCustomFunction() {
    given()
        .auth().preemptive().basic(DEFAULT_USERNAME, DEFAULT_PASSWORD)
        .body("times2 2 *")
    .when()
        .put("/define")
    .then()
        .statusCode(200);

    assertEvalResult("3 times2", "6");
  }

  private void setMaxFractionDigitsForSession(int maxFractionDigits, SessionFilter session) {
    given()
        .filter(session)
        .auth().preemptive().basic(DEFAULT_USERNAME, DEFAULT_PASSWORD)
        .body(maxFractionDigits)
    .when()
        .put("/maxFractionDigits")
    .then()
        .statusCode(200);
  }

  private void assertEvalResult(String expression, String expectedResult) {
    assertEvalResultForSession(expression, expectedResult, new SessionFilter());
  }

  private void assertEvalResultForSession(String expression, String expectedResult, SessionFilter session) {
    given()
        .filter(session)
        .auth().preemptive().basic(DEFAULT_USERNAME, DEFAULT_PASSWORD)
        .body(expression)
    .when()
        .post("/eval")
    .then()
        .body(equalTo(expectedResult));
  }
}
