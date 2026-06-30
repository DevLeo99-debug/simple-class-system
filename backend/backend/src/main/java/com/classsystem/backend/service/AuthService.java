package com.classsystem.backend.service;

import com.classsystem.backend.dto.LoginDTO;
import com.classsystem.backend.dto.LoginRespDTO;
import com.classsystem.backend.dto.RegisterDTO;
import com.classsystem.backend.dto.UserInfoDTO;

public interface AuthService {
    LoginRespDTO login(LoginDTO dto);
    UserInfoDTO register(RegisterDTO dto);
    UserInfoDTO getUserInfo(Long userId);
}