package com.classsystem.backend.controller;

import com.classsystem.backend.dto.LoginDTO;
import com.classsystem.backend.dto.LoginRespDTO;
import com.classsystem.backend.dto.RegisterDTO;
import com.classsystem.backend.dto.UserInfoDTO;
import com.classsystem.backend.result.Result;
import com.classsystem.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginRespDTO> login(@RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @PostMapping("/register")
    public Result<UserInfoDTO> register(@RequestBody RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    @GetMapping("/me")
    public Result<UserInfoDTO> getMe(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(authService.getUserInfo(userId));
    }
}