package com.baiyu.yim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.reactive.WebSocketReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;

/**
 * 其中@SpringBootApplication申明让spring boot自动给程序进行必要的配置，
 * 等价于以默认属性使用@Configuration，@EnableAutoConfiguration和@ComponentScan
 * @author baiyu
 * @data 2019-12-30 16:43
 */
@SpringBootApplication(exclude = { WebSocketMessagingAutoConfiguration.class,
        WebSocketReactiveAutoConfiguration.class,
        WebSocketServletAutoConfiguration.class,
        WebServicesAutoConfiguration.class,
        JmxAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        ValidationAutoConfiguration.class,
        WebClientAutoConfiguration.class,
        JacksonAutoConfiguration.class })
public class ServerLauncher {
    public static void main(String[] args) {
        SpringApplication.run(ServerLauncher.class, args);
    }
}
