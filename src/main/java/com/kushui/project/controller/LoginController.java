package com.kushui.project.controller;


import com.kushui.project.common.BaseResponse;
import com.kushui.project.common.ResultUtils;
import com.kushui.project.service.MsmService;
import com.kushui.project.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import static com.kushui.project.common.ErrorCode.OPERATION_ERROR;

/**
 * 用于手机号登陆接口
 */
@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {
    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private MsmService msmService;


    @GetMapping("/captcha/{token}/{phone}")
    public BaseResponse<String> sendMsg(@PathVariable String token,@PathVariable String phone){

        //从redis获取验证码，如果获取获取到，返回ok
        // key 手机号  value 验证码
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) {
            return ResultUtils.success("验证码已经发送");
        }
        //如果从redis获取不到，
        // 生成验证码，
        code = RandomUtil.getSixBitRandom();
        //调用service方法，通过整合短信服务进行发送
        boolean isSend = msmService.send(phone,code);
        //生成验证码放到redis里面，设置有效时间
        if(isSend) {
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            System.out.println("短信验证码"+code);
            return ResultUtils.success("发送短信成功");
        } else {
            log.info("发送短信失败");
            return ResultUtils.error(OPERATION_ERROR,"发送短信失败");
        }


    }


}
