package com.chenhaiyang.tcc.transaction.spring.support;

import com.chenhaiyang.tcc.transaction.api.BeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * tcc加载实现类的BeanFactory
 * 这里实现的是通过Spring的application-context
 * @author chenhaiyang
 */
@Component
public class TccApplicationContextBeanFactory implements BeanFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getBean(Class<?> aClass) {
        return this.applicationContext.getBean(aClass);
    }
}
