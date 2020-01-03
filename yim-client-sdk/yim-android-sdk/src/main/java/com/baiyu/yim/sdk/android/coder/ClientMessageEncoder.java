package com.baiyu.yim.sdk.android.coder;

import com.baiyu.yim.sdk.android.constant.YIMConstant;
import com.baiyu.yim.sdk.android.model.Protobufable;

import java.nio.ByteBuffer;

/**
 * 客户端消息发送前进行编码
 * @author baiyu
 * @data 2019-12-31 15:54
 */
public class ClientMessageEncoder {

    public ByteBuffer encode(Object object)  {

        Protobufable data = (Protobufable) object;
        byte[] byteArray = data.getByteArray();

        ByteBuffer iobuffer = ByteBuffer.allocate(byteArray.length + YIMConstant.DATA_HEADER_LENGTH);

        iobuffer.put(createHeader(data.getType(), byteArray.length));
        iobuffer.put(byteArray);
        iobuffer.flip();

        return iobuffer;

    }

    /**
     * 消息体最大为65535
     *
     * @param type
     * @param length
     * @return
     */
    private byte[] createHeader(byte type, int length) {
        byte[] header = new byte[YIMConstant.DATA_HEADER_LENGTH];
        header[0] = type;
        header[1] = (byte) (length & 0xff);
        header[2] = (byte) ((length >> 8) & 0xff);
        return header;
    }
}
