package com.baiyu.yim.sdk.android.coder;

import com.baiyu.yim.sdk.android.constant.YIMConstant;
import com.baiyu.yim.sdk.android.model.HeartbeatRequest;
import com.baiyu.yim.sdk.android.model.Message;
import com.baiyu.yim.sdk.android.model.ReplyBody;
import com.baiyu.yim.sdk.android.model.proto.MessageProto;
import com.baiyu.yim.sdk.android.model.proto.ReplyBodyProto;
import com.google.protobuf.InvalidProtocolBufferException;

import java.nio.ByteBuffer;

/**
 * 客户端消息解码
 * @author baiyu
 * @data 2019-12-31 15:53
 */
public class ClientMessageDecoder {

    public Object doDecode(ByteBuffer iobuffer)   {

        /**
         * 消息头3位
         */
        if (iobuffer.remaining() < YIMConstant.DATA_HEADER_LENGTH) {
            return null;
        }

        iobuffer.mark();

        byte conetnType = iobuffer.get();

        byte lv = iobuffer.get();// int 低位
        byte hv = iobuffer.get();// int 高位

        int conetnLength = getContentLength(lv, hv);

        // 如果消息体没有接收完整，则重置读取，等待下一次重新读取
        if (conetnLength > iobuffer.remaining()) {
            iobuffer.reset();
            return null;
        }

        byte[] dataBytes = new byte[conetnLength];
        iobuffer.get(dataBytes, 0, conetnLength);

        iobuffer.position(0);

        try {
            return mappingMessageObject(dataBytes, conetnType);
        } catch (InvalidProtocolBufferException e) {
            return null;
        }

    }

    private Object mappingMessageObject(byte[] bytes, byte type) throws InvalidProtocolBufferException {

        if (YIMConstant.ProtobufType.S_H_RQ == type) {
            HeartbeatRequest request = HeartbeatRequest.getInstance();
            return request;
        }

        if (YIMConstant.ProtobufType.REPLYBODY == type) {
            ReplyBodyProto.Model bodyProto = ReplyBodyProto.Model.parseFrom(bytes);
            ReplyBody body = new ReplyBody();
            body.setKey(bodyProto.getKey());
            body.setTimestamp(bodyProto.getTimestamp());
            body.putAll(bodyProto.getDataMap());
            body.setCode(bodyProto.getCode());
            body.setMessage(bodyProto.getMessage());
            return body;
        }

        if (YIMConstant.ProtobufType.MESSAGE == type) {
            MessageProto.Model bodyProto = MessageProto.Model.parseFrom(bytes);
            Message message = new Message();
            message.setId(bodyProto.getId());
            message.setAction(bodyProto.getAction());
            message.setContent(bodyProto.getContent());
            message.setSender(bodyProto.getSender());
            message.setReceiver(bodyProto.getReceiver());
            message.setTitle(bodyProto.getTitle());
            message.setExtra(bodyProto.getExtra());
            message.setTimestamp(bodyProto.getTimestamp());
            message.setFormat(bodyProto.getFormat());
            return message;
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
