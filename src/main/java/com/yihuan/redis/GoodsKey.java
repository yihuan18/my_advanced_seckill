package com.yihuan.redis;

public class GoodsKey extends BasePrefix {



    private GoodsKey(String prefix){
        super(prefix);
    }

    private GoodsKey(int expireTime,String prefix){
        super(expireTime,prefix);
    }

    public static final GoodsKey getGoodsList = new GoodsKey(60,"goodsList");
    public static final GoodsKey getGoodsDetail = new GoodsKey(60,"goodsDetail");
    public static final KeyPrefix getSeckillGoodsStock = new GoodsKey(0,"seckillGoodsStock");

}
