package com.postal.robotdemo.controller;

import com.postal.robotdemo.common.Result;
import com.postal.robotdemo.entity.Product;
import com.postal.robotdemo.mapper.ProductMapper;
import com.postal.robotdemo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String status) {
        return Result.ok(productService.page(page, size, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<Product> get(@PathVariable Long id) {
        return Result.ok(productMapper.selectById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Result<Product> create(@RequestBody Product product) {
        return Result.ok(productService.create(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Result<Product> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return Result.ok(productService.update(product));
    }

    @PutMapping("/{id}/on-shelf")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Result<Void> onShelf(@PathVariable Long id) {
        productService.onShelf(id);
        return Result.ok();
    }

    @PutMapping("/{id}/off-shelf")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Result<Void> offShelf(@PathVariable Long id) {
        productService.offShelf(id);
        return Result.ok();
    }
}
