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
    private Integer sampleMissing = 0;
    private Integer sampleMisplaced = 0;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
