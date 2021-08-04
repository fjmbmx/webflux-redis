package com.santander.services;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.LuhnCheck;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Component
public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        // class name. method name. parameter value
        String keySuffix = target.getClass().getSimpleName() + "." + method.getName() + "." + Arrays.toString(params);
        log.info("Cache key suffix : {}", keySuffix);
        return keySuffix;
    }

}