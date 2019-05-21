package com.yihuan.access;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.yihuan.domain.SeckillUser;
import com.yihuan.redis.AccessKey;
import com.yihuan.redis.RedisService;
import com.yihuan.result.CodeMsg;
import com.yihuan.result.Result;
import com.yihuan.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SeckillUserService seckillUserService;

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            SeckillUser user = getUser(request,response);
            UserContext.setUser(user);

            HandlerMethod handlerMethod = (HandlerMethod)handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String accessKey = request.getRequestURI();

            if(needLogin){
                if(user == null) {
                    render(response, CodeMsg.SERVER_ERROR);
                    return false;
                }
                accessKey += "_" + user.getId();
            }

            AccessKey prefix = AccessKey.getAccessKeyByExpireSeconds(seconds);
            //查询访问次数,接口限流
            Integer visitCount = redisService.get(prefix,accessKey,Integer.class);
            if(visitCount == null){
                redisService.set(prefix,accessKey,1);
            }else if(visitCount < maxCount){
                redisService.incr(prefix,accessKey);
            }else {
                render(response,CodeMsg.ACCESS_LIMIT_REACH);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream outputStream = response.getOutputStream();
        String string = JSON.toJSONString(Result.error(codeMsg));
        outputStream.write(string.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

    private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response){
        //根据token获取用户信息
        String paramToken = request.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);

        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }

        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        SeckillUser seckillUser = seckillUserService.getByToken(response,token);
        return seckillUser;
    }

    private String getCookieValue(HttpServletRequest request, String cookieName){

        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length == 0)
            return  null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName))
                return cookie.getValue();
        }
        return null;
    }
}
