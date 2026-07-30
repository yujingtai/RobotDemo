package com.postal.robotdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.postal.robotdemo.common.Result;
import com.postal.robotdemo.entity.SysUser;
import com.postal.robotdemo.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final SysUserMapper userMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<SysUser>> list() {
        return Result.ok(userMapper.selectList(null));
    }
}
