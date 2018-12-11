package com.chenhaiyang.tcc.transaction.spring.support;

import com.chenhaiyang.tcc.transaction.api.BeanFactory;
import com.chenhaiyang.tcc.transaction.api.adapter.BeanFactoryAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Sprting 加载Bean的后置处理器
 * @author chenhaiyang
 */
@Component
public class TccBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {
    /**
     * 将BeanFactory 设置到BeanFactoryAdapter中
     * @param contextRefreshedEvent 上下文刷新事件
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        if (applicationContext.getParent() == null) {
            BeanFactoryAdapter.setBeanFactory(applicationContext.getBean(BeanFactory.class));
        }
    }
}
