package com.bot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
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
        log.info(jp.getTarget().getClass().getName().replace(jp.getTarget().getClass().getPackageName() + ".", "") +
                " executing method " + jp.getSignature().getName() + " with " +
                (jp.getArgs().length == 0 ? "no arguments" : "arguments: " + Arrays.asList(jp.getArgs())));
    }

    @After("execution(* com.bot.eshop.*.service.*Service.*(..))")
    public void afterMethodsAmazonService(JoinPoint jp) {
        log.info("Finished method " + jp.getSignature().getName());
    }

    @Around("execution(* com.bot.service.IndexService.executeAll(..))")
    public void aroundExecuteAll(ProceedingJoinPoint pjp) throws Throwable {
        log.info("");
        log.info("EXECUTING ALL SCANS!");
        log.info("");
        pjp.proceed();
        log.info("");
        log.info("FINISHED ALL SCANS!");
        log.info("");
    }
}
