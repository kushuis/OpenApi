package com.kushui.project.filter;

import com.alibaba.fastjson.JSON;
import com.kushui.kuapicommon.util.JwtUtil;
import com.kushui.project.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import static com.kushui.project.common.ErrorCode.NOT_LOGIN_ERROR;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static AntPathMatcher pathMatcher = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI= request.getRequestURI();

        //设置不会拦截的请求,我们只想拦截经过controller的请求
        String[] urls = new String[]{"/api/user/login",
                                "/api/user/logout",
                            "/api/user/**",
                            "/api/interfaceInfo/**","/api/login/**","/api/**"};
        //判断一个请求是否要拦截
        boolean check = check(urls, requestURI);
        if (check){
            log.info("本次请求不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }



        log.info("拦截到请求: {}",request.getRequestURI());
        Long userId = this.getUserId(request);
        if(userId == null){
            log.info("用户未登录");
            response.getWriter().write(JSON.toJSONString(ResultUtils.error(NOT_LOGIN_ERROR)));
        }
        filterChain.doFilter(request,response);
    }

    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        if(!StringUtils.isEmpty(token)) {
            return JwtUtil.getUserId(token);
        }
        return null;
    }
    /**
     * 路径匹配，
     * 本次请求是否需要放行
     * 若请求路径在Strings中则放行
     */
    public boolean check(String[] urls,String requestURL){
        for (String url :urls) {
            boolean match = pathMatcher.match(url, requestURL);
            if (match){
                return true;
            }
        }
        return false;
    }
}
