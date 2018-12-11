package com.chenhaiyang.tcc.transaction.core.recover;

import com.chenhaiyang.tcc.transaction.api.RecoverConfig;
import lombok.Setter;

/**
 * 默认的事务恢复配置
 * @author chenhaiyang
 */
public class DefaultTransactionRecoverConfig implements RecoverConfig{
    /**
     * 最大重试次数
     */
    @Setter
    private int maxRetryCount=20;
    /**
     * 多久以前还存在的事务日志算是超时。默认120秒，即2分钟
     */
    @Setter
    private int recoverDuration=120;
    /**
     * 当重试次数超过五次，该事务下一次重试的间隔会越来越久。，默认60秒
     * 配置重试次数超过五次以后的指数退避算法。即每多重试一次，则下一次恢复事务要多等待（ nextRetriesAddTime*retriecount-5）次
     */
    @Setter
    private int nextRetriesAddTime=60;
    /**
     * 线程的执行cron表达式，默认一分钟执行一次。
     */
    @Setter
    private String cronExpression="0 */1 * * * ?";

    @Override
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    @Override
    public int getRecoverDuration() {
        return recoverDuration;
    }

    @Override
    public int getNextRetriesAddTime() {
        return nextRetriesAddTime;
    }

    @Override
    public String getCronExpression() {
        return cronExpression;
    }
}
