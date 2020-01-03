package com.baiyu.yim.sdk.client.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * java |android 客户端请求结构
 * @author baiyu
 * @data 2019-12-31 15:27
 */
public class Intent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String action;

    private HashMap<String, Object> data = new HashMap<String, Object>();

    public Intent() {
    }

    public Intent(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void putExtra(String key, Object value) {
        data.put(key, value);
    }

    public Object getExtra(String key) {
        return data.get(key);
    }

    public long getLongExtra(String key, long defValue) {
        Object v = getExtra(key);
        try {
            return Long.parseLong(v.toString());
        } catch (Exception e) {
            return defValue;
        }
    }
}
