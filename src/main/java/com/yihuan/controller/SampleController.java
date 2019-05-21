package com.yihuan.controller;
import com.yihuan.domain.User;
import com.yihuan.rabbitmq.MQReceiver;
import com.yihuan.rabbitmq.MQSender;
import com.yihuan.redis.UserKey;
import com.yihuan.result.Result;
import com.yihuan.redis.RedisService;
import com.yihuan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class SampleController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private MQReceiver mqReceiver;

//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> testMq(){
//        String hello = "helloYihuanMQ";
//        mqSender.send(hello);
//        return Result.success("hello yihuan mq");
//    }
//
//    @RequestMapping("/mq/topic")
//    @ResponseBody
//    public Result<String> testTopicMQ(){
//        String hello = "helloYihuanMQ";
//        mqSender.sendTopic(hello);
//        return Result.success("hello yihuan mq");
//    }
//
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public Result<String> testFanout(){
//        String hello = "helloYihuanMQ";
//        mqSender.sendFanout(hello);
//        return Result.success("hello yihuan mq");
//    }
//
//    @RequestMapping("/mq/headers")
//    @ResponseBody
//    public Result<String> testHeaders(){
//        String hello = "helloYihuanMQ";
//        mqSender.sendHeaders(hello);
//        return Result.success("hello yihuan mq");
//    }

    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }

    @RequestMapping("/mysql")
    @ResponseBody
    public Result<User> testMysql(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/tx")
    @ResponseBody
    public Result<Boolean> testTransaction() {
        Boolean bool = userService.testTx();
        return Result.success(bool);
    }

    @RequestMapping("/redis")
    @ResponseBody
    public Result<String> testRedis(){
        redisService.set(UserKey.getById,"1","hello,yihuan");
        String str = redisService.get(UserKey.getById,"1",String.class);
        return Result.success(str);
    }
}
