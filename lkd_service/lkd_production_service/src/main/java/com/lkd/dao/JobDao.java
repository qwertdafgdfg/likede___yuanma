package com.lkd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lkd.entity.JobEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobDao extends BaseMapper<JobEntity>{
}
