package com.chenhaiyang.tcc.transaction.api;

/**
 * bean生成工厂
 * @author chenhaiyang
 */
public interface BeanFactory {
    /**
     * 根据className从不同的介质加载Bean实体。例如，SpringBean介质
     * @param targetClass 目标类
     * @return 返回执行结果
     */
    Object getBean(Class<?> targetClass);
}
