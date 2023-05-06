package com.kushui.project.service;

import com.kushui.project.common.BaseResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface MsmService {
    //发送手机验证码
    boolean send(String phone, String code);

    BaseResponse<String> login(String phone, String code, HttpServletRequest request);
}
