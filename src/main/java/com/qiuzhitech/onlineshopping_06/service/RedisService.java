package com.qiuzhitech.onlineshopping_06.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;

@Service
@Slf4j
public class RedisService {

    @Resource
    JedisPool jedisPool;

    public void setValue(String key, String value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value);
        jedisClient.close();
    }
    public String getValue(String key) {
        Jedis jedisClient = jedisPool.getResource();
        String value = jedisClient.get(key);
        jedisClient.close();
        return value;
    }

    public boolean tryToGetDistributedLock(String lockKey, String requestId, int expireTime) {
        Jedis jedis = jedisPool.getResource();
        String result = jedis.set(lockKey, requestId, "NX", "PX", expireTime);
        jedis.close();
        return "OK".equals(result);
    }

    public boolean releaseDistributedLock(String lockKey, String requestId) {
        Jedis jedis = jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1]" +
                " then return redis.call('del', KEYS[1])" +
                " else return 0 end";
        Long result = (Long)jedis.eval(script);
        jedis.close();
        return result == 1L;
    }

    public long stockDeduct(String redisKey) {
        try(Jedis jedisClient =jedisPool.getResource()) {
            String script =
                    "if redis.call('exists', KEYS[1]) == 1 then\n" +
                            "    local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                            "    if (stock<=0) then\n" +
                            "        return -1\n" +
                            "    end\n" +
                            "\n" +
                            "    redis.call('decr', KEYS[1]);\n" +
                            "    return stock - 1;\n" +
                            "end\n" +
                            "\n" +
                            "return -1;";
            Long stock = (Long) jedisClient.eval(script, Collections.singletonList(redisKey), Collections.emptyList());
            if (stock < 0) {
                log.info("There is no stock available");
                return -1;
            } else {
                log.info("Validate and decreased redis stock, current available stock：" + stock);
                return stock;
            }
        } catch (Throwable e) {
            log.error("Redis failed on stockDeduct");
            return -1;
        }
    }

    public long revertStock(String redisKey) {
        Jedis jedis = jedisPool.getResource();
        Long incr = jedis.incr(redisKey);
        jedis.close();
        return incr;
    }

    public void addToDenyList(String userId, String commodityId) {
        Jedis jedis = jedisPool.getResource();
        // key: denyList:123
        // values: set of {1,2}
        jedis.sadd("denyList:" + userId, commodityId);
        jedis.close();
        log.info("Add userId: {} into denyList for commodityId: {}", userId, commodityId);
    }

    public void removeFromDenyList(String userId, String commodityId) {
        Jedis jedis = jedisPool.getResource();
        // key: denyList:123
        // values: set of {1,2,3} ==> {2,3}
        jedis.srem("denyList:" + userId, commodityId);
        jedis.close();
        log.info("Remove userId: {} into denyList for commodityId: {}", userId, commodityId);
    }

    public boolean isInDenyList(String userId, String commodityId) {
        Jedis jedis = jedisPool.getResource();
        // key: denyList:123
        // values: set of {1,2,3} ==> {2,3}
        Boolean isInDenyList = jedis.sismember("denyList:" + userId, commodityId);
        jedis.close();
        log.info("userId: {} , commodityId {} is InDenyList result: {}", userId, commodityId,
                isInDenyList);
        return isInDenyList;
    }
}
