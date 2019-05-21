package com.yihuan.redis;
/*
    定义了session中存放的用户信息
    在redis中存储为
    [SeckillUserKey + token , SeckillUser]
 */
public class SeckillUserKey extends BasePrefix{

    public static final int TOKEN_EXPIRE = 3600*24*2;

    private SeckillUserKey(int expireSeconds, String prefix){
        super(expireSeconds, prefix);
    }

    public static SeckillUserKey token = new SeckillUserKey(TOKEN_EXPIRE, "tk");
    public static SeckillUserKey getById = new SeckillUserKey(0,"id");
}
