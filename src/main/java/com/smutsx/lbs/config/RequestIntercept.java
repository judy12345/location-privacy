package com.smutsx.lbs.config;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.smutsx.lbs.common.Result;


/**
 * Request请求拦截
 * @author bill
 *
 */
public class RequestIntercept implements HandlerInterceptor {
	
	/**
	 *  设置返回结果
	 * @param response
	 * @param result
	 * @return
	 * @throws IOException
	 */
	private boolean setResponse(HttpServletResponse response, Result result) throws IOException {
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html; charset=utf-8");
	    OutputStream output = null;
	    try {
	        output = response.getOutputStream();
	        output.write(JacksonUtil.getObjectMapper().writeValueAsString(result).getBytes("UTF-8"));
	    } catch (IOException e) {
	        throw new RuntimeException(e.getMessage(), e);
	    } finally {
	        if (output != null){
	            output.close();
	        }
	        return false;
	    }
	}

	 /**
	  * 请求拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	 //请求路径
        String requestURI = request.getRequestURI();
        
    	return true;
    }
    
    

}
