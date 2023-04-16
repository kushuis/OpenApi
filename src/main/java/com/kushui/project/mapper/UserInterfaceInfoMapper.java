package com.kushui.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kushui.kuapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
 * @Entity com.kushui.project.model.entity.UserInterfaceInfo
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




