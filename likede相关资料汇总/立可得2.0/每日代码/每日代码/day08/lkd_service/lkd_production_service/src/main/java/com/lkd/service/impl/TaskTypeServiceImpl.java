package com.lkd.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.TaskTypeDao;
import com.lkd.entity.TaskTypeEntity;
import com.lkd.service.TaskTypeService;
import org.springframework.stereotype.Service;

@Service
public class TaskTypeServiceImpl extends ServiceImpl<TaskTypeDao,TaskTypeEntity> implements TaskTypeService{
}
