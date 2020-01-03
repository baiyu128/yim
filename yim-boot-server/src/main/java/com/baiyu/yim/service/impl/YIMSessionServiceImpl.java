package com.baiyu.yim.service.impl;

import com.baiyu.yim.repository.SessionRepository;
import com.baiyu.yim.sdk.server.handler.YIMNioSocketAcceptor;
import com.baiyu.yim.sdk.server.model.YIMSession;
import com.baiyu.yim.service.YIMSessionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author baiyu
 * @data 2019-12-30 16:56
 */
@Service
public class YIMSessionServiceImpl implements YIMSessionService {

    @Resource
    private YIMNioSocketAcceptor nioSocketAcceptor;

    @Resource
    private SessionRepository sessionRepository;

    @Override
    public void save(YIMSession session) {
        sessionRepository.save(session);
    }

    @Override
    public YIMSession get(String account) {
        YIMSession session = sessionRepository.get(account);

        if (session != null){
            session.setSession(nioSocketAcceptor.getManagedSession(session.getNid()));
        }

        return session;
    }

    @Override
    public List<YIMSession> list() {
        return sessionRepository.findAll();
    }

    @Override
    public void remove(String account) {
        sessionRepository.remove(account);
    }
}
