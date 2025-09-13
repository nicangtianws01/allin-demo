package org.example.oauth2.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.oauth2.dto.Result;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.naming.NoPermissionException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private CacheManager cacheManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        if (!requestURI.startsWith("/resource")) {
            return true;
        }

        // 从缓存中获取token
        Cache cache = cacheManager.getCache("token");
        if (cache == null) {
            throw new NoPermissionException("Invalid token");
        }
        // 假设用户为admin
        String token = cache.get("auth:admin", String.class);
        if (token == null) {
            throw new NoPermissionException("Invalid token");
        }
        request.setAttribute("token", token);
        return true;
    }
}
