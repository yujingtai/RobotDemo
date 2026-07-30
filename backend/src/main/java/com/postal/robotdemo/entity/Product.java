package com.postal.robotdemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String tags;
    private String imageUrl;
    private String displayPosition;
    private Integer robotGrabbable = 0; // 0-否 1-是
    private String status = "ON_SHELF"; // ON_SHELF / OFF_SHELF
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
