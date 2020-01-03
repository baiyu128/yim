package com.baiyu.yim.sdk.server.filter.decoder;

import com.baiyu.yim.sdk.server.constant.YIMConstant;
import com.baiyu.yim.sdk.server.model.SentBody;
import com.baiyu.yim.sdk.server.model.YIMSession;
import com.baiyu.yim.sdk.server.model.proto.SentBodyProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

import java.util.List;

/**
 * 服务端接收来自应用的消息解码
 *
 * @author baiyu
 * @data 2019-12-30 14:08
 */
public class AppMessageDecoder extends ByteToMessageDecoder {
    @Override
    public void decode(ChannelHandlerContext arg0, ByteBuf buffer, List<Object> queue) throws Exception {

        /**
         * 消息头3位
         */
        if (buffer.readableBytes() < YIMConstant.DATA_HEADER_LENGTH) {
            return;
        }

        buffer.markReaderIndex();

        byte conetnType = buffer.readByte();

        byte lv = buffer.readByte();// int 低位
        byte hv = buffer.readByte();// int 高位

        int conetnLength = getContentLength(lv, hv);

        // 如果消息体没有接收完整，则重置读取，等待下一次重新读取
        if (conetnLength <= buffer.readableBytes()) {
            byte[] dataBytes = new byte[conetnLength];
            buffer.readBytes(dataBytes);

            Object message = mappingMessageObject(dataBytes, conetnType);
            if (message != null) {
                arg0.channel().attr(AttributeKey.valueOf(YIMSession.PROTOCOL)).set(YIMSession.NATIVEAPP);
                queue.add(message);
                return;
            }
        }

        buffer.resetReaderIndex();
    }

    public Object mappingMessageObject(byte[] data, byte type) throws Exception {

        if (YIMConstant.ProtobufType.C_H_RS == type) {
            SentBody body = new SentBody();
            body.setKey(YIMConstant.CLIENT_HEARTBEAT);
            body.setTimestamp(System.currentTimeMillis());
            return body;
        }

        if (YIMConstant.ProtobufType.SENTBODY == type) {
            SentBodyProto.Model bodyProto = SentBodyProto.Model.parseFrom(data);
            SentBody body = new SentBody();
            body.setKey(bodyProto.getKey());
            body.setTimestamp(bodyProto.getTimestamp());
            body.putAll(bodyProto.getDataMap());

            return body;
        }
        return null;
    }

    /**
     * 解析消息体长度
     *
     * @param type
     * @param length
     * @return
     */
    private int getContentLength(byte lv, byte hv) {
        int l = (lv & 0xff);
        int h = (hv & 0xff);
        return (l | (h <<= 8));
    }
}
