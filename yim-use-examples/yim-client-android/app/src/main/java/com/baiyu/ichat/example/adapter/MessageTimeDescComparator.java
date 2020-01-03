package com.baiyu.ichat.example.adapter;

import com.baiyu.yim.sdk.android.model.Message;

import java.util.Comparator;

/**
 * @author baiyu
 * @data 2020-01-03 9:34
 */
public class MessageTimeDescComparator implements Comparator<Message>  {

    @Override
    public int compare(Message arg0, Message arg1) {

        return (int) (arg1.getTimestamp() - arg0.getTimestamp());
    }
}
