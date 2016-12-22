package com.heaven.dubboclient;

import com.heaven.dubboservice.iface.MyService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ClientMainProcessor {
    //Log4j的日志基本配置（要使用这种方式首先引入log4j的支持）
    static {
        BasicConfigurator.configure();
    }

    /**
     * 日志
     */
    private static final Log LOGGER = LogFactory.getLog(ClientMainProcessor.class);

    /**
     * 锁定用
     */
    private static Object WAITOBJECT = new Object();

    public static void main(String[] args) throws Exception {
        ApplicationContext app = new ClassPathXmlApplicationContext(new String[]{"application-client.xml"});

        // 开始RPC调用
        MyService myService = (MyService)app.getBean("myService");
        LOGGER.info("myService = " + myService.doMyTest("1234", "abcde"));

        // 这里锁定这个应用程序，和DUBBO框架本身的工作原理没有任何关系，只是为了让其不退出
        synchronized (ClientMainProcessor.WAITOBJECT) {
            ClientMainProcessor.WAITOBJECT.wait();
        }
    }
}
