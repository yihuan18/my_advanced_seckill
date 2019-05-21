package com.yihuan.rabbitmq;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final String SECKILL_QUEUE = "seckill_queue";

    public static final String QUEUE = "queue";

    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String TOPIC_EXCHANGE = "topic.exchange";

    public static final String FANOUT_EXCHANGE = "fanout";

    public static final String HEADERS_QUEUE = "headers.queue";
    public static final String HEADERS_EXCHANGE = "headers.exchange";

    @Bean
    public Queue seckillQueue(){
        return new Queue(SECKILL_QUEUE,true);
    }

    /*
        Direct模式 交换机Exchange
     */
    @Bean
    public Queue queue(){
        return new Queue(QUEUE,true);
    }

    /*
        Topic模式
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1,true);
    }
    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2,true);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
    }

    /*
        Fanout模式 （广播模式）
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding fanoutBind1(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBind2(){
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    /*
        Header模式
     */
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(MQConfig.HEADERS_EXCHANGE);
    }
    @Bean
    public Queue headersQueue1(){
        return new Queue(MQConfig.HEADERS_QUEUE,true);
    }
    @Bean
    public Binding headerBinding(){
        Map<String,Object> map = new HashMap<>();
        map.put("header1","value1");
        map.put("header2","value2");
        //where all必须match map中所有的key和value
        return BindingBuilder.bind(headersQueue1()).to(headersExchange()).whereAll(map).match();
    }



}
