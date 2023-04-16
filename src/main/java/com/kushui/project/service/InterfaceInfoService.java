package com.kushui.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kushui.kuapicommon.model.entity.InterfaceInfo;

/**
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
