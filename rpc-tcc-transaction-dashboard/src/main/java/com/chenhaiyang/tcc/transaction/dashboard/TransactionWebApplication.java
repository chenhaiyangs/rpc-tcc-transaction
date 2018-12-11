package com.chenhaiyang.tcc.transaction.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * web客户端启动接口
 * @author chenhaiyang
 */
@SpringBootApplication
@ImportResource(locations= {"classpath:transaction-datasource.xml"})
public class TransactionWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionWebApplication.class);
    }
}
