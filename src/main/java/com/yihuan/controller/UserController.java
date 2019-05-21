package com.yihuan.controller;

import com.yihuan.domain.SeckillUser;
import com.yihuan.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public Result<SeckillUser> info(Model model, SeckillUser user){
        return Result.success(user);

    }
}
