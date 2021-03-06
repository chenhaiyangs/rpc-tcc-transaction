package com.chenhaiyang.tcc.transaction.spring.schedule;

import com.chenhaiyang.tcc.transaction.api.TransactionConfiguration;
import com.chenhaiyang.tcc.transaction.api.exception.TccException;
import com.chenhaiyang.tcc.transaction.core.task.TransactionRecoveryTask;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 事务恢复JOB
 * @author chenhaiyang
 */
@Component
public class TransactionRecoverScheduled {
    /**
     * 事务恢复任务
     */
    private TransactionRecoveryTask transactionRecoveryTask;

    /**
     * 事务恢复任务调度器
     */
    @Resource
    private Scheduler scheduler;

    /**
     * 注入的是TCC事务配置器.
     */
    private TransactionConfiguration transactionConfiguration;
    @Autowired
    public TransactionRecoverScheduled(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration = transactionConfiguration;
        this.transactionRecoveryTask = new TransactionRecoveryTask(transactionConfiguration);
    }

    /**
     * 初始化方法，Spring启动时执行.
     */
    @PostConstruct
    public void init() {

        try {
            // MethodInvokingJobDetailFactoryBean 负责生成具体的任务，只需要指定某个对象的某个方法，在触发器触发时，即调用指定对象的指定方法。
            MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
            // 指定该任务对应的调用对象，这个对象所属的类无需实现任何接口
            jobDetail.setTargetObject(transactionRecoveryTask);
            // 指定在targetObject对象中某个的方法(此处调用TransactionRecovery中的startRecover方法)
            jobDetail.setTargetMethod("start");
            // 设置任务名称
            jobDetail.setName("transactionRecoveryScheduled");
            // 是否允许任务并发执行，类默认是并发执行的，这时候如果不设置“aysnc”为false，很可能带来并发或者死锁的问题，而且几率较小，不容易复现,
            // 设置为false表示等上一个任务执行完后再开启新的任务
            jobDetail.setConcurrent(false);
            jobDetail.afterPropertiesSet();

            // 触发器生成器类，用被指定的调度器调度生成指定规则的触发器对象
            // 该类负责在spring容器中创建一个触发器，该类的ID应该在SchedulerFactoryBean属性的List中被引用，这样这个触发器才能保证被某个指定调度器调度
            CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
            // 设置触发器名称
            cronTrigger.setBeanName("transactionRecoveryCronTrigger");
            // 触发规则（这里通过事务配置器获取事务恢复定时任务规则）
            cronTrigger.setCronExpression(transactionConfiguration.getRecoverConfig().getCronExpression());
            cronTrigger.afterPropertiesSet();
            cronTrigger.setJobDetail(jobDetail.getObject());

            // 设置调度任务
            scheduler.scheduleJob(jobDetail.getObject(), cronTrigger.getObject());
            // 启动任务调度器
            scheduler.start();
        } catch (Exception e) {
            throw new TccException(e);
        }
    }
}
