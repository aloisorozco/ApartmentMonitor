package com.big_hackathon.backend_v2.repo;

import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Repository
public class RedisDAO {

    private JedisPool redisThreadPool;
    private String REDIS_IP = "127.0.0.1";
    private int REDIS_PORT = 6379;

    RedisDAO(){
        redisThreadPool = new JedisPool(new JedisPoolConfig(), REDIS_IP, REDIS_PORT);
    }

    public Jedis getJedisInstance(){
        return redisThreadPool.getResource();
    }
}
