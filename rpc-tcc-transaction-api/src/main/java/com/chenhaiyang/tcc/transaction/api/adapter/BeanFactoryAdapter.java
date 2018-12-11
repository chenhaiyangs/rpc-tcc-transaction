package com.chenhaiyang.tcc.transaction.api.adapter;

import com.chenhaiyang.tcc.transaction.api.BeanFactory;

/**
 * beanFactory适配器
 * @author chenhaiyang
 */
public class BeanFactoryAdapter {
    /**
     * BeanFactory
     */
    private static BeanFactory beanFactory;

    /**
     * getBean
     * @param targetClass 要获取的类
     * @return 返回结果
     */
    public static Object getBean(Class<?> targetClass) {
        return beanFactory.getBean(targetClass);
    }
    /**
     * 注入BeanFactory
     * @param beanFactory beanFactory
     */
    public static void setBeanFactory(BeanFactory beanFactory) {
        BeanFactoryAdapter.beanFactory = beanFactory;
    }
}
