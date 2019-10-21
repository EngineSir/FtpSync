package com.fhzz.tool;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "sessionFilter", urlPatterns = { "/*" })
public class SessionFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String username = (String) req.getSession().getAttribute("username");
		String url = req.getRequestURI();
		//过滤静态文件
		if (url.indexOf("bootstrap") > 0 || url.indexOf("css") > 0 || url.indexOf("image") > 0 || url.indexOf("js") > 0 || url.indexOf("json-viewer") > 0 || url.indexOf("layui") > 0) { 
			chain.doFilter(request, response);
		}else if(url.indexOf("login") > 0) { // 过滤掉登陆地址和登陆页
			chain.doFilter(request, response);
		} else if (username != null && username.equals("admin")) {	//判断登录是否为admin
				chain.doFilter(request, response);
				return;
		}else {
			res.sendRedirect("/login");
		}

	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
