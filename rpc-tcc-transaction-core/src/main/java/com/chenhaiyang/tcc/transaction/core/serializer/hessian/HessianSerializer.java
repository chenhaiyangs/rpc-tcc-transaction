package com.chenhaiyang.tcc.transaction.core.serializer.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.chenhaiyang.tcc.transaction.api.TransactionSerializer;
import com.chenhaiyang.tcc.transaction.api.exception.TccException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 使用hessian的序列化方式
 * @author chenhaiyang
 */
@SuppressWarnings("unchecked")
public class HessianSerializer implements TransactionSerializer{
    @Override
    public <T> byte[] serialize(T obj) {
        Hessian2Output hos;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            hos = new Hessian2Output(bos);
            hos.writeObject(obj);
            hos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new TccException("Hessian serialize error " + e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream bios;
        try {
            bios = new ByteArrayInputStream(bytes);
            Hessian2Input his = new Hessian2Input(bios);
            return (T)  his.readObject();
        } catch (IOException e) {
            throw new TccException("Hessian deSerialize error " + e);
        }
    }
}
