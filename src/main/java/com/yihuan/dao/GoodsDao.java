package com.yihuan.dao;

import com.yihuan.domain.Goods;
import com.yihuan.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*, sg.seckill_price, sg.stock_count, sg.start_time, sg.end_time from goods g left join seckill_goods sg on g.id = sg.goods_id where g.id = #{id}")
    public GoodsVo getById(@Param("id") long id);

    @Select("select g.*, sg.seckill_price, sg.stock_count, sg.start_time, sg.end_time from seckill_goods sg left join goods g on sg.goods_id = g.id")
    public List<GoodsVo> getGoodsVoList();

    /*
        stock_count > 0 的条件是为了不产生超卖现象
     */
    @Update("update seckill_goods set stock_count = stock_count - 1 where goods_id = #{id} and stock_count > 0")
    public int reduceStock(GoodsVo goodsVo);
}
