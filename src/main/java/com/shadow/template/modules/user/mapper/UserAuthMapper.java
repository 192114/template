package com.shadow.template.modules.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shadow.template.modules.user.entity.UserAuthEntity;

@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuthEntity> {
} 