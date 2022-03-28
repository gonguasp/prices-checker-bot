package com.bot.service;

import com.bot.BotApplication;
import com.bot.config.Endpoints;
import com.bot.eshop.amazon.controller.AmazonController;
import com.bot.eshop.pccomponentes.controller.PcComponentesController;
import com.bot.event.EmailEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
@Slf4j
public class IndexService {

    @Autowired
    private PcComponentesController pcComponentesController;

    @Autowired
    private AmazonController amazonController;

    @Autowired
    private EmailEventPublisher emailEventPublisher;

    public Map<String, List<String>> fillEndpoints() {
        if(Endpoints.endpoints == null) {
            RequestMappingHandlerMapping requestMappingHandlerMapping = BotApplication.applicationContext
                    .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
            Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
                    .getHandlerMethods();
            Endpoints.endpoints = new HashMap<>();
            map.forEach((key, value) -> {
                List<String> endpoints = Endpoints.endpoints.get(value.getBean().toString());
                if (endpoints == null) {
                    endpoints = new ArrayList<>();
                }
                if (key.getMethodsCondition().getMethods().contains(RequestMethod.GET)) {
                    endpoints.add(WebMvcLinkBuilder.linkTo(value.getMethod()).withRel(key.getDirectPaths().iterator().next()).getHref());
                    Endpoints.endpoints.put(value.getBean().toString(), endpoints);
                    log.info("{} {}", key, value);
                }
            });
        }

        return Endpoints.endpoints;
    }

    @Scheduled(fixedDelay = 600000, initialDelay = 1000)
    public String executeAll() throws InvocationTargetException, IllegalAccessException, IOException {
        final String notExecuteMethod = "getSales";
        for (Method method : pcComponentesController.getClass().getDeclaredMethods()) {
            if(!notExecuteMethod.equals(method.getName())) {
                method.invoke(pcComponentesController);
            }
        }

        for (Method method : amazonController.getClass().getDeclaredMethods()) {
            if(!notExecuteMethod.equals(method.getName())) {
                method.invoke(amazonController);
            }
        }

        emailEventPublisher.publishCustomEvent("");
        return "DONE";
    }

}
