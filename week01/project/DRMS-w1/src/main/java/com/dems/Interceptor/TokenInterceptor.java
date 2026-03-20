package com.dems.Interceptor;


import com.dems.enums.UserRole;
import com.dems.utils.jwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        //1.获取请求路径
//        String path = request.getRequestURI();
//        //2.判断是否是登录请求，如果路径包含/login，说明是登录操作，放行
//        if (path.contains("/login")) {
//            log.info("登录操作");
//            return  true;
//        }
        //3.获取请求头中的token
        String token = request.getHeader("token");
        //4.判断token是否存在，若不存在说明用户没有登录，返回401
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        //5.如果令牌存在，校验令牌，如果校验失败，也返回401
        //只要无法解析就是非法令牌，直接处理错误就行
        try {
            jwtUtil.parseJwt(token);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        //6.获取访问路径,如果是学生专用就看角色是不是学生，是就放行，不是就不放行。管理员的亦然
        String path = request.getRequestURI();
        Integer role = jwtUtil.getUserRoleFromJwt(token);
        if(path.contains("/student")){
            if(!role.equals(UserRole.STUDENT.getCode() )){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }else if(path.contains("/admin")){
            if(!role.equals(UserRole.ADMIN.getCode())){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }
        return true;
    }
}
