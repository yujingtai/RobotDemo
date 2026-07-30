package com.postal.robotdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.postal.robotdemo.entity.TaskInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {
}
