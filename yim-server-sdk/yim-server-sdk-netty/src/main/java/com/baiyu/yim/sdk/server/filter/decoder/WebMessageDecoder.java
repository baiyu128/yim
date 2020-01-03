package com.baiyu.yim.sdk.server.filter.decoder;

import com.baiyu.yim.sdk.server.constant.YIMConstant;
import com.baiyu.yim.sdk.server.model.SentBody;
import com.baiyu.yim.sdk.server.model.proto.SentBodyProto;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 服务端接收来自websocket消息解码,netty自带的websocket协议解码器和普通二进制流解码器无法共存，
 * 所以只能重新写一遍websocket消息编解码咯
 * @author baiyu
 * @data 2019-12-30 14:11
 */
public class WebMessageDecoder extends ByteToMessageDecoder {

    public static final byte MASK = 0x1;// 1000 0000
    public static final byte HAS_EXTEND_DATA = 126;
    public static final byte HAS_EXTEND_DATA_CONTINUE = 127;
    public static final byte PAYLOADLEN = 0x7F;// 0111 1111

    public static final byte TAG_MASK = 0x0F;// 0000 1111 > 15

    private static final byte OPCODE_BINARY = 0x2;
    private static final byte OPCODE_CLOSE = 0x8;

    @Override
    public void decode(ChannelHandlerContext arg0, ByteBuf iobuffer, List<Object> queue) throws Exception {

        iobuffer.markReaderIndex();

        /**
         * 接下来判断fin标志位是否是1 如果是0 则等待消息接收完成
         */
        byte tag = iobuffer.readByte();
        int frameFin = tag > 0 ? 0 : 1; // 有符号byte 第一位为1则为负数 第一位为0则为正数，以此 判断fin字段是 0 还是 1
        if (frameFin == 0) {
            iobuffer.resetReaderIndex();
            return;
        }

        /**
         * 获取帧类型，因为使用了protobuf，所以只支持二进制帧 OPCODE_BINARY，以及客户端关闭连接帧通知 OPCODE_CLOSE
         */
        int frameOqcode = tag & TAG_MASK;

        if (OPCODE_BINARY == frameOqcode) {

            byte head = iobuffer.readByte();
            byte datalength = (byte) (head & PAYLOADLEN);
            int realLength = 0;

            /**
             * Payload len，7位或者7+16位或者7+64位，表示数据帧中数据大小，这里有好几种情况。 如果值为0-125，那么该值就是payload
             * data的真实长度。 如果值为126，那么该7位后面紧跟着的2个字节就是payload data的真实长度。
             * 如果值为127，那么该7位后面紧跟着的8个字节就是payload data的真实长度。
             */
            if (datalength == HAS_EXTEND_DATA) {
                realLength = iobuffer.readShort();
            } else if (datalength == HAS_EXTEND_DATA_CONTINUE) {
                realLength = (int) iobuffer.readLong();
            } else {
                realLength = datalength;
            }

            boolean masked = (head >> 7 & MASK) == 1;
            if (masked) {// 有掩码
                // 获取掩码
                byte[] mask = new byte[4];
                iobuffer.readBytes(mask);

                byte[] data = new byte[realLength];
                iobuffer.readBytes(data);
                for (int i = 0; i < realLength; i++) {
                    // 数据进行异或运算
                    data[i] = (byte) (data[i] ^ mask[i % 4]);
                }

                handleMessage(data, queue);
            }

        } else if (OPCODE_CLOSE == frameOqcode) {
            handleSocketClosed(arg0,iobuffer);
        } else {
            // 忽略其他类型的消息
            iobuffer.readBytes(new byte[iobuffer.readableBytes()]);
        }
    }

    private void handleSocketClosed(ChannelHandlerContext arg0,ByteBuf iobuffer) {
        iobuffer.readBytes(new byte[iobuffer.readableBytes()]);
        arg0.channel().close();
    }

    public void handleMessage(byte[] data, List<Object> queue) throws InvalidProtocolBufferException {
        byte type = data[0];

        /**
         * 只处理心跳响应以及，sentbody消息
         */
        if (type == YIMConstant.ProtobufType.C_H_RS) {
            SentBody body = new SentBody();
            body.setKey(YIMConstant.CLIENT_HEARTBEAT);
            body.setTimestamp(System.currentTimeMillis());
            queue.add(body);
        }

        if (type == YIMConstant.ProtobufType.SENTBODY) {

            int length = getContentLength(data[1], data[2]);
            byte[] protobuf = new byte[length];
            System.arraycopy(data, YIMConstant.DATA_HEADER_LENGTH, protobuf, 0, length);

            SentBodyProto.Model bodyProto = SentBodyProto.Model.parseFrom(protobuf);
            SentBody body = new SentBody();
            body.setKey(bodyProto.getKey());
            body.setTimestamp(bodyProto.getTimestamp());
            body.putAll(bodyProto.getDataMap());
            queue.add(body);

        }

    }

    /**
     * 解析消息体长度
     *
     * @param lv type
     * @param hv length
     * @return
     */
    private int getContentLength(byte lv, byte hv) {
        int l = (lv & 0xff);
        int h = (hv & 0xff);
        return (l | (h <<= 8));
    }
}
