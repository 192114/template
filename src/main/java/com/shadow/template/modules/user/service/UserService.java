package com.shadow.template.modules.user.service;

import com.shadow.template.modules.user.dto.UserCreateCommand;
import com.shadow.template.modules.user.entity.UserAuthEntity;

public interface UserService {
  boolean isExistByEmail(String email);

  UserAuthEntity getUserByEmail(String email);

  void createUser(UserCreateCommand user);

  void disabledUser(Long userId);
}
