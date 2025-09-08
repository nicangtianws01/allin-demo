package org.example.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@Component
public class AuthHandlerInterceptor implements HandlerInterceptor {
    @Resource
    private JwtService jwtService;

    @Value("${token.refreshTime}")
    private long refreshTime;

    @Value("${token.expiresTime}")
    private long expiresTime;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        Map<String, String> map = jwtService.parseToken(token);
        long timeOfUse = System.currentTimeMillis() - Long.parseLong(map.get("timeStamp"));
        if (timeOfUse < refreshTime) {
            return true;
        } else if (timeOfUse >= refreshTime && timeOfUse < expiresTime) {
            response.setHeader("token", jwtService.getToken(map.get("userId"), map.get("userRole")));
            return true;
        } else {
            throw new RuntimeException("Token is expired");
        }
    }
}
