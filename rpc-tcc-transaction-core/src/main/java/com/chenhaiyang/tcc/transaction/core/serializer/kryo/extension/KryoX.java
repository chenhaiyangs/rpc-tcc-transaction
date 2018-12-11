package com.chenhaiyang.tcc.transaction.core.serializer.kryo.extension;

import com.esotericsoftware.kryo.Kryo;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 解决kryo无法序列化无参构造的类的问题
 * @author chenhaiyang
 */
public class KryoX extends Kryo{
    private final ReflectionFactory REFLECTION_FACTORY = ReflectionFactory
            .getReflectionFactory();

    private final ConcurrentHashMap<Class<?>, Constructor<?>> constructors = new ConcurrentHashMap<>();

    @Override
    public <T> T newInstance(Class<T> type) {
        try {
            return super.newInstance(type);
        } catch (Exception e) {
            return newInstanceFromReflectionFactory(type);
        }
    }

    private Object newInstanceFrom(Constructor<?> constructor) {
        try {
            return constructor.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private  <T> T newInstanceFromReflectionFactory(Class<T> type) {
        Constructor<?> constructor = constructors.get(type);
        if (constructor == null) {
            constructor = newConstructorForSerialization(type);
            Constructor<?> saved = constructors.putIfAbsent(type, constructor);
            if(saved!=null) {
                constructor = saved;
            }
        }
        return (T) newInstanceFrom(constructor);
    }

    private <T> Constructor<?> newConstructorForSerialization(
            Class<T> type) {
        try {
            Constructor<?> constructor = REFLECTION_FACTORY
                    .newConstructorForSerialization(type,
                            Object.class.getDeclaredConstructor());
            constructor.setAccessible(true);
            return constructor;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
