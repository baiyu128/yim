package com.baiyu.yim.handler;

import com.baiyu.yim.sdk.server.constant.YIMConstant;
import com.baiyu.yim.sdk.server.handler.YIMRequestHandler;
import com.baiyu.yim.sdk.server.model.SentBody;
import com.baiyu.yim.sdk.server.model.YIMSession;
import com.baiyu.yim.service.YIMSessionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 断开连接，清除session
 * @author baiyu
 * @data 2019-12-30 17:12
 */
@Component
public class SessionClosedHandler implements YIMRequestHandler {

    @Resource
    private YIMSessionService yimSessionService;

    public void process(YIMSession ios, SentBody message) {
        Object quietly = ios.getAttribute(YIMConstant.KEY_QUIETLY_CLOSE);
        if (Objects.equals(quietly, true)) {
            return;
        }

        Object account = ios.getAttribute(YIMConstant.KEY_ACCOUNT);
        if (account == null) {
            return;
        }

        YIMSession oldSession = yimSessionService.get(account.toString());

        if (oldSession == null || oldSession.isApnsOpend()) {
            return;
        }

        oldSession.setState(YIMSession.STATE_DISABLED);
        oldSession.setNid(null);
        yimSessionService.save(oldSession);
    }
}
