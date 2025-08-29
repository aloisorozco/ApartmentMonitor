package com.big_hackathon.backend_v2.filter;

import java.io.IOException;
import java.time.Instant;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.big_hackathon.backend_v2.repo.RedisDAO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

@Component
public class RateLimiter extends OncePerRequestFilter{

    private RedisDAO redisDAO;

    private final long USER_CACHE_TTL = 10000; // in ms
    private final int API_LIMIT = 5; // TODO: Change to more requests later on, for not 2 for testing.
    private final String REDIS_KEYSPACE = "rate_limiter";

    RateLimiter(RedisDAO redisDAO){
        this.redisDAO = redisDAO;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String usrIP = request.getRemoteAddr();

        if(isRateLimited(usrIP, Instant.now().toEpochMilli())){
            response.sendError(429, "wooooow, you are making way to many request bozo - wait a sec and make the request again.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // We are using a sorted set to store the timestamps -> the main reason is just so we can use the zremrangeByScore function in Redis to delete the range of itmes at once
    // this batch opp is more efficient that doing 'n' read requests to a Redis list to find the index of the element up to where we need to trim.
    // Using a Lua script is an option to keep in mind -> overkill for now.
    boolean isRateLimited(String userID, long timeOfRequest){

        boolean rateLimited = false;
        String userkeyspace = REDIS_KEYSPACE + ":" + userID;

        Jedis instance = redisDAO.getJedisInstance();
        // System.out.println("[LOG] Servicing request from " + userID);

        // Update user's key with a fresh TTL if exist.
        boolean userExistsAndUpdatedTTL = instance.pexpire(userkeyspace, USER_CACHE_TTL) == 1 ? true : false;
        
        if(userExistsAndUpdatedTTL){
            long cutoff = timeOfRequest - USER_CACHE_TTL;
            instance.zremrangeByScore(userkeyspace, 0, cutoff);
            
            long numOfRequests = instance.zcard(userkeyspace);
            
            if(numOfRequests >= API_LIMIT){
                rateLimited = true;
            }else{
                instance.zadd(userkeyspace, timeOfRequest, Long.toString(timeOfRequest));
                rateLimited = false;
            }

        }else{
            Pipeline cmdPipeline = instance.pipelined();
            cmdPipeline.zadd(userkeyspace, timeOfRequest,  Long.toString(timeOfRequest));
            cmdPipeline.pexpire(userkeyspace, USER_CACHE_TTL);
            rateLimited = false;

            // Sync the pipelined commands with Redis -> execute them all at once to avoid redundat RTTs (since we do not need the response of each cmd right away anyways).
            cmdPipeline.sync();
        }

        // return thread to the pool
        instance.close();
        return rateLimited;
    }
    
}
