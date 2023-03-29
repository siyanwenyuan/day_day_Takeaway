package com.chen.reggie.common;

/**
 * 基于threadLocal封装工具类，用于保存和获取当前用户的id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    /**
     * 设置id
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取id
     * @return
     */

    public static Long getCurrentId(){
        return threadLocal.get();

    }
}
