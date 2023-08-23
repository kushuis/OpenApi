package com.kushui.kuapiinterface;

import com.kushui.kuapiclientsdk.client.KuApiClient;
import com.kushui.kuapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class YuapiInterfaceApplicationTests {

//    @Resource
//    private KuApiClient kuApiClient;

    @Test
    void contextLoads() {
        KuApiClient kuApiClient =  new KuApiClient("72ff6bf8f0bafef0a90833e9410ae45a","ed4e3acfb4e6359e80fbb57bff120379");
        User user = new User();
        user.setUsername("likushui");

        String result = kuApiClient.getUsernameByPost(user);

        System.out.println(result);

    }

}
