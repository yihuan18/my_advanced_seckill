package com.yihuan.vo;

import com.yihuan.domain.SeckillUser;

public class GoodsDetailVo {
    private int seckillStatus = 0;
    private long remainSeconds = 0;
    private SeckillUser user;
    private GoodsVo goodsVo;

    public SeckillUser getUser() {
        return user;
    }

    public void setUser(SeckillUser user) {
        this.user = user;
    }

    public int getSeckillStatus() {
        return seckillStatus;
    }

    public void setSeckillStatus(int seckillStatus) {
        this.seckillStatus = seckillStatus;
    }

    public long getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(long remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }
}
