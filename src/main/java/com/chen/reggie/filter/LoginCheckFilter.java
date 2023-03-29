package com.chen.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.chen.reggie.common.BaseContext;
import com.chen.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 创造一个过滤器
 * 检查用户是否已经完成登录
 *
 */

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")//配置一个过滤器需要加的注解，其中需要添加需要进行过滤的路径
public class LoginCheckFilter implements Filter {

    //路劲匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;//需要进行强转
        HttpServletResponse httpServletResponse=(HttpServletResponse)servletResponse;
        //获取本次请求的urI  注意是uri  不是url
        String requestURI = (httpServletRequest.getRequestURI());

        log.info("拦截到的请求：{}",requestURI);
        //定义不需要处理的请求路径
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"


        };
        //判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //如果不需要处理，则直接放行
        if(check){
            //放行代码
            log.info("本次请求不需要处理{}",requestURI);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        //如果需要处理，处理就是判断登录状态，如果已经登录，则放行
        //从session中获取
       if( httpServletRequest.getSession().getAttribute("employee")!=null){

           //判断如果session中得到了数据则直接放行
           log.info("用户已登录，用户id为：{}",httpServletRequest.getSession().getAttribute("employee"));
           // 为了得到id 为了设置工具类中的id
            Long empId=  (Long) httpServletRequest.getSession().getAttribute("employee");
           BaseContext.setCurrentId(empId);
           filterChain.doFilter(httpServletRequest,httpServletResponse);
           return;

       }

       //判断用户登录状态
        //判断移动端用户登录状态
        if( httpServletRequest.getSession().getAttribute("user")!=null){

            //判断如果session中得到了数据则直接放行
            log.info("用户已登录，用户id为：{}",httpServletRequest.getSession().getAttribute("user"));
            // 为了得到id 为了设置工具类中的id
            Long userId=  (Long) httpServletRequest.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;

        }
       //如果未登录，则返回未登录的结果,通过输出流方式向客户端响应结果
        log.info("用户为未登录");
       // return R.error("NOTLOGIN");
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return ;

    }

    /**
     * 路劲匹配，坚持本次请求是否需要放行
     * @param urls
     * @param requestURL
     * @return
     */
    public boolean check(String [] urls, String requestURL){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match( url,String.valueOf(requestURL));
            if(match){
                return true;
            }
        }
        return false;

        
    }
}
