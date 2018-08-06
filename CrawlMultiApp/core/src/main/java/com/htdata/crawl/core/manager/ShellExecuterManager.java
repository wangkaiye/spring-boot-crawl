package com.htdata.crawl.core.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ShellExecuterManager {
    /**
     * 不等待执行结果，直接返回
     *
     * @param shellCommand 此命令需要nohup xxx &设置为后台运行
     * @return
     */
    public int executeShell(String shellCommand) {
        int success = 0;
        try {
            Runtime.getRuntime().exec(shellCommand);
            log.info("shellCommand:" + shellCommand);
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
        return success;
    }


}
