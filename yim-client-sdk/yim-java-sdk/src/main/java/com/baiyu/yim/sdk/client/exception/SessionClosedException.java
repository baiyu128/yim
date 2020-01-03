package com.baiyu.yim.sdk.client.exception;

/**
 * @author baiyu
 * @data 2019-12-31 15:25
 */
public class SessionClosedException extends Exception {

    private static final long serialVersionUID = 1L;

    public SessionClosedException() {
        super();
    }

    public SessionClosedException(String s) {
        super(s);
    }
}
