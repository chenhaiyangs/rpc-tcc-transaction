package com.chenhaiyang.tcc.transaction.core.serializer.kryo;

import com.chenhaiyang.tcc.transaction.api.TransactionSerializer;
import com.chenhaiyang.tcc.transaction.api.exception.TccException;
import com.chenhaiyang.tcc.transaction.core.serializer.kryo.extension.KryoX;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * kryo序列化格式
 * 要求被序列化对象必须有空参构造函数
 * @author chenhaiyang
 */
public class KryoSerializer implements TransactionSerializer{

    @Override
    public <T> byte[] serialize(T obj) {
        byte[] bytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); Output output = new Output(outputStream)) {
            //获取kryo对象
            Kryo kryo = new KryoX();
            kryo.writeObject(output, obj);
            bytes = output.toBytes();
            output.flush();
        } catch (IOException e) {
            throw new TccException("kryo serialize error: " + e);
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes,Class<T> clazz) {
        T object;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            Kryo kryo = new KryoX();
            Input input = new Input(inputStream);
            object = kryo.readObject(input, clazz);
            input.close();
        } catch (IOException e) {
            throw new TccException("kryo deSerialize error: " + e.getMessage());
        }
        return object;
    }
}
