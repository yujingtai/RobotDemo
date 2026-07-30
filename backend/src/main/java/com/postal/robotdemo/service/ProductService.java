package com.postal.robotdemo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.postal.robotdemo.common.BizException;
import com.postal.robotdemo.entity.Inventory;
import com.postal.robotdemo.entity.Product;
import com.postal.robotdemo.mapper.InventoryMapper;
import com.postal.robotdemo.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;

    public Page<Product> page(int page, int size, String keyword, String status) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(Product::getName, keyword);
        }
        if (status != null && !status.isEmpty()) {
            qw.eq(Product::getStatus, status);
        }
        qw.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(new Page<>(page, size), qw);
    }

    @Transactional
    public Product create(Product product) {
        productMapper.insert(product);
        // 同时创建库存记录
        Inventory inv = new Inventory();
        inv.setProductId(product.getId());
        inv.setTotalQuantity(0);
        inv.setLockedQuantity(0);
        inv.setAvailableQuantity(0);
        inv.setLowThreshold(5);
        inventoryMapper.insert(inv);
        return product;
    }

    public Product update(Product product) {
        Product exist = productMapper.selectById(product.getId());
        if (exist == null) throw new BizException(404, "商品不存在");
        productMapper.updateById(product);
        return productMapper.selectById(product.getId());
    }

    public void onShelf(Long id) {
        Product p = productMapper.selectById(id);
        if (p == null) throw new BizException(404, "商品不存在");
        p.setStatus("ON_SHELF");
        productMapper.updateById(p);
    }

    public void offShelf(Long id) {
        Product p = productMapper.selectById(id);
        if (p == null) throw new BizException(404, "商品不存在");
        p.setStatus("OFF_SHELF");
        productMapper.updateById(p);
    }
}
