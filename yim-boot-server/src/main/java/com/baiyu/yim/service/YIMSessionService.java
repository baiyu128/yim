package com.baiyu.yim.service;

import com.baiyu.yim.sdk.server.model.YIMSession;

import java.util.List;

/**
 * 集群 session管理实现示例， 各位可以自行实现 AbstractSessionManager接口来实现自己的 session管理 服务器集群时
 * 须要将YIMSession 信息存入数据库或者redis中 等 第三方存储空间中，便于所有服务器都可以访问
 * @author baiyu
 * @data 2019-12-30 16:55
 */
public interface YIMSessionService {

    void save(YIMSession session);

    YIMSession get(String account);

    List<YIMSession> list();

    void remove(String account);
}
