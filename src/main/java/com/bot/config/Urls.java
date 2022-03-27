package com.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "urls")
public class Urls {

    private Map<String, Object> pcComponentes;
    private List<String> amazon;
}
