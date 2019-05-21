package com.yihuan.controller;

import com.alibaba.druid.util.StringUtils;
import com.yihuan.dao.GoodsDao;
import com.yihuan.domain.SeckillUser;
import com.yihuan.redis.GoodsKey;
import com.yihuan.redis.RedisService;
import com.yihuan.result.Result;
import com.yihuan.service.GoodsService;
import com.yihuan.service.SeckillUserService;
import com.yihuan.vo.GoodsDetailVo;
import com.yihuan.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private SeckillUserService seckillUserService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /*
        使用argumentResolver传入参数
        通过重写 webConfig 中的 addArgumentResolvers
        并且添加对应的参数解析器 UserArgumentResolver (implements )
        从而直接传入期望的参数 : user
        而不是从@CookieValue中获取参数

        使用thymeleafViewResolver页面缓存：
        produces,responseBody直接返回html源代码

        无缓存：1000*10 | 1016/sec
        页面缓存: 1000*10 | 2222/sec
     */
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String toList(Model model,
                         // @CookieValue(value = SeckillUserService.COOKIE_NAME_TOKEN,required = false) String cookieToken,
                         // @RequestParam(value = SeckillUserService.COOKIE_NAME_TOKEN,required = false) String paramToken
                         SeckillUser seckillUser,
                         HttpServletRequest request,
                         HttpServletResponse response
                         ){
        /*
              取缓存
              手动渲染模板
              结果输出
         */
         //取页面缓存
        String html = redisService.get(GoodsKey.getGoodsList,"", String.class);
        if(!StringUtils.isEmpty(html)){
            //System.out.println("[goods list]get from redis");

            return html;
        }

        model.addAttribute("user",seckillUser);
        //get good lists
        List<GoodsVo> list = goodsService.getGoodsVoList();
        model.addAttribute("goodsList",list);
        //return "goods_list";

        //手动渲染
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",webContext);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"",html);
            //System.out.println("[goods list]set to redis");
        }

        //结果输出
        return html;
    }

    /*
        使用thymeleafViewResolver进行url缓存：
        produces,responseBody直接返回html源代码
     */
    @RequestMapping(value = "/to_detail2/{goodsId}",produces = "text/html")
    @ResponseBody
    public String detail(Model model,SeckillUser seckillUser,
                         @PathVariable("goodsId") long goodsId,
                         HttpServletResponse response,
                         HttpServletRequest request){

        model.addAttribute("user",seckillUser);

        //取页面缓存
        String html = redisService.get(GoodsKey.getGoodsDetail,""+goodsId, String.class);
        if(!StringUtils.isEmpty(html)){
            //System.out.println("[goods detail]get from redis");
            return html;
        }

        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        model.addAttribute("goods",goodsVo);

        //get seckill Status and remain time
        int seckillStatus;  //0 : not start | 1 : killing now... | 2 : kill end
        long startTime = goodsVo.getStartTime().getTime();
        long endTime = goodsVo.getEndTime().getTime();
        long now = new Date().getTime();
        long remainTime;

        if(startTime > now){ //not start
            remainTime = (startTime - now)/1000;
            seckillStatus = 0;
        } else if(endTime < now){//already end
            remainTime = -1;
            seckillStatus = 2;
        } else {//killing...
            remainTime = 0;
            seckillStatus = 1;
        }

        model.addAttribute("seckillStatus",seckillStatus);
        model.addAttribute("remainTime", remainTime);

        //return "goods_detail";

        //手动渲染
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",webContext);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
            //System.out.println("[goods detail]set to redis");
        }

        //结果输出
        return html;
    }

    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail_static(Model model, SeckillUser seckillUser,
                                               @PathVariable("goodsId") long goodsId,
                                               HttpServletResponse response,
                                               HttpServletRequest request){

        model.addAttribute("user",seckillUser);


        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        model.addAttribute("goods",goodsVo);

        //get seckill Status and remain time
        int seckillStatus;  //0 : not start | 1 : killing now... | 2 : kill end
        long startTime = goodsVo.getStartTime().getTime();
        long endTime = goodsVo.getEndTime().getTime();
        long now = new Date().getTime();
        long remainTime;

        if(startTime > now){ //not start
            remainTime = (startTime - now)/1000;
            seckillStatus = 0;
        } else if(endTime < now){ //already end
            remainTime = -1;
            seckillStatus = 2;
        } else { //killing...
            remainTime = 0;
            seckillStatus = 1;
        }

        model.addAttribute("seckillStatus",seckillStatus);
        model.addAttribute("remainTime", remainTime);

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoodsVo(goodsVo);
        goodsDetailVo.setRemainSeconds(remainTime);
        goodsDetailVo.setSeckillStatus(seckillStatus);
        goodsDetailVo.setUser(seckillUser);

        return Result.success(goodsDetailVo);
    }
}
