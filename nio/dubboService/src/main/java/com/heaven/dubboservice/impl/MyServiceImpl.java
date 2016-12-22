package com.heaven.dubboservice.impl;

import com.heaven.dubboservice.iface.MyService;
import org.springframework.stereotype.Component;

@Component("MyServiceImpl")
public class MyServiceImpl implements MyService {

    /* (non-Javadoc)
     * @see yinwenjie.test.dubboService.iface.MyService#doMyTest(java.lang.String, java.lang.String)
     */
    @Override
    public String doMyTest(String field1, String field2) {

        return field1 + field2;
    }
}
