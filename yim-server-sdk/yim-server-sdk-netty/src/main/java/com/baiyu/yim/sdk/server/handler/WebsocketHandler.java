package com.baiyu.yim.sdk.server.handler;

import com.baiyu.yim.sdk.server.model.HandshakerResponse;
import com.baiyu.yim.sdk.server.model.SentBody;
import com.baiyu.yim.sdk.server.model.YIMSession;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * 处理websocket握手请求，返回响应的报文给浏览器
 * @author baiyu
 * @data 2019-12-30 14:16
 */
public class WebsocketHandler implements YIMRequestHandler {
    private final static String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    public void process(YIMSession session, SentBody body) {
        session.setChannel(YIMSession.CHANNEL_BROWSER);
        String secKey = body.get("key") + GUID;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(secKey.getBytes("iso-8859-1"), 0, secKey.length());
            byte[] sha1Hash = md.digest();
            secKey = new String(Base64.getEncoder().encode(sha1Hash));
        } catch (Exception ignore) {}
        session.write(new HandshakerResponse(secKey));
    }
}
