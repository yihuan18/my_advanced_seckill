package com.yihuan.rabbitmq;

import com.yihuan.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendSeckillMsg(SeckillMsg seckillMsg) {
        String msg = RedisService.beanToString(seckillMsg);
        //logger.info("send direct msg : " + msg);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE,msg);
    }



/*

    //Direct模式 交换机Exchange
    public void send(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send direct msg : " + msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
    }

    //Topic模式
    public void sendTopic(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send topic msg : " + msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");
    }

    //Fanout模式 （广播模式）
    public void sendFanout(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send fanout msg : " + msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg+"1");
    }

    //Header模式
    public void sendHeaders(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("send headers msg : " + msg);

        MessageProperties properties = new MessageProperties();
        properties.setHeader("header1","value1");
        properties.setHeader("header2","value2");
        Message obj = new Message(msg.getBytes(),properties);

        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
    }
*/
}
