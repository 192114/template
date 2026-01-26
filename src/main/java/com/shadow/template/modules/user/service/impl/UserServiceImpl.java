package com.shadow.template.modules.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shadow.template.modules.user.dto.UserAuthDto;
import com.shadow.template.modules.user.entity.UserAuthEntity;
import com.shadow.template.modules.user.mapper.UserAuthMapper;
import com.shadow.template.modules.user.service.UserService;

public class UserServiceImpl implements UserService {
  @Autowired
  UserAuthMapper userAuthMapper;

  @Override
  public boolean isExistByEmail(String email) {
    LambdaQueryWrapper<UserAuthEntity> lambdaQuery = new LambdaQueryWrapper<UserAuthEntity>();
    return this.userAuthMapper.exists(lambdaQuery.eq(UserAuthEntity::getEmail, email));
  }

  @Override
  public UserAuthEntity getUserByEmail(String email) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getUserByEmail'");
  }

  @Override
  public void createUser(UserAuthDto user) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'createUser'");
  }

  @Override
  public void disabledUser(Long userId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'disabledUser'");
  }
}
