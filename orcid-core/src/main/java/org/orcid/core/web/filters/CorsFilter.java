package org.orcid.core.web.filters;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * @author Robert Peters (rcpeters)
 * 
 */

public class CorsFilter extends OncePerRequestFilter {

	@Resource
	CrossDomainWebManger crossDomainWebManger;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
			// CORS "pre-flight" request
			response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
			if(crossDomainWebManger.allowed(request)) {
	                    response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Origin,Content-Type,Accept,x-csrf-token");
	                }else{
	                    response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Origin,Content-Type, Accept");
	                }
		}
		filterChain.doFilter(request, response);
	}

}