package com.tencent.wxcloudrun.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.RemovalCause;

import java.util.concurrent.TimeUnit;

/**
 * @author yozora
 * description
 * @version 1.0
 * @date 2025/03/09 21:01
 */
public class CaffeineCacheUtil {

    private static final Cache<String, Object> cache;

    static {
        cache = Caffeine.newBuilder()
                // 最大缓存数量
                .maximumSize(10000)
                // 写入后60分钟过期
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> {
                    if (cause == RemovalCause.EXPIRED) {
                        System.out.println("缓存过期，key: " + key);
                    }
                })
                .build();
    }

    /**
     * 将数据放入缓存
     *
     * @param key   缓存key
     * @param value 缓存value
     */
    public static void put(String key, Object value) {
        cache.put(key, value);
    }

    /**
     * 从缓存中获取数据
     *
     * @param key 缓存key
     * @return 缓存value
     */
    public static Object get(String key) {
        return cache.getIfPresent(key);
    }

    /**
     * 从缓存中移除数据
     *
     * @param key 缓存key
     */
    public static void invalidate(String key) {
        cache.invalidate(key);
    }


    /**
     * description: 自定义过期时间的缓存
     *
     * @return com.github.benmanes.caffeine.cache.Cache<java.lang.String, java.lang.Object>
     * @author yozora
     * @date 21:03 2025/3/9
     **/
    public static Cache<String, Object> createCustomExpiryCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfter(new Expiry<String, Object>() {
                    @Override
                    public long expireAfterCreate(String key, Object value, long currentTime) {
                        // 根据需求设置过期时间，例如5分钟
                        return TimeUnit.MINUTES.toNanos(5);
                    }

                    @Override
                    public long expireAfterUpdate(String key, Object value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(String key, Object value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .build();
    }
}
