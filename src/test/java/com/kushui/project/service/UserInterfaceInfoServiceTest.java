package com.kushui.project.service;

import com.google.gson.Gson;
import com.kushui.kuapiclientsdk.client.KuApiClient;
import com.kushui.project.common.ErrorCode;
import com.kushui.project.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@SpringBootTest
public class UserInterfaceInfoServiceTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Test
    public void invokeCount() {
        boolean b = userInterfaceInfoService.invokeCount(1L, 1L);
        Assertions.assertTrue(b);
    }


    /**
     * 测试反射
     */
    @Test
    public void fanshe() {
        try {
            Class<?> clientClazz = Class.forName("com.kushui.kuapiclientsdk.client.KuApiClient");
            // 1. 获取构造器，参数为ak,sk
            Constructor<?> binApiClientConstructor = clientClazz.getConstructor(String.class, String.class);
            // 2. 构造出客户端
            Object apiClient =  binApiClientConstructor.newInstance("72ff6bf8f0bafef0a90833e9410ae45a", "ed4e3acfb4e6359e80fbb57bff120379");

            // 3. 找到要调用的方法
            Method[] methods = clientClazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals("getUsernameByPost")) {
                    // 3.1 获取参数类型列表
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 0) {
                        // 如果没有参数，直接调用
                        System.out.println(method.invoke(apiClient));
                    }
                    Gson gson = new Gson();
                    // 构造参数
                    Object parameter = gson.fromJson("{\"username\":\"kushui\"}", parameterTypes[0]);
                    System.out.println(method.invoke(apiClient, parameter));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "找不到调用的方法!! 请检查你的请求参数是否正确!");
        }
    }
}