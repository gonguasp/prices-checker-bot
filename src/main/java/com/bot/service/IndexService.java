package com.bot.service;

import com.bot.BotApplication;
import com.bot.config.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IndexService {

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
}
