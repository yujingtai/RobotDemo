package com.postal.robotdemo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.postal.robotdemo.common.BizException;
import com.postal.robotdemo.dto.LoginReq;
import com.postal.robotdemo.entity.SysUser;
import com.postal.robotdemo.mapper.SysUserMapper;
import com.postal.robotdemo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Map<String, Object> login(LoginReq req) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, req.getUsername()));

        if (user == null || user.getStatus() == 0) {
            throw new BizException(401, "用户名或密码错误");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BizException(401, "用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("role", user.getRole().name());
        return result;
    }
}
