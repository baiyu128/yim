package com.baiyu.yim.repository;

import com.baiyu.yim.sdk.server.model.YIMSession;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * todo 正式场景下，使用redis或者数据库来存储session信息
 * @author baiyu
 * @data 2019-12-30 17:01
 */
@Repository
public class SessionRepository {

    private ConcurrentHashMap<String, YIMSession> map = new ConcurrentHashMap<>();


    public void save(YIMSession session){
        map.put(session.getAccount(),session);
    }

    public YIMSession get(String account){
        return map.get(account);
    }

    public void remove(String account){
        map.remove(account);
    }

    public List<YIMSession> findAll(){
        return new LinkedList<>(map.values());
    }
}
