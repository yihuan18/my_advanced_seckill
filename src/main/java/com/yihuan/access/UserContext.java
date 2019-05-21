package com.yihuan.access;

import com.yihuan.domain.SeckillUser;

public class UserContext {

    private static ThreadLocal<SeckillUser> userHolder = new ThreadLocal<>();

    public static void setUser(SeckillUser seckillUser){
        userHolder.set(seckillUser);
    }

    public static SeckillUser getUser(){
        return userHolder.get();
    }
}
