package com.yihuan.service;

import com.yihuan.dao.GoodsDao;
import com.yihuan.domain.Goods;
import com.yihuan.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    public List<GoodsVo> getGoodsVoList(){
        return goodsDao.getGoodsVoList();
    }

    public GoodsVo getGoodsVoById(long id){
        return goodsDao.getById(id);
    }

    public boolean reduceStock(GoodsVo goodsVo) {
        return goodsDao.reduceStock(goodsVo) > 0;
    }
}
