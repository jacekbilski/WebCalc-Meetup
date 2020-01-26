package com.webcalc.user;

import java.util.UUID;

public class User {

  public final UUID id;
  private final String username;
  private final String password;

  public User(UUID id, String username, String password) {
    this.id = id;
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
