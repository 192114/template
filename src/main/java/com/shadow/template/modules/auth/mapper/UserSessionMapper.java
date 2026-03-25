package com.shadow.template.modules.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shadow.template.modules.auth.entity.UserSessionEntity;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserSessionMapper extends BaseMapper<UserSessionEntity> {
  @Update(
      """
      UPDATE user_session
      SET revoked = 1, revoked_time = #{revokedTime}
      WHERE id = #{id} AND revoked = 0
      """)
  int revokeIfActive(@Param("id") Long id, @Param("revokedTime") LocalDateTime revokedTime);
}
