package com.chenhaiyang.tcc.transaction.api;

/**
 * 事务恢复配置
 * @author chenhaiyang
 */
public interface RecoverConfig {

    /**
     * 获取最大重试次数
     * @return 最大重试次数
     */
    int getMaxRetryCount();

    /**
     * 获取需要执行事务恢复的持续时间。修复RecoverDuration毫秒前没有被更新和删除的事务
     * @return  持续间隔
     */
    int getRecoverDuration();

    /**
     * 获取下一次重试需要添加的时间
     * 指数退避算法：
     * 比如第一次重试2秒后，第二次重试则是4秒后，第三方重试则是8秒后
     * @return 每次重试都比下一次添加更长的间隔时间
     */
    int getNextRetriesAddTime();

    /**
     * 获取定时任务规则表达式;
     * @return cron表达式
     */
    String getCronExpression();
}
