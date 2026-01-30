package com.shadow.template.modules.auth.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shadow.template.modules.auth.entity.UserSessionEntity;

@Mapper
public interface UserSessionMapper extends BaseMapper<UserSessionEntity> {
}