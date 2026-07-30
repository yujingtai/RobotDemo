package com.postal.robotdemo.controller;

import com.postal.robotdemo.common.Result;
import com.postal.robotdemo.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public Result<?> list() {
        return Result.ok(inventoryService.listAll());
    }

    @GetMapping("/{productId}")
    public Result<?> getByProduct(@PathVariable Long productId) {
        return Result.ok(inventoryService.getByProductId(productId));
    }

    @PostMapping("/{productId}/restock")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Result<Void> restock(@PathVariable Long productId, @RequestBody Map<String, Integer> body) {
        // 简化: 直接修改库存
        var inv = inventoryService.getByProductId(productId);
        if (inv != null) {
            int qty = body.getOrDefault("quantity", 0);
            inv.setTotalQuantity(inv.getTotalQuantity() + qty);
            inv.setAvailableQuantity(inv.getAvailableQuantity() + qty);
            // update via mapper
        }
        return Result.ok();
    }
}
