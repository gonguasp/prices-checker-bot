package com.bot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class EshopAspect {

    @Before("execution(* com.bot.eshop.*.service.*Service.*(..))")
    public void beforeMethodsAmazonService(JoinPoint jp) {
        log.info("Executing method " + jp.getSignature().getName() + " with " +
                (jp.getArgs().length == 0 ? "no arguments" : "arguments: " + Arrays.asList(jp.getArgs())));
    }

    @After("execution(* com.bot.eshop.*.service.*Service.*(..))")
    public void afterMethodsAmazonService(JoinPoint jp) {
        log.info("Finished method " + jp.getSignature().getName());
    }
}
