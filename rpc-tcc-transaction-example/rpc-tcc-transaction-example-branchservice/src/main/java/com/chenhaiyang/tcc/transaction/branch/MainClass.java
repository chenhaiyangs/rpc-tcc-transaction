package com.chenhaiyang.tcc.transaction.branch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.locks.LockSupport;

/**
 * 主程序启动类
 * @author chenhaiyang
 */
@Slf4j
public class MainClass {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext  context = new ClassPathXmlApplicationContext(new String[] {"config-init.xml"});
        context.registerShutdownHook();
        context.start();

        log.info("the service started successfully!");
        LockSupport.park();
    }
}
