package com.smutsx.lbs.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.smutsx.lbs.common.Result;

/**
 * Controller 返回结果拦截处理
 * Created by bill on 2018/10/28.
 */
@ControllerAdvice
@ResponseBody
public class LBSAdvice implements ResponseBodyAdvice<Object>{
    private Logger logger = LoggerFactory.getLogger(LBSAdvice.class);
   
    /**
         * 全局异常捕捉处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result errorHandler(Exception e) {
        logger.error("【系统异常】：",e );
        Result result = new Result();
        result.addError(e.getMessage());
        return result;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        //默认不拦截
        return true;
    }

    /**
         * 返回结果拦截
     * @param returnValue
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    public Object beforeBodyWrite(@Nullable Object returnValue, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        return returnValue;
    }
}
