package com.yihuan.service;

import com.alibaba.druid.util.StringUtils;
import com.yihuan.dao.SeckillUserDao;
import com.yihuan.domain.SeckillUser;
import com.yihuan.exception.GlobalException;
import com.yihuan.redis.RedisService;
import com.yihuan.redis.SeckillUserKey;
import com.yihuan.result.CodeMsg;
import com.yihuan.util.Md5Util;
import com.yihuan.util.UUIDUtil;
import com.yihuan.vo.LoginVo;
import com.yihuan.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    private SeckillUserDao seckillUserDao;

    @Autowired
    private RedisService redisService;

    /*
        对象缓存
     */
    public SeckillUser getById(long id){
        //读取缓存
        SeckillUser user = redisService.get(SeckillUserKey.getById, ""+id, SeckillUser.class);
        if(user != null)
            return user;

        //读取数据库
        user = seckillUserDao.getById(id);

        //写缓存
        if(user != null){
            redisService.set(SeckillUserKey.getById, ""+id, user);
        }
        return user;
    }

    public boolean updatePassword(String token, long id, String password){
        //取缓存
        SeckillUser user = getById(id);
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        //更新数据库
        SeckillUser toBeUpdate = new SeckillUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(password);
        seckillUserDao.update(toBeUpdate);

        user.setPassword(password);

        //更新缓存,与seckillUser相关的缓存都要更改
        redisService.del(SeckillUserKey.getById,""+id);
        redisService.set(SeckillUserKey.token,token,user);

        return true;
    }

    public SeckillUser getByMobile(long mobile){
        return seckillUserDao.getById(mobile);
    }

    public boolean register(RegisterVo registerVo){
        if(registerVo == null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);

        String pwdInput = registerVo.getPassword();
        String pwdForm = Md5Util.inputPwdToFormPwd(pwdInput);

        //get random salt and encode
        String randomSalt = "randomSalt";
        String pwdDB = Md5Util.FormPwdToDBPwd(pwdForm,randomSalt);

        int ret = seckillUserDao.insert(Long.valueOf(registerVo.getMobile()) ,
                registerVo.getNickname(), pwdDB, randomSalt);

        return ret >= 0;
    }

    public String login(HttpServletResponse response, LoginVo loginVo){
        if(loginVo  == null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);

        long mobile = Long.valueOf(loginVo.getMobile());
        String pwdForm = loginVo.getPassword();

        //if mobile exist
        SeckillUser user = getByMobile(mobile);
        if(user == null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);

        //if password correct
        String pwdDB  = Md5Util.FormPwdToDBPwd(pwdForm,user.getSalt());
        if(!pwdDB.equals(user.getPassword()))
            throw new GlobalException(CodeMsg.PWD_ERROR);

        String token = UUIDUtil.uuid();
        addCookie(response,user,token);

        return token;
    }

    public SeckillUser getByToken(HttpServletResponse response, String token){
        if(StringUtils.isEmpty(token))
            return null;

        SeckillUser user = redisService.get(SeckillUserKey.token,token,SeckillUser.class);

        //延长cookie的有效期
        if(user != null) {
            addCookie(response, user, token);
        }
        return user;
    }

    private void addCookie(HttpServletResponse response, SeckillUser user, String token){
        //generate cookie
        redisService.set(SeckillUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
