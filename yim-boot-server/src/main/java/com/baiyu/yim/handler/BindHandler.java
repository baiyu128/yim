package com.baiyu.yim.handler;

import com.baiyu.yim.push.YIMMessagePusher;
import com.baiyu.yim.sdk.server.constant.YIMConstant;
import com.baiyu.yim.sdk.server.handler.YIMRequestHandler;
import com.baiyu.yim.sdk.server.model.Message;
import com.baiyu.yim.sdk.server.model.ReplyBody;
import com.baiyu.yim.sdk.server.model.SentBody;
import com.baiyu.yim.sdk.server.model.YIMSession;
import com.baiyu.yim.service.YIMSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 账号绑定实现
 * @author baiyu
 * @data 2019-12-30 17:09
 */
@Component
public class BindHandler implements YIMRequestHandler {

    private final Logger logger = LoggerFactory.getLogger(BindHandler.class);

    @Resource
    private YIMSessionService yimSessionService;

    @Value("${server.host}")
    private String host;

    @Resource
    private YIMMessagePusher defaultMessagePusher;

    @Override
    public void process(YIMSession newSession, SentBody body) {
        ReplyBody reply = new ReplyBody();
        reply.setKey(body.getKey());
        reply.setCode(YIMConstant.ReturnCode.CODE_200);
        reply.setTimestamp(System.currentTimeMillis());

        try {

            String account = body.get("account");
            newSession.setAccount(account);
            newSession.setDeviceId(body.get("deviceId"));
            newSession.setHost(host);
            newSession.setChannel(body.get("channel"));
            newSession.setDeviceModel(body.get("device"));
            newSession.setClientVersion(body.get("version"));
            newSession.setSystemVersion(body.get("osVersion"));
            newSession.setBindTime(System.currentTimeMillis());
            /*
             * 由于客户端断线服务端可能会无法获知的情况，客户端重连时，需要关闭旧的连接
             */
            YIMSession oldSession = yimSessionService.get(account);

            /*
             * 如果是账号已经在另一台终端登录。则让另一个终端下线
             */

            if (oldSession != null && fromOtherDevice(newSession,oldSession) && oldSession.isConnected()) {
                sendForceOfflineMessage(oldSession, account, newSession.getDeviceModel());
            }

            /*
             * 有可能是同一个设备重复连接，则关闭旧的链接，这种情况一般是客户端断网，联网又重新链接上来，之前的旧链接没有来得及通过心跳机制关闭，在这里手动关闭
             * 条件1，连接来自是同一个设备
             * 条件2.2个连接都是同一台服务器
             */

            if (oldSession != null && !fromOtherDevice(newSession,oldSession) && Objects.equals(oldSession.getHost(),host)) {
                closeQuietly(oldSession);
            }

            yimSessionService.save(newSession);


        } catch (Exception exception) {
            reply.setCode(YIMConstant.ReturnCode.CODE_500);
            logger.error("Bind has error",exception);
        }

        newSession.write(reply);
    }

    private boolean fromOtherDevice(YIMSession oldSession ,YIMSession newSession) {

        return !Objects.equals(oldSession.getDeviceId(), newSession.getDeviceId());
    }

    private void sendForceOfflineMessage(YIMSession oldSession, String account, String deviceModel) {

        Message msg = new Message();
        msg.setAction(YIMConstant.MessageAction.ACTION_999);// 强行下线消息类型
        msg.setReceiver(account);
        msg.setSender("system");
        msg.setContent(deviceModel);
        msg.setId(System.currentTimeMillis());

        defaultMessagePusher.push(msg);

        closeQuietly(oldSession);

    }

    // 不同设备同一账号登录时关闭旧的连接
    private void closeQuietly(YIMSession oldSession) {
        if (oldSession.isConnected() && Objects.equals(host, oldSession.getHost())) {
            oldSession.setAttribute(YIMConstant.KEY_QUIETLY_CLOSE,true);
            oldSession.closeOnFlush();
        }
    }
}
