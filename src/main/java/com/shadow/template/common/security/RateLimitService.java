package com.shadow.template.common.security;

import com.shadow.template.common.exception.BizException;
import com.shadow.template.common.result.ResultCode;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RateLimitService {
  private final StringRedisTemplate stringRedisTemplate;

  public RateLimitService(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public void requireAllowed(String key, long limit, Duration window) {
    final String redisKey = "rate_limit:" + key;
    final Long count = stringRedisTemplate.opsForValue().increment(redisKey);
    if (count == null) {
      throw new BizException(ResultCode.SYSTEM_ERROR);
    }
    if (count == 1L) {
      stringRedisTemplate.expire(redisKey, window);
    }
    if (count > limit) {
      throw new BizException(ResultCode.TOO_MANY_REQUESTS);
    }
  }
}
