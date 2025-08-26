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

@Component
public class RateLimiter extends OncePerRequestFilter{

    private RedisDAO redisDAO;

    private final long USER_CACHE_TTL = 60000; // in ms
    private final int API_LIMIT = 2; // TODO: Change to more requests later on, for not 2 for testing.
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

    // TODO: we can either use Streams or a SortedSet as the underlying datastructure in Redis to store the Ratelimiter timestamps -> both work but have pros and cons, we should see which one is best
    // Streams -> more memory heavy (store metadat about request), but efficient triming and append opps + Redis autogenerates time stamps (could be an issue if timestamp on Redis server is not the same as the requests).
    // SortedSet -> light on memeory, heavier IO since adding an item is log(n), trim is log(n) + M.
    boolean isRateLimited(String userID, long timeOfRequest){

        boolean rateLimited = false;
        String userkeyspace = REDIS_KEYSPACE + ":" + userID;

        Jedis instance = redisDAO.getJedisInstance();

        // Update user's key with a fresh TTL if exist.
        boolean userExistsAndUpdatedTTL = instance.pexpire(userkeyspace, USER_CACHE_TTL) == 1 ? true : false;
        
        if(userExistsAndUpdatedTTL){
            long cutoff = timeOfRequest - USER_CACHE_TTL;
            instance.zremrangeByScore(userkeyspace, 0, cutoff);
            long numOfRequests = instance.zcard(userkeyspace);
            
            if(numOfRequests >= API_LIMIT){
                rateLimited = true;
            }else{
                instance.zadd(userkeyspace, timeOfRequest, "");
                rateLimited = false;
            }

        }else{
            instance.zadd(userkeyspace, timeOfRequest, "");
            instance.pexpire(userkeyspace, USER_CACHE_TTL);
            rateLimited = false;
        }
        
        // return thread to the pool
        instance.close();
        return rateLimited;
    }
    
}
