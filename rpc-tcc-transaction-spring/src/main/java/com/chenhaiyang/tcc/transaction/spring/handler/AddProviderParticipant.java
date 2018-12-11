package com.chenhaiyang.tcc.transaction.spring.handler;

import com.chenhaiyang.tcc.transaction.api.TransactionConfiguration;
import com.chenhaiyang.tcc.transaction.spring.AddParticipanthandler;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * 添加根任务的消费者执行者
 * 目标该逻辑和添加根参与者的逻辑完全一致，因此复用添加根事务参与者的逻辑
 * @author chenhaiyang
 */
public class AddProviderParticipant implements AddParticipanthandler{

    private AddParticipanthandler addParticipanthandler;

    public AddProviderParticipant(TransactionConfiguration transactionConfiguration) {
        this.addParticipanthandler =new AddRootParticipant(transactionConfiguration);
    }

    @Override
    public void process(ProceedingJoinPoint pjp,Method method) {
        addParticipanthandler.process(pjp,method);
    }
}
