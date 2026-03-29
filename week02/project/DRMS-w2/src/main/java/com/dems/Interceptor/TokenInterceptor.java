package com.dems.Interceptor;


import com.dems.utils.Constant;
import com.dems.utils.JwtUtil;
import com.dems.utils.RepairRecordUtil;
import com.dems.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate StringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 放行浏览器的预检请求 (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        //3.获取请求头中的token
        String token = request.getHeader("token");
        //4.判断token是否存在，若不存在说明用户没有登录，返回401
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.error("用户未登录");
            return false;
        }

        //5.如果令牌存在，校验令牌，如果校验失败，也返回401
        //只要无法解析就是非法令牌，直接处理错误就行
        try {
            Claims claims = JwtUtil.parseJwt(token);
            //从令牌中获取claims并存入threadlocal以便之后的方法取用（有用户的id，角色，用户名）
            UserContext.setData(claims);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.error("非法令牌");
            return false;
        }
        //通过token查redis里面有没有数据，没有的话就是登录过期
        String key = Constant.TOKEN_PREFIX + token;
        if(RepairRecordUtil.isEmpty(StringRedisTemplate.opsForValue().get(key))){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.error("登录过期");
            return false;
        }
        //刷新时间
        StringRedisTemplate.expire(key, Constant.TOKEN_EXPIRATION, TimeUnit.MINUTES);
        log.info("刷新Token");
        //6.获取访问路径,如果是学生专用就看角色是不是学生，是就放行，不是就不放行。管理员的亦然
        String path = request.getRequestURI();
        Integer role = UserContext.getUserRole();
        if(path.contains("/student")){
            if(!role.equals(Constant.STU_ROLE)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                System.out.println("用户不是学生");
                return false;
            }
        }else if(path.contains("/admin")){
            if(!role.equals(Constant.ADMIN_ROLE)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                System.out.println("用户不是管理员");
                return false;
            }
        }
        return true;
    }

    /**
     * 清除数据
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContext.remove();
    }
}
