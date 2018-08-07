package com.htdata.crawl.core.service.impl;

import com.htdata.crawl.core.CoreApplicationTests;
import com.htdata.crawl.core.service.CommandExecuteService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class RunTimeCommandExecuteServiceImplTest extends CoreApplicationTests {
@Autowired
    private CommandExecuteService commandExecuteService;

    @Test
    public void processParamAndExecute() {
        System.out.println(commandExecuteService.processParamAndExecute(null));
    }
}