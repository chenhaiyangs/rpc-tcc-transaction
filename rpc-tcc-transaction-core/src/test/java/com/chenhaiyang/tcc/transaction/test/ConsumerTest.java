package com.chenhaiyang.tcc.transaction.test;

import com.chenhaiyang.tcc.transaction.test.vo.User;
import org.junit.Test;

import java.util.function.Consumer;

public class ConsumerTest {

    public void exec(Consumer<User> userConsumer){
        User t = new User(1,1,"张三丰");
        System.out.println(t);
        userConsumer.accept(t);
        System.out.println(t);
    }

    @Test
    public void test(){
        exec(user -> {
            user.setAge(12);
            user.setName("我被修改了");
        });
    }
}
