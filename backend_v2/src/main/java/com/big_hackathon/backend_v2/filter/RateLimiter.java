package com.big_hackathon.backend_v2.filter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.big_hackathon.backend_v2.repo.RedisDAO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;

@Aspect
@Component
public class RateLimiter{

    private RedisDAO redisDAO;

    private final long USER_CACHE_TTL = 10000; // in ms
    private final int API_LIMIT = 5; // TODO: Change to more requests later on, for not 2 for testing.
    private final String REDIS_KEYSPACE = "rate_limiter";

    private final String LUA_SLIDING_WINDOW_SCRIPT;

    RateLimiter(RedisDAO redisDAO) throws IOException{
        this.redisDAO = redisDAO;

        InputStream is = getClass().getResourceAsStream("SlidingWindow.lua");
        if (is == null) {
            throw new FileNotFoundException("Lua script not found in classpath: SlidingWindow.lua");
        }
        LUA_SLIDING_WINDOW_SCRIPT = new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    // We are using a sorted set to store the timestamps -> the main reason is just so we can use the zremrangeByScore function in Redis to delete the range of itmes at once
    // this batch opp is more efficient that doing 'n' read requests to a Redis list to find the index of the element up to where we need to trim.
    // Using a Lua script is an option to keep in mind -> overkill for now.
    @SneakyThrows
    @Around("@annotation(RateLimited)")
    public Object rateLimitting(ProceedingJoinPoint jp){

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        String userID = request.getRemoteAddr();
        long timeOfRequest = Instant.now().toEpochMilli();

        Jedis instance = redisDAO.getJedisInstance();
        String userkeyspace = REDIS_KEYSPACE + ":" + userID;

        Object res = instance.eval(LUA_SLIDING_WINDOW_SCRIPT, Arrays.asList(userkeyspace), Arrays.asList(String.valueOf(timeOfRequest), String.valueOf(USER_CACHE_TTL), String.valueOf(API_LIMIT)));
        instance.close();

        boolean isLimited = (Long) res == 1 ? true : false;
        
        if(isLimited){
            return new ResponseEntity<>("wooooow, you are making way to many request bozo - wait a sec and make the request again", HttpStatus.TOO_MANY_REQUESTS);
        }

        // Will re-throw whatever error the intercepted method threw
        return jp.proceed();
    }
}
