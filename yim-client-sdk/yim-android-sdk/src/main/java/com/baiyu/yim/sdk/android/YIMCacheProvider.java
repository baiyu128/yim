package com.baiyu.yim.sdk.android;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

/**
 * @author baiyu
 * @data 2019-12-31 17:01
 */
public class YIMCacheProvider extends ContentProvider {

    static final String MODEL_KEY = "PRIVATE_CIM_CONFIG";

    @Override
    public int delete(Uri arg0, String key, String[] arg2) {
        getContext().getSharedPreferences(MODEL_KEY, Context.MODE_PRIVATE).edit().remove(key).apply();
        return 0;
    }

    @Override
    public String getType(Uri arg0) {
        return null;
    }

    @Override
    public Uri insert(Uri arg0, ContentValues values) {
        String key = values.getAsString("key");
        String value = values.getAsString("value");
        getContext().getSharedPreferences(MODEL_KEY, Context.MODE_PRIVATE).edit().putString(key, value).apply();
        return null;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri arg0, String[] arg1, String key, String[] arg3, String arg4) {
        MatrixCursor cursor = new MatrixCursor(new String[] { "value" });
        String value = getContext().getSharedPreferences(MODEL_KEY, Context.MODE_PRIVATE).getString(arg1[0], null);
        cursor.addRow(new Object[] { value });
        return cursor;
    }

    @Override
    public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        return 0;
    }
}
