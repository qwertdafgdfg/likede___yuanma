package com.lkd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lkd.entity.UserEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
    @Results(id="userMap",value = {
            @Result(property = "id",column = "id",id = true),
            @Result(property = "roleId",column = "role_id"),
            @Result(property = "role",column = "role_id",one = @One(select = "com.lkd.dao.RoleDao.selectById"))
    })
    @Select("select * from tb_user where role_id=2")
    List<UserEntity> getAllOperater();
}
