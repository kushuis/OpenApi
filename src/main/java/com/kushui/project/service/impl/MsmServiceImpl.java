package com.kushui.project.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kushui.kuapicommon.model.entity.User;
import com.kushui.kuapicommon.util.JwtUtil;
import com.kushui.project.common.BaseResponse;
import com.kushui.project.common.ErrorCode;
import com.kushui.project.common.ResultUtils;
import com.kushui.project.exception.BusinessException;
import com.kushui.project.mapper.UserMapper;
import com.kushui.project.service.MsmService;
import com.kushui.project.service.UserService;
import com.kushui.project.util.ConstantPropertiesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.kushui.project.common.ErrorCode.PARAMS_ERROR;
import static com.kushui.project.constant.UserConstant.USER_LOGIN_STATE;

@Service
public class MsmServiceImpl implements MsmService {

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private UserService userService;

    @Override
    public boolean send(String phone, String code) {
        //判断手机号是否为空
        if(StringUtils.isEmpty(phone)) {
            return false;
        }
        //整合阿里云短信服务
        //设置相关参数
        DefaultProfile profile = DefaultProfile.
                getProfile(ConstantPropertiesUtils.REGION_Id,
                        ConstantPropertiesUtils.ACCESS_KEY_ID,
                        ConstantPropertiesUtils.SECRECT);
        IAcsClient client = new DefaultAcsClient(profile);
        //以上是在项目中获取阿里云的接口
        //以下是设置消息内容
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        //手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //签名名称
        request.putQueryParameter("SignName", "枯水个人博客网站");
        //模板code
        request.putQueryParameter("TemplateCode", "SMS_268625240");
        //验证码  使用json格式   {"code":"123456"}
        Map<String,Object> param = new HashMap();
        param.put("code",code);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));

        //调用方法进行短信发送
        try {
            //通过阿里云接口传输消息
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    //手机号登陆
    @Override
    public BaseResponse<String> login(String phone, String code, HttpServletRequest request) {
        //校验参数
        if(StringUtils.isEmpty(phone) ||
                StringUtils.isEmpty(code)) {
            return ResultUtils.error(PARAMS_ERROR,"登陆失败");
        }

        //校验校验验证码
        String mobleCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(mobleCode)) {
          return   ResultUtils.error(PARAMS_ERROR,"登陆失败");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = userService.getOne(queryWrapper);
        if(user != null){
            String token = JwtUtil.createToken(user.getId(), user.getUserName());
            //记录用户的登录态
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            //登陆成功返回token
            return ResultUtils.success(token);
        }
        return ResultUtils.error(PARAMS_ERROR,"登陆失败");
    }

}

