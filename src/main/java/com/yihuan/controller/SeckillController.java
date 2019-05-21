package com.yihuan.controller;

import com.yihuan.access.AccessLimit;
import com.yihuan.domain.SeckillOrder;
import com.yihuan.domain.SeckillUser;
import com.yihuan.rabbitmq.MQSender;
import com.yihuan.rabbitmq.SeckillMsg;
import com.yihuan.redis.AccessKey;
import com.yihuan.redis.GoodsKey;
import com.yihuan.redis.RedisService;
import com.yihuan.result.CodeMsg;
import com.yihuan.result.Result;
import com.yihuan.service.GoodsService;
import com.yihuan.service.OrderService;
import com.yihuan.service.SeckillService;
import com.yihuan.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender mqSender;

    private Map<Long,Boolean> localOverMap = new HashMap<>();

    /*
        系统初始化是调用，将商品缓存加载到redis中
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
        if(goodsVoList == null)
            return;

        for(GoodsVo goodsVo : goodsVoList){
            redisService.set(GoodsKey.getSeckillGoodsStock,""+goodsVo.getId(),goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(),false);
        }
    }

    /*
        获取秒杀路径
     */
    @AccessLimit(seconds=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(SeckillUser seckillUser,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam(value = "verifyCode",defaultValue = "0") int verifyCode){
        if(seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }



        //验证码校验
        boolean checkVerifyCode = seckillService.checkVerifyCode(seckillUser,goodsId,verifyCode);
        if(!checkVerifyCode){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //获取秒杀地址
        String path = seckillService.createSeckillPath(seckillUser.getId(),goodsId);

        return Result.success(path);
    }

    /*
        获取验证码
     */
    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillVerifyCode(SeckillUser seckillUser, Model model,
                                               @RequestParam("goodsId") long goodsId,
                                               HttpServletResponse response){
        if(seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        model.addAttribute("user",seckillUser);

        BufferedImage image = seckillService.createVerifyCode(seckillUser,goodsId);
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"JPEG",outputStream);
            outputStream.flush();
            outputStream.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

    /*
            mq前： 10000 : 522/sec
            mq后： 10000 ： 1182/sec

            redis优化：
            1 redis 预减库存
            2 redis 判断重复秒杀
            3 rabbitmq 加入消息队列
            tip : 使用本地标示拦截多余的请求
    */
    @RequestMapping(value = "/{path}/do_seckill",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSeckill(SeckillUser seckillUser, Model model,
                                     @RequestParam("goodsId") long goodsId,
                                     @PathVariable("path") String path){

        if(seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        model.addAttribute("user",seckillUser);

        //验证path
        boolean check = seckillService.checkPath(seckillUser.getId(), goodsId, path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //使用本地标记减少redis访问，拦截多余请求
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.STOCK_OVER);
        }

        //预减库存(redis)
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock,""+goodsId);
        if(stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.STOCK_OVER);
        }

        //判断是否重复秒杀(redis)
        SeckillOrder seckillOrder = orderService.getOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if(seckillOrder != null){
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }

        SeckillMsg seckillMsg = new SeckillMsg();
        seckillMsg.setUser(seckillUser);
        seckillMsg.setGoodsId(goodsId);
        mqSender.sendSeckillMsg(seckillMsg);
        return Result.success(0); //code 0 代表排队中


    /*
        //判断库存
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        int stock = goodsVo.getStockCount();
        if(stock <= 0){
            return Result.error(CodeMsg.STOCK_OVER);
        }

        //判断是否重复秒杀
        SeckillOrder seckillOrder = orderService.getOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if(seckillOrder != null){
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }

        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = seckillService.seckill(seckillUser,goodsVo);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsVo);
        return Result.success(orderInfo);
    */
    }

    /*
        成功 ： orderId
        失败 ： -1
        排队中 ： 0
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(SeckillUser seckillUser, Model model,
                                     @RequestParam("goodsId") long goodsId) {

        if (seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        model.addAttribute("user", seckillUser);

        long ret = seckillService.getSeckillResult(seckillUser.getId(),goodsId);
        return Result.success(ret);
    }
}
