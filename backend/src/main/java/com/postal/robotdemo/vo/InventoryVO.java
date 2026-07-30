package com.postal.robotdemo.vo;

import lombok.Data;

@Data
public class InventoryVO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer totalQuantity;
    private Integer lockedQuantity;
    private Integer availableQuantity;
    private Integer lowThreshold;
    private Integer sampleMissing;
    private Integer sampleMisplaced;
}
