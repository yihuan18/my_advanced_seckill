package com.yihuan.dao;

import com.yihuan.domain.SeckillUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SeckillUserDao {

    @Select("select * from seckill_user where id = #{id}")
    public SeckillUser getById(@Param("id") long id);

    @Insert("insert into seckill_user(id,nickname,password,salt) values(#{id},#{nickname},#{password},#{salt})")
    public int insert(@Param("id") long id, @Param("nickname") String nickname, @Param("password") String password, @Param("salt") String salt);

    @Update("update seckill_user set password = #{password} where id = #{id}")
    void update(SeckillUser toBeUpdate);
}
