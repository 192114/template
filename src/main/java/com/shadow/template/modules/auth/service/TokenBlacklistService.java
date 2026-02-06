package com.shadow.template.modules.auth.service;

public interface TokenBlacklistService {
  void addTokenToBlacklist(String token);
  boolean isTokenInBlacklist(String token);
}
