package com.yihuan.domain;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class OrderInfo {
    private long id;
    private long userId;
    private long goodsId;
    private long deliverAddrId;
    private String goodsName;
    private int goodsCount;
    private double goodsPrice;
    private short orderChannel;
    private short status;
    private Date createTime;
    private Date payTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public long getDeliverAddrId() {
        return deliverAddrId;
    }

    public void setDeliverAddrId(long deliverAddrId) {
        this.deliverAddrId = deliverAddrId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(int goodsCount) {
        this.goodsCount = goodsCount;
    }

    public double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public short getOrderChannel() {
        return orderChannel;
    }

    public void setOrderChannel(short orderChannel) {
        this.orderChannel = orderChannel;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }
}
