package com.yihuan.service;

import com.yihuan.dao.OrderDao;
import com.yihuan.domain.OrderInfo;
import com.yihuan.domain.SeckillOrder;
import com.yihuan.domain.SeckillUser;
import com.yihuan.redis.OrderKey;
import com.yihuan.redis.RedisService;
import com.yihuan.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisService redisService;

    /*
        优化 ：使用缓存
     */
    public SeckillOrder getOrderByUserIdGoodsId(long userId, long goodsId){
        //return orderDao.getOrderByUserIdGoodsId(userId,goodsId);
        return redisService.get(OrderKey.getSeckillOrderByUidGid,""+userId+"_"+goodsId,SeckillOrder.class);
    }

    public OrderInfo createOrder(SeckillUser user, GoodsVo goodsVo) {

        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setCreateTime(new Date());
        orderInfo.setDeliverAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getSeckillPrice());
        orderInfo.setOrderChannel((short) 1);
        orderInfo.setStatus((short) 0);
        orderInfo.setUserId(user.getId());

        orderDao.insertOrderInfo(orderInfo);

        SeckillOrder seckillOrder = new SeckillOrder();

        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());

        orderDao.insertSeckillOrder(seckillOrder);

        redisService.set(OrderKey.getSeckillOrderByUidGid,""+user.getId()+"_"+goodsVo.getId(),seckillOrder);

        return orderInfo;
    }

    public OrderInfo getByOrderId(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
