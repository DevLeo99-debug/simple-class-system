package com.classsystem.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    // 密钥
    private static final String SECRET_STR = "classsystemsecretkey1234567890abcdefghijklmn";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STR.getBytes(StandardCharsets.UTF_8));
    // 过期时间 1天
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    // 生成token
    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 解析token获取用户id
    public static Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }

    // 校验token
    public static boolean verifyToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}