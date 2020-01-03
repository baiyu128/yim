package com.baiyu.yim.sdk.client.model;

import com.baiyu.yim.sdk.client.constant.YIMConstant;
import com.baiyu.yim.sdk.model.proto.SentBodyProto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * java |android 客户端请求结构
 * @author baiyu
 * @data 2019-12-31 15:30
 */
public class SentBody implements Serializable, Protobufable  {

    private static final long serialVersionUID = 1L;

    private String key;

    private HashMap<String, String> data = new HashMap<String, String>();;

    private long timestamp;

    public SentBody() {
        timestamp = System.currentTimeMillis();
    }

    public String getKey() {
        return key;
    }

    public String get(String k) {
        return data.get(k);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public Set<String> getKeySet() {
        return data.keySet();
    }

    public void remove(String k) {
        data.remove(k);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("#SentBody#").append("\n");
        ;
        buffer.append("key:").append(key).append("\n");
        buffer.append("timestamp:").append(timestamp).append("\n");
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
    public byte[] getByteArray() {
        SentBodyProto.Model.Builder builder = SentBodyProto.Model.newBuilder();
        builder.setKey(key);
        builder.setTimestamp(timestamp);
        if (!data.isEmpty()) {
            builder.putAllData(data);
        }
        return builder.build().toByteArray();
    }

    @Override
    public byte getType() {
        return YIMConstant.ProtobufType.SENTBODY;
    }
}
