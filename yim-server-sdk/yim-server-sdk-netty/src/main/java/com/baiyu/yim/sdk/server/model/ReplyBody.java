package com.baiyu.yim.sdk.server.model;

import com.baiyu.yim.sdk.server.constant.YIMConstant;
import com.baiyu.yim.sdk.server.model.feature.EncodeFormatable;
import com.baiyu.yim.sdk.server.model.proto.ReplyBodyProto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 请求应答对象
 * @author baiyu
 * @data 2019-12-30 14:40
 */
public class ReplyBody implements Serializable, EncodeFormatable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求key
     */
    private String key;

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回说明
     */
    private String message;

    /**
     * 返回数据集合
     */
    private HashMap<String, String> data = new HashMap<String, String>();

    private long timestamp;

    public ReplyBody() {
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void put(String k, String v) {
        if (v != null && k != null) {
            data.put(k, v);
        }
    }

    public void putAll(Map<String, String> map) {
        data.putAll(map);
    }

    public String get(String k) {
        return data.get(k);
    }

    public void remove(String k) {
        data.remove(k);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<String> getKeySet() {
        return data.keySet();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("#ReplyBody#").append("\n");
        buffer.append("key:").append(this.getKey()).append("\n");
        buffer.append("timestamp:").append(timestamp).append("\n");
        buffer.append("code:").append(code).append("\n");

        if (!data.isEmpty()) {
            buffer.append("data{").append("\n");
            for (String key : getKeySet()) {
                buffer.append(key).append(":").append(this.get(key)).append("\n");
            }
            buffer.append("}");
        }

        return buffer.toString();
    }

    @Override
    public byte[] getProtobufBody() {
        ReplyBodyProto.Model.Builder builder = ReplyBodyProto.Model.newBuilder();
        builder.setCode(code);
        if (message != null) {
            builder.setMessage(message);
        }
        if (!data.isEmpty()) {
            builder.putAllData(data);
        }
        builder.setKey(key);
        builder.setTimestamp(timestamp);

        return builder.build().toByteArray();
    }

    @Override
    public byte getDataType() {
        return YIMConstant.ProtobufType.REPLYBODY;
    }
}
