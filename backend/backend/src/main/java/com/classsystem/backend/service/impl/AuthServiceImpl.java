package com.classsystem.backend.service.impl;

import com.classsystem.backend.dto.LoginDTO;
import com.classsystem.backend.dto.LoginRespDTO;
import com.classsystem.backend.dto.RegisterDTO;
import com.classsystem.backend.dto.UserInfoDTO;
import com.classsystem.backend.entity.User;
import com.classsystem.backend.mapper.UserMapper;
import com.classsystem.backend.service.AuthService;
import com.classsystem.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginRespDTO login(LoginDTO dto) {
        User user = userMapper.findByUsernameAndRole(dto.getUsername(), dto.getRole());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        UserInfoDTO userInfo = new UserInfoDTO(
                user.getId(), user.getUsername(), user.getName(),
                user.getRole(), user.getEmail(), user.getPhone()
        );
        return new LoginRespDTO(token, userInfo);
    }

    @Override
    public UserInfoDTO register(RegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setRole(dto.getRole());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        userMapper.insert(user);
        return new UserInfoDTO(
                user.getId(), user.getUsername(), user.getName(),
                user.getRole(), user.getEmail(), user.getPhone()
        );
    }

    @Override
    public UserInfoDTO getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return new UserInfoDTO(
                user.getId(), user.getUsername(), user.getName(),
                user.getRole(), user.getEmail(), user.getPhone()
        );
    }
}