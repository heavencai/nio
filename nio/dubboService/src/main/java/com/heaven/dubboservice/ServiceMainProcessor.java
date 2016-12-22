package com.heaven.dubboservice;

import org.apache.log4j.BasicConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceMainProcessor {
    //Log4j的日志基本配置（要使用这种方式首先引入log4j的支持）
    static {
        BasicConfigurator.configure();
    }

    private static Object WAITOBJECT = new Object();

    public static void main(String[] args) throws Exception {
        new ClassPathXmlApplicationContext(new String[]{"application-service.xml"});

        /*
         * 这里锁定这个应用程序，和DUBBO框架本身的工作原理没有任何关系，只是为了让其不退出
         * 当然您也可以使用ClassPathXmlApplicationContext中的start方法，效果一样。
         *
         * 个人习惯
         * */
        synchronized (ServiceMainProcessor.WAITOBJECT) {
            ServiceMainProcessor.WAITOBJECT.wait();
        }
    }
}
