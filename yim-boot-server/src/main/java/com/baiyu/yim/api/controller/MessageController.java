package com.baiyu.yim.api.controller;

import com.baiyu.yim.api.controller.dto.MessageResult;
import com.baiyu.yim.push.DefaultMessagePusher;
import com.baiyu.yim.sdk.server.model.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author baiyu
 * @data 2019-12-30 17:21
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Resource
    private DefaultMessagePusher defaultMessagePusher;

    /**
     * 此方法仅仅在集群时，通过服务器调用
     *
     * @param message
     * @return
     */
    @RequestMapping(value = "/dispatch",method= RequestMethod.POST)
    public MessageResult dispatchSend(Message message) {
        return send(message);
    }


    @RequestMapping(value = "/send",method=RequestMethod.POST)
    public MessageResult send(Message message)  {

        MessageResult result = new MessageResult();

        message.setId(System.currentTimeMillis());

        defaultMessagePusher.push(message);

        result.id = message.getId();
        result.timestamp = message.getTimestamp();
        return result;
    }
}
