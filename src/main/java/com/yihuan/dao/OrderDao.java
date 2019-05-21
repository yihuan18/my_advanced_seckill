package com.yihuan.dao;

import com.yihuan.domain.OrderInfo;
import com.yihuan.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from seckill_order where user_id = #{userId} and goods_id = #{goodsId}")
    SeckillOrder getOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    /*
        插入订单详情
        返回插入的订单id
     */
    @Insert("insert into order_info(user_id,goods_id,deliver_addr_id,goods_name,goods_count,goods_price,order_channel,status, create_time)" +
            "values(#{userId},#{goodsId},#{deliverAddrId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createTime})")
    @SelectKey(keyColumn = "id", keyProperty = "id",resultType = long.class, before = false, statement = "select last_insert_id() as id")
    long insertOrderInfo(OrderInfo orderInfo);

    @Insert("insert into seckill_order(user_id,order_id,goods_id) values(#{userId},#{orderId},#{goodsId})")
    int insertSeckillOrder(SeckillOrder seckillOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
}
