package com.baiyu.yim.api.controller.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * @author baiyu
 * @data 2019-12-30 17:21
 */
public class BaseResult {
    public int code = HttpStatus.OK.value();
    public String message;
    public Object data;
    public List<?> dataList;
}
