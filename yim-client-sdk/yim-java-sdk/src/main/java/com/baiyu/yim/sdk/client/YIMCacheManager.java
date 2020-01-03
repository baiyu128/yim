package com.baiyu.yim.sdk.client;

import java.util.HashMap;

/**
 * @author baiyu
 * @data 2019-12-31 15:21
 */
class YIMCacheManager {
    private static HashMap<String, String> YIM_CONFIG_INFO = new HashMap<String, String>();

    public static final String KEY_MANUAL_STOP = "KEY_MANUAL_STOP";

    public static final String KEY_YIM_DESTROYED = "KEY_YIM_DESTROYED";

    public static final String KEY_YIM_SERVIER_HOST = "KEY_YIM_SERVIER_HOST";

    public static final String KEY_YIM_SERVIER_PORT = "KEY_YIM_SERVIER_PORT";

    public static final String KEY_YIM_CONNECTION_STATE = "KEY_YIM_CONNECTION_STATE";

    static YIMCacheManager toolkit;

    public static YIMCacheManager getInstance() {
        if (toolkit == null) {
            toolkit = new YIMCacheManager();
        }
        return toolkit;
    }

    public void remove(String key) {
        YIM_CONFIG_INFO.remove(key);
    }

    public void putString(String key, String value) {
        YIM_CONFIG_INFO.put(key, value);

    }

    public String getString(String key) {
        return YIM_CONFIG_INFO.get(key);
    }

    public void putBoolean(String key, boolean value) {
        putString(key, Boolean.toString(value));
    }

    public boolean getBoolean(String key) {
        String value = getString(key);
        return value == null ? false : Boolean.parseBoolean(value);
    }

    public void putInt(String key, int value) {
        putString(key, String.valueOf(value));
    }

    public int getInt(String key) {
        String value = getString(key);
        return value == null ? 0 : Integer.parseInt(value);
    }
}
