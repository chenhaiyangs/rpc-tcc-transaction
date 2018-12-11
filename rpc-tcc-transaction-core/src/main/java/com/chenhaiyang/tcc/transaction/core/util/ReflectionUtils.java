package com.chenhaiyang.tcc.transaction.core.util;

import java.lang.reflect.Method;

/**
 * 反射工具类
 * @author chenhaiyang
 */
public class ReflectionUtils {
    /**
     * 或者拥有指定方法的类
     * @param aClass 目标类
     * @param methodName 方法名
     * @param parameterTypes 参数类型
     * @return 返回class
     */
    public static Class getDeclaringType(Class<?> aClass, String methodName, Class<?>[] parameterTypes) {
        Class findClass = aClass;
        do {
            Class[] clazzes = findClass.getInterfaces();
            for (Class<?> clazz : clazzes) {
                Method method;
                try {
                    method = clazz.getDeclaredMethod(methodName,parameterTypes);
                } catch (NoSuchMethodException e) {
                    method = null;
                }
                if (method != null) {
                    return clazz;
                }
            }
            findClass = findClass.getSuperclass();
        } while (!findClass.equals(Object.class));
        return aClass;
    }

    /**
     * 获取指定参数在参数列表中的位置
     * @param parameterTypes 参数列表
     * @param findClass 要找寻的类型
     * @return 返回结果
     */
    public static int getParamPosition(Class[] parameterTypes, Class<?> findClass) {
        int i=-1;
        for (int j = 0; j < parameterTypes.length; j++) {
            if (parameterTypes[j].equals(findClass)) {
                i=j;
                break;
            }
        }
        return i;
    }

    /**
     * 获取默认的返回值。如果结果返回类型是基本类型，需要设置默认返回着
     * @param returnType returnType
     * @return object
     */
    public static Object getNullValue(Class<?> returnType) {
        if (boolean.class.equals(returnType)) {
            return false;
        } else if (byte.class.equals(returnType)) {
            return ' ';
        } else if (short.class.equals(returnType)) {
            return 0;
        } else if (int.class.equals(returnType)) {
            return 0;
        } else if (long.class.equals(returnType)) {
            return 0;
        } else if (float.class.equals(returnType)) {
            return 0;
        } else if (double.class.equals(returnType)) {
            return 0;
        }
        return null;
    }

    /**
     * 将参数class类型转换为String类型
     * @param parameterTypes 参数类型
     * @return 返回结果
     */
    public static String[] getParameterStringArgs(Class<?>[] parameterTypes) {
        String[] parameterClass = new String[parameterTypes.length];
        for(int i=0;i<parameterTypes.length;i++){
            String clazzName = parameterTypes[i].getName();
            parameterClass[i]=clazzName;
        }
        return parameterClass;
    }
}
