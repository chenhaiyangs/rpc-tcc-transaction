package com.chenhaiyang.tcc.transaction.spring.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 初始化SchedulerFactorBean
 * @author chenhaiyang
 */
@Configuration
public class AutoScheduler {

    @Bean
    public SchedulerFactoryBean getScheduler(){
        return new SchedulerFactoryBean();
    }
}
