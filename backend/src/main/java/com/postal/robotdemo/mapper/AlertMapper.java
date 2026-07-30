package com.postal.robotdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.postal.robotdemo.entity.Alert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlertMapper extends BaseMapper<Alert> {
}
