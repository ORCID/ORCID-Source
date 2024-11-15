package org.orcid.core.web.filters;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * @author Robert Peters (rcpeters)
 * 
 */

public class CorsFilter extends OncePerRequestFilter {

	private static Log log = LogFactory.getLog(CorsFilter.class);

	@Resource
	CrossDomainWebManger crossDomainWebManger;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
			// CORS "pre-flight" request
			response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");

			boolean allowCrossDomain = false;

			try {
				allowCrossDomain = crossDomainWebManger.allowed(request);
			} catch (URISyntaxException e) {
				String origin  = request.getHeader("origin");
				String referer = request.getHeader("referer");
				log.error("Unable to process your request due an invalid URI exception, please check your origin and request headers: origin = '" + origin + "' referer = '" + referer + "'" , e);
				// Lets log the exception and assume cross domain call was rejected
			}

			if(allowCrossDomain) {
				response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Origin,Content-Type,Accept,x-csrf-token");
			} else {
				response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Origin,Content-Type, Accept");
			}
		}
		filterChain.doFilter(request, response);
	}

}