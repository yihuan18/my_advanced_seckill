package com.yihuan.controller;

import com.yihuan.domain.OrderInfo;
import com.yihuan.domain.SeckillUser;
import com.yihuan.result.CodeMsg;
import com.yihuan.result.Result;
import com.yihuan.service.GoodsService;
import com.yihuan.service.OrderService;
import com.yihuan.vo.GoodsVo;
import com.yihuan.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;


    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> getOrderDetail(Model model, SeckillUser user,
                                                @RequestParam("orderId")long orderId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        OrderInfo order = orderService.getByOrderId(orderId);

        if(order == null)
            return Result.error(CodeMsg.ORDER_NOT_EXIST);

        long goodsId = order.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoods(goodsVo);
        orderDetailVo.setOrder(order);
        return Result.success(orderDetailVo);
    }


}
