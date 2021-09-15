package com.smutsx.lbs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;

/**
 * Jackson帮助
 */
public class JacksonUtil{
    private static ObjectMapper objectMapper;

    public static void setObjectMapper(ObjectMapper mapper) throws BeansException {
        objectMapper = mapper;
    }

    public static ObjectMapper getObjectMapper() {
        if(objectMapper == null){
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }
}
