package com.webcalc.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WebCalcUserDetailsService implements UserDetailsService {

  private static final Map<String, User> users = new HashMap<>();

  static {
    users.put("username1", new User(UUID.randomUUID(), "username1", "{noop}password1"));
    users.put("username2", new User(UUID.randomUUID(), "username2", "{noop}password2"));
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (users.containsKey(username))
      return new SpringUserAdapter(users.get(username));
    throw new UsernameNotFoundException("User unknown");
  }
}
