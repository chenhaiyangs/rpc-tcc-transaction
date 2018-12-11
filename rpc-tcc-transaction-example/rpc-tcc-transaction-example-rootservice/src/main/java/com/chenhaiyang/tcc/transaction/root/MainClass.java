package com.chenhaiyang.tcc.transaction.root;

import com.chenhaiyang.tcc.transaction.root.service.RootService;
import com.chenhaiyang.tcc.transaction.root.vo.RootRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.locks.LockSupport;

/**
 * dubbo root服务消费者启动脚本
 * @author chenhaiyang
 */
@Slf4j
public class MainClass {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"config-init.xml"});
        context.registerShutdownHook();
        context.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //在这里示例调用try方法，查看分布式事务的全部生命周期
        RootService rootService = (RootService) context.getBean("rootService");
        String result =rootService.rootTryMethod(new RootRequest("try msg"));
        System.out.println("rootdemo result :"+result);

        log.info("the rootservice started successfully!");
        LockSupport.park();
    }
}
