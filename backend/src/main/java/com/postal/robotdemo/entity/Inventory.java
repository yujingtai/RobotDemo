package com.postal.robotdemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inventory")
public class Inventory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Integer totalQuantity;
    private Integer lockedQuantity;
    private Integer availableQuantity;
    private Integer lowThreshold;
    private Integer sampleMissing;
    private Integer sampleMisplaced;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
