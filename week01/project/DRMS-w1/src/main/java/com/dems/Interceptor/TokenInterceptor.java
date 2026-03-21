package com.dems.Interceptor;


import com.dems.enums.UserRole;
import com.dems.utils.jwtUtil;
import com.dems.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
            System.out.println("用户未登录");
            return false;
        }

        //5.如果令牌存在，校验令牌，如果校验失败，也返回401
        //只要无法解析就是非法令牌，直接处理错误就行
        try {
            Claims claims = jwtUtil.parseJwt(token);
            //从令牌中获取claims并存入threadlocal以便之后的方法取用
            UserContext.setData(claims);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("非法令牌");
            return false;
        }
        //6.获取访问路径,如果是学生专用就看角色是不是学生，是就放行，不是就不放行。管理员的亦然
        String path = request.getRequestURI();
        Integer role = UserContext.getUserRole();
        if(path.contains("/student")){
            if(!role.equals(UserRole.STUDENT.getCode() )){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                System.out.println("用户不是学生");
                return false;
            }
        }else if(path.contains("/admin")){
            if(!role.equals(UserRole.ADMIN.getCode())){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                System.out.println("用户不是管理员");
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        //清除数据
        UserContext.remove();
    }
}
