package com.lkd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lkd.entity.VmStatusInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VmStatusInfoDao extends BaseMapper<VmStatusInfoEntity> {
    @Select("select inner_code from tb_vm_status_info order by utime desc")
    Page<String> getAllInnerCodeList(Page<String> page);
}
