package com.kushui.kuapicommon.service;

import com.kushui.kuapicommon.model.entity.InterfaceInfo;

/**
 *
 */
//这里没有继承IService是保持这个接口的干净
public interface InnerInterfaceInfoService {

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
