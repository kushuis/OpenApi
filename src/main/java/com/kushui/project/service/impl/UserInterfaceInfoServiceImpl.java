package com.kushui.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kushui.project.common.ErrorCode;
import com.kushui.project.mapper.UserInterfaceInfoMapper;
import com.kushui.project.service.UserInterfaceInfoService;
import com.kushui.project.exception.BusinessException;
import com.kushui.kuapicommon.model.entity.UserInterfaceInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 *
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {


    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0");
        }
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public boolean invokeCount(long interfaceInfoId, long userId) {
        //校验用户id，接口id是否合理
        //校验用户的接口剩余调用次数是否充足
        //接口总调用次数+1，剩余调用次数-1
        //考虑计数的并发安全问题如何解决

        int maxRetries = 3; // 最大重试次数
        int retryCount = 0;

        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        while (retryCount < maxRetries) {
            //查询调用接口详情，包括剩余次数和调用版本号
            QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId);
            queryWrapper.eq("interfaceInfoId", interfaceInfoId);
            UserInterfaceInfo userInterfaceInfo = userInterfaceInfoMapper.selectOne(queryWrapper);
            Integer version = userInterfaceInfo.getVersion();

            Integer leftNum = userInterfaceInfo.getLeftNum();
            if (leftNum <= 0) {
                log.error("接口剩余调用次数不足");
                return false;
            }

            UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("userId", userId);
            updateWrapper.eq("interfaceInfoId", interfaceInfoId);
            updateWrapper.eq("version", version);
            updateWrapper.gt("leftNum", 0);
            updateWrapper.setSql("totalNum = totalNum +1,leftNum = leftNum-1,version = version+1");
            boolean updatedRows = this.update(updateWrapper);
            if (updatedRows ) {
                // 更新成功
                return true;
            } else {
                // 更新失败，可能是版本号冲突，进行重试
                retryCount++;
            }
        }
        return false;
    }

    @Override
    public boolean recoverInvokeCount(long userId, long interfaceInfoId) {
        if (userId<0 || interfaceInfoId<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户或接口不存在");
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId",userId);
        updateWrapper.eq("interfaceInfoId",interfaceInfoId);
        updateWrapper.gt("leftNum",0);
        updateWrapper.setSql("totalNum = totalNum -1,leftNum = leftNum+1,version = version+1");
        return this.update(updateWrapper);
    }

}




