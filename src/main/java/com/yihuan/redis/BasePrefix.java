package com.yihuan.redis;

public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;

    private String prefix;

    public BasePrefix(String prefix){
        this(0,prefix);
    }

    public BasePrefix(int expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }
    /*
        0 : never expire
     */
    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = this.getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
