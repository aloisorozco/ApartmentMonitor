package com.big_hackathon.backend_v2.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.big_hackathon.backend_v2.repo.RedisDAO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimiter extends OncePerRequestFilter{

    private RedisDAO redisDAO;

    RateLimiter(RedisDAO redisDAO){
        this.redisDAO = redisDAO;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doFilterInternal'");
    }

    boolean isRateLimited(String userID){
        //TODO
        return true;
    }
    
}
