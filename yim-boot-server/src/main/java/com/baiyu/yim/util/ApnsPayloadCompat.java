package com.baiyu.yim.util;

import cn.teaey.apns4j.protocol.ApnsPayload;

/**
 * @author baiyu
 * @data 2019-12-30 16:51
 */
public class ApnsPayloadCompat extends ApnsPayload {
    private static final String DATA_FORMAT = "{\"aps\": {\"message\": {\"action\":\"%s\",\"content\":\"%s\",\"sender\":\"%s\",\"receiver\":\"%s\",\"format\":\"%s\"},\"content-available\": 1}}";

    private String action;
    private String content;
    private String sender;
    private String format;
    private String receiver;


    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toJsonString() {
        return String.format(DATA_FORMAT, action, content, sender,receiver,format);
    }
}
