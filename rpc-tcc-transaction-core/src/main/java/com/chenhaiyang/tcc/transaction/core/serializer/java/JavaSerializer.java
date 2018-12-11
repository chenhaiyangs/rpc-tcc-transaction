package com.chenhaiyang.tcc.transaction.core.serializer.java;

import com.chenhaiyang.tcc.transaction.api.TransactionSerializer;
import com.chenhaiyang.tcc.transaction.api.exception.TccException;

import java.io.*;

/**
 * 使用jdk的序列化方式
 * @author chenhaiyang
 */
@SuppressWarnings("unchecked")
public class JavaSerializer implements TransactionSerializer{
    @Override
    public <T> byte[] serialize(T obj) {
        try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(); ObjectOutput objectOutput = new ObjectOutputStream(arrayOutputStream)) {
            objectOutput.writeObject(obj);
            objectOutput.flush();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new TccException("java serialize error："+e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes,Class<T> clazz) {
        try (ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes); ObjectInput input = new ObjectInputStream(arrayInputStream)) {
            return (T) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new TccException("java deSerialize error: "+e);
        }
    }
}
