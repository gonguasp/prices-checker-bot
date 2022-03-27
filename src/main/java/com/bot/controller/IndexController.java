package com.bot.controller;

import com.bot.BotApplication;
import com.bot.config.Endpoints;
import com.bot.config.Urls;
import com.bot.service.IndexService;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@Data
public class IndexController {

    @NonNull
    private final Urls urls;

    @Autowired
    private IndexService indexService;

    @GetMapping("/urls")
    public Urls getAllUrls() {
        return urls;
    }

    @GetMapping("/endpoints")
    public Map<String, List<String>> getAllEndpoints() {
        return indexService.fillEndpoints();
    }
}
