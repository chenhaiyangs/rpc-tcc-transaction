package com.chenhaiyang.tcc.transaction.api;

/**
 * 事务日志序列化
 * @author chenhaiyang
 */
public interface TransactionSerializer {
    /**
     * 序列化
     * @param t 参数
     * @return 序列化结果
     */
    <T> byte[] serialize(T t);

    /**
     * 反序列化
     * @param bytes bytes[]
     * @param clazz 要反序列化成的类
     * @return t
     */
    <T> T deserialize(byte[] bytes,Class<T> clazz);
}
