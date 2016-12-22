package com.heaven.test.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * （代理）调用处理器。<br>
 * 什么意思呢：当“代理者”被调用时，这个实现类中的invoke方法将被触发。<br>
 * “代理者”对象，外部模块/外部系统所调用的方法名、方法中的传参信息都将以invoke方法实参的形式传递到方法中。
 *
 * @author yinwenjie
 */
public class BusinessInvocationHandler implements InvocationHandler {

    /**
     * 真实的业务处理对象
     */
    private BusinessInterface realBusiness;

    public BusinessInvocationHandler(BusinessInterface realBusiness) {
        this.realBusiness = realBusiness;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("（代理）调用处理器被激活=====");
        System.out.println("“代理者对象”：" + proxy.getClass().getName());
        System.out.println("“外部模块/外部系统”调用的方法名：" + method.getName());

        System.out.println("---------正式业务执行前；");
        Object resultObject = method.invoke(this.realBusiness, args);
        System.out.println("---------正式业务执行后；");

        return resultObject;
    }

}