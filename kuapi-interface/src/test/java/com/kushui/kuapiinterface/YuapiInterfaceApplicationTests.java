package com.kushui.kuapiinterface;

import com.kushui.kuapiclientsdk.client.KuApiClient;
import com.kushui.kuapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class YuapiInterfaceApplicationTests {

    @Resource
    private KuApiClient kuApiClient;

    @Test
    void contextLoads() {
        String result = kuApiClient.getNameByGet("kushui");
        User user = new User();
        user.setUsername("likushui");
        String usernameByPost = kuApiClient.getUsernameByPost(user);
        System.out.println(result);
        System.out.println(usernameByPost);
    }

}
