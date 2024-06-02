package com.cc.filter;

import com.alibaba.fastjson.JSON;
import com.cc.common.Result;
import com.cc.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登陆，登陆了才给访问
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    @Autowired
    private RedisTemplate redisTemplate;

    //调用Spring核心包的字符串匹配工具 AntPathMatcher 来匹配路径 用于判断是否可以放行
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //强转一下,向下转型
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        //获取url
        String requestUrl = httpServletRequest.getRequestURI();
        //定义可以放行的请求url
        String[] urls = {
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/user/sendMsg",
            "/user/login",
            "/doc.html",
            "/webjars/**",
            "/swagger-resources",
            "/v2/api-docs"
        };
        //判断请求的url是否可以放行
        boolean isMatch = checkUrl(urls, requestUrl);
        //不需要处理直接放行
        if (isMatch){
            log.info("匹配到了{}",requestUrl);
            //放行 交给下一个过滤器 或者是目标资源
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            //放行完了直接结束就行
            return;
        }

        //判断用户已经登陆可以放行（PC后台版） 通过redis判断是否登陆 通过redis的key来判断 有key就是登陆了
        if (redisTemplate.opsForValue().get("employee")!= null){
            log.info("后台用户已登录");
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            //获取当前新增操作人员的id
            Long empId= (Long) redisTemplate.opsForValue().get("employee");
            //存入LocalThread
            BaseContext.setCurrentId(empId);
            //放行完了直接结束就行
            return;
        }//判断用户已经登陆可以放行（移动端前台版）
        if (redisTemplate.opsForValue().get("user") != null){
            log.info("前台用户已登录");
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            //获取当前新增操作人员的id
            Long userId= (Long) redisTemplate.opsForValue().get("user");
            //存入LocalThread
            BaseContext.setCurrentId(userId);
            //放行完了直接结束就行
            return;
        }
        //没有登陆，跳转到登陆页面
        //前端有拦截器完成跳转页面，所以我们用输入流写个json来触发前端的拦截器完成跳转
        httpServletResponse.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        log.info("拦截到当前用户未登录，交由前端跳转至登录界面");
        return;
    }

    /**
     * @param urls 之前定义的可以放行的url地址数组
     * @param requestUrl 客户端打来的url地址
     * @return  返回值boolean值，true的话就是我们可以放行的目标
     */
    public boolean checkUrl(String []urls,String requestUrl){
        Boolean matchUrlResult = true;
        //遍历的同时调用PATH_MATCHER来对路径进行匹配
        for (String url : urls) {
            //匹配路径 返回值是boolean类型 true就是匹配到了
            boolean match=PATH_MATCHER.match(url, requestUrl);
            if (match){
                //匹配到了可以放行的路径，直接放行
                return true;
            }
        }
        //否则就是没有匹配到，不予放行
        return false;
    }

}
