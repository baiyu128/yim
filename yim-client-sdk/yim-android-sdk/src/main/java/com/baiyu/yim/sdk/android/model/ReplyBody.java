package com.baiyu.yim.sdk.android.model;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * 请求应答对象
 * @author baiyu
 * @data 2019-12-31 16:54
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

    private long timestamp;

    /**
     * 返回数据集合
     */
    private Hashtable<String, String> data = new Hashtable<String, String>();

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
        data.put(k, v);
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
