package com.kushui.kuapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import com.kushui.kuapiclientsdk.utils.SignUtils;


import java.util.HashMap;
import java.util.Map;

/**
 * 公共的API-SDK，抽取ak,sk并且封装生成签名的过程
 */
public class CommonApiClient {

    protected final String accessKey;
    protected final String secretKey;

    protected static final String GATEWAY_HOST ="http://localhost:8090";

    public CommonApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 负责将数字签名的相关参数填入请求头中
     * @param body
     * @return
     */
    protected  Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送
//        hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", SignUtils.genSign(body, secretKey));
        return hashMap;
    }


}
