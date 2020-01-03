package com.baiyu.yim.sdk.server.filter;

import com.baiyu.yim.sdk.server.constant.YIMConstant;
import com.baiyu.yim.sdk.server.filter.decoder.AppMessageDecoder;
import com.baiyu.yim.sdk.server.filter.decoder.WebMessageDecoder;
import com.baiyu.yim.sdk.server.model.SentBody;
import com.baiyu.yim.sdk.server.model.YIMSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 服务端接收消息路由解码，通过消息类型分发到不同的真正解码器
 * @author baiyu
 * @data 2019-12-30 14:02
 */
public class ServerMessageDecoder extends ByteToMessageDecoder {

    private WebMessageDecoder webMessageDecoder;
    private AppMessageDecoder appMessageDecoder;

    public static final Pattern SEC_KEY_PATTERN = Pattern.compile("^(Sec-WebSocket-Key:).+",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    public static final Pattern UPGRADE_PATTERN = Pattern.compile("^(Upgrade:).+",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    public ServerMessageDecoder() {
        webMessageDecoder = new WebMessageDecoder();
        appMessageDecoder = new AppMessageDecoder();

    }

    @Override
    public void decode(ChannelHandlerContext arg0, ByteBuf buffer, List<Object> queue) throws Exception {

        Object protocol = arg0.channel().attr(AttributeKey.valueOf(YIMSession.PROTOCOL)).get();

        if (Objects.equals(YIMSession.WEBSOCKET, protocol)) {
            webMessageDecoder.decode(arg0, buffer, queue);
            return;
        }

        if (Objects.equals(YIMSession.NATIVEAPP, protocol)) {
            appMessageDecoder.decode(arg0, buffer, queue);
            return;
        }

        boolean handshake = tryWebsocketHandleHandshake(arg0, buffer, queue);
        if (!handshake) {
            appMessageDecoder.decode(arg0, buffer, queue);
        }
    }

    private boolean tryWebsocketHandleHandshake(ChannelHandlerContext arg0, ByteBuf iobuffer, List<Object> queue) {

        iobuffer.markReaderIndex();

        byte[] data = new byte[iobuffer.readableBytes()];
        iobuffer.readBytes(data);

        String request = new String(data);
        String secKey = getSecWebSocketKey(request);
        boolean handShake = secKey != null && Objects.equals(getUpgradeProtocol(request), YIMSession.WEBSOCKET);
        if (handShake) {
            /**
             * 重要，握手响应之后，删除标志HANDSHAKE_FRAME,并标记当前session的协议为websocket
             */
            arg0.channel().attr(AttributeKey.valueOf(YIMSession.PROTOCOL)).set(YIMSession.WEBSOCKET);

            SentBody body = new SentBody();
            body.setKey(YIMConstant.CLIENT_WEBSOCKET_HANDSHAKE);
            body.setTimestamp(System.currentTimeMillis());
            body.put("key", secKey);
            queue.add(body);

        } else {
            iobuffer.resetReaderIndex();
        }

        return handShake;
    }

    /**
     * 通过正则获取websocket握手消息中的Sec-WebSocket-Key
     *
     * @param message
     * @return
     */
    private String getSecWebSocketKey(String message) {
        Matcher m = SEC_KEY_PATTERN.matcher(message);
        if (m.find()) {
            return m.group().split(":")[1].trim();
        }
        return null;
    }

    /**
     * 通过正则获取websocket握手消息中的 Upgrade 值 预期为websocket
     *
     * @param message
     * @return
     */
    private String getUpgradeProtocol(String message) {
        Matcher m = UPGRADE_PATTERN.matcher(message);
        if (m.find()) {
            return m.group().split(":")[1].trim();
        }
        return null;
    }
}
