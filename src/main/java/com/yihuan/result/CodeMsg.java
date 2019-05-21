package com.yihuan.result;


import com.sun.tools.javac.jvm.Code;

public class CodeMsg {

    private int code;
    private String msg;

    /*
        define some certain kind of error
     */

    //success
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");

    //generic error 500X
    public static CodeMsg SERVER_ERROR = new CodeMsg(5001,"服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(5002,"参数校验异常 : %s");
    public static CodeMsg SESSION_ERROR = new CodeMsg(5003,"session失效");
    public static  CodeMsg REQUEST_ILLEGAL = new CodeMsg(5004,"验证码错误");
    public static  CodeMsg ACCESS_LIMIT_REACH = new CodeMsg(5005,"访问太频繁");

    //login error 400X
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(4001,"手机号不能为空");
    public static CodeMsg PASS_EMPTY = new CodeMsg(4002,"密码不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(4003,"手机号不合法");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(4004,"手机号不存在");
    public static CodeMsg PWD_ERROR = new CodeMsg(4005,"密码错误");

    //sec-kill error 600X
    public static CodeMsg STOCK_OVER = new CodeMsg(6001,"商品秒杀完毕");
    public static CodeMsg REPEATE_SECKILL = new CodeMsg(6002,"不能重复秒杀");
    public static  CodeMsg ORDER_NOT_EXIST = new CodeMsg(6003,"订单不存在");
    /*
        Constructor
     */
    private CodeMsg(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    /*
        getters
     */
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg,args);
        return new CodeMsg(code,message);
    }
}
