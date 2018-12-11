package com.chenhaiyang.tcc.transaction.test;

import com.chenhaiyang.tcc.transaction.api.TransactionSerializer;
import com.chenhaiyang.tcc.transaction.core.serializer.hessian.HessianSerializer;
import com.chenhaiyang.tcc.transaction.core.serializer.java.JavaSerializer;
import com.chenhaiyang.tcc.transaction.core.serializer.kryo.KryoSerializer;
import com.chenhaiyang.tcc.transaction.test.vo.User;
import org.junit.Test;

/**
 * 序列化测试类
 * @author chenhaiyang
 */
public class SeralizerTest {

    private User user = new User(1,2,"张三");

    @Test
    public void TestJDKSerializer(){

        TransactionSerializer serializer = new JavaSerializer();

        byte[] bytes =  serializer.serialize(user);
        System.out.println("jdk序列化结果："+new String(bytes));

        User user2 = serializer.deserialize(bytes,user.getClass());
        System.out.println("jdk反序列化结果："+user2);
    }

    @Test
    public void testHessian2Serializer(){
        TransactionSerializer serializer = new HessianSerializer();

        byte[] bytes =  serializer.serialize(user);
        System.out.println("hessian2序列化结果："+new String(bytes));

        User user2 = serializer.deserialize(bytes,user.getClass());
        System.out.println("hessian2反序列化结果："+user2);
    }

    @Test
    public void testKryoSerializer(){
        TransactionSerializer serializer = new KryoSerializer();

        byte[] bytes =  serializer.serialize(user);
        System.out.println("kryo序列化结果："+new String(bytes));

        User user2 = serializer.deserialize(bytes,user.getClass());
        System.out.println("kryo反序列化结果："+user2);
    }
}
