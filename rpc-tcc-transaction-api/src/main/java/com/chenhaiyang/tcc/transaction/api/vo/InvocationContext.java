package com.chenhaiyang.tcc.transaction.api.vo;

import com.chenhaiyang.tcc.transaction.api.adapter.BeanFactoryAdapter;
import com.chenhaiyang.tcc.transaction.api.exception.TccException;
import lombok.Getter;

import java.io.*;
import java.lang.reflect.Method;

/**
 * 事务方法执行最小单元
 * 可能是一个confirm函数，也可能是一个cancel函数
 * @author chenhaiyang
 */
public class InvocationContext implements Serializable {
    /**
     * 目标类名
     */
    @Getter
    private String targetClassName;
    /**
     * confirm或者cancel函数的方法名
     */
    @Getter
    private String methodName;
    /**
     * 参数名列表
     */
    @Getter
    private String[] parameterTypesString;
    /**
     * 实际参数，try/confirm/cancel的参数必须一致
     * 由于控制面板查询复用了该类，反序列化会出问题,如：内存中无此类，因此，args使用默认的jdk序列化方式存储成byte[][]数组
     */
    @Getter
    private byte[][] args;

    public InvocationContext(String targetClassName, String methodName, String[] parameterTypesString, Object[] args) {
        this.targetClassName = targetClassName;
        this.methodName = methodName;
        this.parameterTypesString = parameterTypesString;
        this.args = convertArgsToByteArray(args);
    }

    /**
     * 执行提交或者回滚动作
     * @throws TccException tcc系统异常
     */
    void invoke() throws TccException{
        if (methodName!=null && methodName.trim().length()>0) {
            try {
                Class<?> targetClass = Class.forName(targetClassName);
                Object target = BeanFactoryAdapter.getBean(targetClass);

                if (target == null && !targetClass.isInterface()) {
                    target = targetClass.newInstance();
                }
                Class<?> tragetClass= targetClass;
                if(target!=null){
                    tragetClass =  target.getClass();
                }
                Class<?>[] parameterTypes= convert(parameterTypesString);
                Method method = tragetClass.getMethod(methodName,parameterTypes);
                method.invoke(target, convertByteArrayToArgs(args));
            } catch (Exception e) {
                throw new TccException(e);
            }
        }
    }
    /**
     * 参数列表转化为参数类
     * @param parameterTypesString 参数类型
     * @return 返回数组
     */
    private static Class<?>[] convert(String[] parameterTypesString) throws ClassNotFoundException {
        Class<?>[] clazzs = new Class[parameterTypesString.length];
        for(int i=0;i<parameterTypesString.length;i++){
            Class<?> clazz = Class.forName(parameterTypesString[i]);
            clazzs[i]=clazz;
        }
        return clazzs;
    }

    /**
     * 将对象参数类型转换为byte类型
     * @param args 参数
     * @return 返回结果
     */
    private byte[][] convertArgsToByteArray(Object[] args) {
        byte[][] parameters = new byte[args.length][];
        for(int i=0;i<args.length;i++){
            try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(); ObjectOutput objectOutput = new ObjectOutputStream(arrayOutputStream)) {
                objectOutput.writeObject(args[i]);
                objectOutput.flush();
                byte[] result = arrayOutputStream.toByteArray();
                parameters[i]=result;
            } catch (IOException e) {
                throw new TccException("java serialize error："+e);
            }
        }
        return parameters;
    }

    /**
     * 将byte[][]转成对象数组
     * @param args 参数
     * @return 返回结果
     */
    private Object[] convertByteArrayToArgs(byte[][] args) {
        Object[] parameters = new Object[args.length];
        for(int i=0;i<args.length;i++){
            try (ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(args[i]); ObjectInput input = new ObjectInputStream(arrayInputStream)) {
                Object result =input.readObject();
                parameters[i]=result;
            } catch (IOException | ClassNotFoundException e) {
                throw new TccException("java deSerialize error: "+e);
            }
        }
        return parameters;
    }
}
