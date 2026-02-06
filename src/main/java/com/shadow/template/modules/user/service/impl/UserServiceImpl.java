package com.shadow.template.modules.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;
import com.shadow.template.modules.user.dto.UserCreateCommand;
import com.shadow.template.modules.user.entity.UserAuthEntity;
import com.shadow.template.modules.user.entity.UserProfileEntity;
import com.shadow.template.modules.user.enums.UserStatusEnum;
import com.shadow.template.modules.user.mapper.UserAuthMapper;
import com.shadow.template.modules.user.mapper.UserProfileMapper;
import com.shadow.template.modules.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {
  @Autowired
  UserAuthMapper userAuthMapper;

  @Autowired
  UserProfileMapper userProfileMapper;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Override
  public boolean isExistByEmail(String email) {
    LambdaQueryWrapper<UserAuthEntity> lambdaQuery = new LambdaQueryWrapper<UserAuthEntity>();
    return this.userAuthMapper.exists(lambdaQuery.eq(UserAuthEntity::getEmail, email));
  }

  @Override
  public UserAuthEntity getUserByEmail(String email) {
    LambdaQueryWrapper<UserAuthEntity> lambdaQuery = new LambdaQueryWrapper<UserAuthEntity>();
    return this.userAuthMapper.selectOne(lambdaQuery.eq(UserAuthEntity::getEmail, email));
  }

  @Override
  public void createUser(UserCreateCommand user) {
    if (isExistByEmail(user.getEmail())) {
      throw new BizException(ResultCode.USER_EMAIL_EXISTS);
    }

    // 创建用户
    UserAuthEntity userAuthEntity = new UserAuthEntity();
    userAuthEntity.setEmail(user.getEmail());
    userAuthEntity.setPasswordHash(passwordEncoder.encode(user.getPassword()));
    userAuthEntity.setStatus(UserStatusEnum.ENABLE);
    this.userAuthMapper.insert(userAuthEntity);

    // 创建用户信息
    UserProfileEntity userProfileEntity = new UserProfileEntity();
    userProfileEntity.setUserId(userAuthEntity.getId());
    this.userProfileMapper.insert(userProfileEntity);
  }

  @Override
  public void disabledUser(Long userId) {
    UserAuthEntity userAuthEntity = new UserAuthEntity();
    userAuthEntity.setId(userId);
    userAuthEntity.setStatus(UserStatusEnum.DISABLED);
    this.userAuthMapper.updateById(userAuthEntity);
  }
}