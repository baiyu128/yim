package com.baiyu.yim.config;

import com.baiyu.yim.handler.BindHandler;
import com.baiyu.yim.handler.SessionClosedHandler;
import com.baiyu.yim.sdk.server.handler.YIMNioSocketAcceptor;
import com.baiyu.yim.sdk.server.handler.YIMRequestHandler;
import com.baiyu.yim.sdk.server.model.SentBody;
import com.baiyu.yim.sdk.server.model.YIMSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author baiyu
 * @data 2019-12-30 17:19
 */
@Configuration
public class YIMConfig implements YIMRequestHandler, ApplicationListener<ApplicationStartedEvent> {

    @Resource
    private ApplicationContext applicationContext;

    private HashMap<String,Class<? extends YIMRequestHandler>> appHandlerMap = new HashMap<>();

    @PostConstruct
    private void initHandler() {
        /*
         * 账号绑定handler
         */
        appHandlerMap.put("client_bind", BindHandler.class);
        /*
         * 连接关闭handler
         */
        appHandlerMap.put("client_closed", SessionClosedHandler.class);
    }

    @Bean(destroyMethod = "destroy")
    public YIMNioSocketAcceptor getNioSocketAcceptor(@Value("${yim.server.port}") int port) {
        YIMNioSocketAcceptor nioSocketAcceptor = new YIMNioSocketAcceptor();
        nioSocketAcceptor.setPort(port);
        nioSocketAcceptor.setAppSentBodyHandler(this);
        return nioSocketAcceptor;
    }

    @Override
    public void process(YIMSession session, SentBody body) {

        YIMRequestHandler handler = findHandlerByKey(body.getKey());

        if(handler == null) {return ;}

        handler.process(session, body);

    }

    private YIMRequestHandler findHandlerByKey(String key){
        Class<? extends YIMRequestHandler> handlerClass = appHandlerMap.get(key);
        if (handlerClass==null){
            return null;
        }
        return applicationContext.getBean(handlerClass);
    }


    /**
     * springboot启动完成之后再启动yim服务的，避免服务正在重启时，客户端会立即开始连接导致意外异常发生.
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        applicationContext.getBean(YIMNioSocketAcceptor.class).bind();
    }
}
