package com.yihuan.rabbitmq;

import com.yihuan.domain.OrderInfo;
import com.yihuan.domain.SeckillOrder;
import com.yihuan.domain.SeckillUser;
import com.yihuan.redis.RedisService;
import com.yihuan.service.GoodsService;
import com.yihuan.service.OrderService;
import com.yihuan.service.SeckillService;
import com.yihuan.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisService redisService;

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    /*
        处理消息队列中的秒杀请求：
        1 判断真实库存是否大于0
        2 判断重复秒杀
        3 执行秒杀
     */
    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message){
        //logger.info("receive direct msg : " + message);
        SeckillMsg seckillMsg = RedisService.stringToBean(message,SeckillMsg.class);

        long goodsId = seckillMsg.getGoodsId();
        SeckillUser seckillUser = seckillMsg.getUser();

        //判断库存
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        int stock = goodsVo.getStockCount();
        if(stock <= 0){
            return;
        }
        //判断重复秒杀
        SeckillOrder seckillOrder = orderService.getOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if(seckillOrder != null){
            return;
        }

        //下订单
        OrderInfo orderInfo = seckillService.seckill(seckillUser,goodsVo);
    }

//    /*
//        Direct模式 交换机Exchange
//     */
//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message){
//        logger.info("receive direct msg : " + message);
//    }
//
//    /*
//        Topic模式
//        Fanout模式 （广播模式）
//     */
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String msg){
//        logger.info("topic queue1 msg : " + msg);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String msg){
//        logger.info("topic queue2 msg : " + msg);
//    }
//
//    /*
//        Headers模式
//     */
//    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
//    public void receiveHeadersQueue(byte[] msg){
//        logger.info("header queue msg : " + new String(msg));
//    }
}
