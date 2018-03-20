package org.orcid.core.web.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Adds jsonp callbacks Follow spring tutorial
 * http://jpgmr.wordpress.com/2010/07
 * /28/tutorial-implementing-a-servlet-filter-for
 * -jsonp-callback-with-springs-delegatingfilterproxy/
 * 
 * @author Robert Peters (rcpeters)
 * 
 */

public class JsonpCallbackFilter extends OncePerRequestFilter {

	private static Log log = LogFactory.getLog(JsonpCallbackFilter.class);

	@Resource
	CrossDomainWebManger crossDomainWebManger;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		Map<String, String[]> parms = httpRequest.getParameterMap();

		if (parms.containsKey("callback")) {
			HttpServletRequestWrapper requestWrapper = new AcceptHeaderRequestWrapper(httpRequest, "application/json");
			GenericResponseWrapper responseWrapper = new GenericResponseWrapper(httpResponse);
			filterChain.doFilter(requestWrapper, responseWrapper);
	                /*
	                 * "setContentType This method has no effect if it is called after getWriter has been called or after the response has been committed."
	                 * https://tomcat.apache.org/tomcat-5.5-doc/servletapi/javax/servlet/ServletResponse.html
	                 */
                        responseWrapper.setContentType("application/javascript;charset=UTF-8");
                        OutputStream out = httpResponse.getOutputStream();
                        out.write(new String(parms.get("callback")[0] + "(").getBytes());
			out.write(responseWrapper.getData());
			out.write(new String(");").getBytes());

			out.close();
		} else {
			filterChain.doFilter(request, response);
		}
	}

}