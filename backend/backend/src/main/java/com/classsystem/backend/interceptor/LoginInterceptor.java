package com.classsystem.backend.interceptor;

import com.classsystem.backend.result.Result;
import com.classsystem.backend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(MAPPER.writeValueAsString(Result.fail("未登录，请先登录")));
            return false;
        }
        if (!JwtUtil.verifyToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(MAPPER.writeValueAsString(Result.fail("token无效或已过期")));
            return false;
        }
        return true;
    }
}