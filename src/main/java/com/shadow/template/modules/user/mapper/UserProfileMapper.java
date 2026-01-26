package com.shadow.template.modules.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shadow.template.modules.user.entity.UserProfileEntity;

@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfileEntity> {}
