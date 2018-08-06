package com.htdata.crawl.web.controller;

import com.htdata.crawl.core.entity.request.CrawlParamEntity;
import com.htdata.crawl.core.service.CommandExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/execute")
public class CommandExecuteController {
    @Autowired
    private CommandExecuteService commandExecuteService;

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public Object getCrawlInfo(@RequestBody CrawlParamEntity crawlParamEntity)
            throws IOException {
        return commandExecuteService.processParamAndExecute(crawlParamEntity);
    }

}
