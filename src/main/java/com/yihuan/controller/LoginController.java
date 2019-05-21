package com.yihuan.controller;

import com.yihuan.result.Result;
import com.yihuan.service.SeckillUserService;

import com.yihuan.vo.LoginVo;
import com.yihuan.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private SeckillUserService seckillUserService;


    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String toRegister(){
        return "register";
    }


    @PostMapping("/register")
    public String doRegister(@Valid RegisterVo registerVo){
        //validate parameter
        boolean ret = seckillUserService.register(registerVo);

        if(ret){
            System.out.println(registerVo.getMobile() + " "
                    + registerVo.getNickname() + " " + registerVo.getPassword());
            return "login";
        }
        else {
            return "register";
        }
    }


    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }


    /*
        使用jsr303参数校验的方式
        直接在service中抛出异常
     */
    @PostMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){

        //do login
        String token = seckillUserService.login(response, loginVo);

        return Result.success(token);
    }
}
