package com.jesper.seckill.redis;
public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}
