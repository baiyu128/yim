package com.baiyu.yim.sdk.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 请求应答对象
 * @author baiyu
 * @data 2019-12-31 15:29
 */
public class ReplyBody implements Serializable {

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
    private HashMap<String, String> data;

    private long timestamp;

    public ReplyBody() {
        data = new HashMap<String, String>();
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

    public void putAll(Map<String, String> map) {
        data.putAll(map);
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
}
