package com.yihuan.service;

import com.alibaba.druid.util.StringUtils;
import com.yihuan.domain.OrderInfo;
import com.yihuan.domain.SeckillOrder;
import com.yihuan.domain.SeckillUser;
import com.yihuan.redis.RedisService;
import com.yihuan.redis.SeckillKey;
import com.yihuan.util.Md5Util;
import com.yihuan.util.UUIDUtil;
import com.yihuan.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class SeckillService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisService redisService;

    /*
        执行秒杀：
        1 减库存
        2 生成订单并插入订单
        3 如果秒杀完毕则redis标示秒杀完毕

        tip : 通过在seckill_order数据表上加联合唯一主键防止重复秒杀
     */
    @Transactional
    public OrderInfo seckill(SeckillUser user, GoodsVo goodsVo){
        //减库存 下订单 写入秒杀订单
        boolean reduceSuccess = goodsService.reduceStock(goodsVo);
        if(reduceSuccess) {
            return orderService.createOrder(user, goodsVo);
        }
        setGoodsOver(goodsVo.getId());
        return null;
    }

    /*
        获取秒杀结果
        成功 ： orderId
        失败 ： -1
        排队中 ： 0
     */
    public long getSeckillResult(long userId, long goodsId) {

        SeckillOrder order = orderService.getOrderByUserIdGoodsId(userId,goodsId);

        if(order != null){
            return order.getOrderId();
        }
        boolean isOver = getGoodsOver(goodsId);
        if(isOver)
            return -1;
        else return 0;
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver,""+goodsId);
    }

    private void setGoodsOver(long goodsId) {
        redisService.set(SeckillKey.isGoodsOver,""+goodsId,true);
    }

    /*
        生成秒杀路径
     */
    public String createSeckillPath(long userId, long goodsId) {
        String path = Md5Util.md5(UUIDUtil.uuid()+"12345");
        redisService.set(SeckillKey.getPath, userId+"_"+goodsId,path);
        return path;
    }

    /*
        校验秒杀路径
     */
    public boolean checkPath(long userId, long goodsId, String path) {
        if(StringUtils.isEmpty(path))
            return false;

        String realPath = redisService.get(SeckillKey.getPath,userId+"_"+goodsId, String.class);
        return path.equals(realPath);
    }

    /*
        生成验证公式图片
     */
    public BufferedImage createVerifyCode(SeckillUser seckillUser, long goodsId) {
        if(seckillUser == null || goodsId <= 0){
            return null;
        }

        int width = 80;
        int height = 32;

        BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        //设置背景颜色
        graphics.setColor(new Color(0xDCDCDC));
        graphics.fillRect(0,0,width,height);
        //设置边框
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0,0,width-1,height-1);
        //设置图片干扰点
        Random random = new Random();
        for(int i = 0 ; i < 50 ; i++){
            int x = random.nextInt();
            int y = random.nextInt();
            graphics.drawOval(x,y,1,1);
        }
        //生成随机验证公式
        String verifyCode = generateVerifyCode(random);
        graphics.setColor(new Color(0,100,0));
        graphics.setFont(new Font("Candara",Font.BOLD,16));
        graphics.drawString(verifyCode,8,24);
        graphics.dispose();

        //把验证码计算结果放入redis中
        int rnd = calc(verifyCode);
        redisService.set(SeckillKey.getSeckillVerifyCode, seckillUser.getId()+"_"+goodsId, rnd);

        return bufferedImage;
    }

    //生成数学公式验证码
    private static char[] ops = new char[]{'+','-','*'};
    private String generateVerifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char op1 = ops[random.nextInt(3)];
        char op2 = ops[random.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    //计算验证公式
    private int calc(String verifyCode) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (int)engine.eval(verifyCode);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /*
        校验验证结果
     */
    public boolean checkVerifyCode(SeckillUser seckillUser, long goodsId, int verifyCode){
        if(seckillUser == null || goodsId <= 0){
            return false;
        }

        Integer realCode = redisService.get(SeckillKey.getSeckillVerifyCode,seckillUser.getId()+"_"+goodsId,Integer.class);
        if(realCode == null || realCode - verifyCode != 0){
            return false;
        }
        redisService.del(SeckillKey.getSeckillVerifyCode,seckillUser.getId()+"_"+goodsId);
        return true;
    }

}
