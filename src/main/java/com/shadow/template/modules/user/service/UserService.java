package com.shadow.template.modules.user.service;

import com.shadow.template.modules.user.dto.UserAuthDto;
import com.shadow.template.modules.user.entity.UserAuthEntity;

public interface UserService {
  boolean isExistByEmail(String email);

  UserAuthEntity getUserByEmail(String email);

  void createUser(UserAuthDto user);

  void disabledUser(Long userId);
}
