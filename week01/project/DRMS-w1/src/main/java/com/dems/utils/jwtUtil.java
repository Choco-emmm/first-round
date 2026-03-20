package com.dems.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class jwtUtil {
    public static final String JWT_SECRET = "dems-jwt-secret-key-for-hmac-sha256-algorithm";//密钥
    public static final long JWT_EXPIRATION = 3600*1000;//过期时间
    public static final Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

    public static String generateJwtToken(Map<String, Object> claims){
        ;
        return Jwts.builder()
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public static Claims parseJwt(String jwt){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * 从token中获取用户id（
     */
    public static String getUserIdFromJwt(String jwt){
        //在访问资源的时候已经验证令牌有效性了所以这里直接使用parseJwt而不是parserBuilder这种麻烦的东东
       return (String) parseJwt(jwt).get("userId");
    }

    /**
     * 从token中获取用户身份
     */
    public static Integer getUserRoleFromJwt(String jwt){
        //同上
        return (Integer) parseJwt(jwt).get("role");
    }


}
