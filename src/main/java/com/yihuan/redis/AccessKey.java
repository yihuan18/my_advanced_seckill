package com.yihuan.redis;

public class AccessKey extends BasePrefix {
    private AccessKey(int expireSeconds, String prefix){
        super(expireSeconds,prefix);
    }

    public static AccessKey getAccessKeyByExpireSeconds(int expireSeconds){
        return new AccessKey(expireSeconds,"access");
    }
}
