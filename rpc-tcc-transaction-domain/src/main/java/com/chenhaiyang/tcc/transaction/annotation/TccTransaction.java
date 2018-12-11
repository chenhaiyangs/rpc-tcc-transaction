package com.chenhaiyang.tcc.transaction.annotation;

import java.lang.annotation.*;

/**
 * tcc事务支持注解
 * 加在try方法上
 * @author chenhaiyang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface TccTransaction {
    /**
     * 确认函数名称
     * @return 返回函数名称
     */
    String confirmMethod();

    /**
     * 取消函数名称
     * @return 返回函数名称
     */
    String cancelMethod();

    /**
     * 主事务是否异步执行confirm和cancel方法（只有主事务可以异步，分支事务不能异步）
     * @return 是否异步，默认是false
     */
    boolean async() default false;
}
