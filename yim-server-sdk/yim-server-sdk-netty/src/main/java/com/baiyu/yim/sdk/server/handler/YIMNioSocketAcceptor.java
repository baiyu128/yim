package com.baiyu.yim.sdk.server.handler;

import com.baiyu.yim.sdk.server.constant.YIMConstant;
import com.baiyu.yim.sdk.server.filter.ServerMessageDecoder;
import com.baiyu.yim.sdk.server.filter.ServerMessageEncoder;
import com.baiyu.yim.sdk.server.model.HeartbeatRequest;
import com.baiyu.yim.sdk.server.model.SentBody;
import com.baiyu.yim.sdk.server.model.YIMSession;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baiyu
 * @data 2019-12-30 14:13
 */
@ChannelHandler.Sharable
public class YIMNioSocketAcceptor extends SimpleChannelInboundHandler<SentBody> {

    private HashMap<String, YIMRequestHandler> innerHandlerMap = new HashMap<String, YIMRequestHandler>();
    private YIMRequestHandler outerRequestHandler;
    private ConcurrentHashMap<String,Channel> channelGroup = new ConcurrentHashMap<String,Channel>();
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    // 连接空闲时间
    public static final int READ_IDLE_TIME = 150;// 秒

    // 连接空闲时间
    public static final int WRITE_IDLE_TIME = 120;// 秒

    public static final int PING_TIME_OUT = 30;// 心跳响应 超时为30秒

    public void bind() {

        /*
         * 预制websocket握手请求的处理
         */
        innerHandlerMap.put(YIMConstant.CLIENT_WEBSOCKET_HANDSHAKE, new WebsocketHandler());
        innerHandlerMap.put(YIMConstant.CLIENT_HEARTBEAT, new HeartbeatHandler());

        ServerBootstrap bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {

                ch.pipeline().addLast(new ServerMessageDecoder());
                ch.pipeline().addLast(new ServerMessageEncoder());
                ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                ch.pipeline().addLast(new IdleStateHandler(READ_IDLE_TIME, WRITE_IDLE_TIME, 0));
                ch.pipeline().addLast(YIMNioSocketAcceptor.this);
            }
        });

        ChannelFuture channelFuture = bootstrap.bind(port).syncUninterruptibly();

        channelFuture.channel().closeFuture().addListener(future -> {
            destroy();
        });

    }

    public void destroy() {

        if(bossGroup != null && !bossGroup.isShuttingDown() && !bossGroup.isShutdown() ) {
            try {bossGroup.shutdownGracefully();}catch(Exception ignore) {}
            return;
        }

        if(workerGroup != null && !workerGroup.isShuttingDown() && !workerGroup.isShutdown() ) {
            try {workerGroup.shutdownGracefully();}catch(Exception ignore) {}
            return;
        }

    }

    /**
     * 设置应用层的sentbody处理handler
     *
     * @param outerRequestHandler
     */
    public void setAppSentBodyHandler(YIMRequestHandler outerRequestHandler) {
        this.outerRequestHandler = outerRequestHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SentBody body) {

        YIMSession session = new YIMSession(ctx.channel());

        YIMRequestHandler handler = innerHandlerMap.get(body.getKey());
        /*
         * 如果有内置的特殊handler需要处理，则使用内置的
         */
        if (handler != null) {
            handler.process(session, body);
            return;
        }

        /*
         * 有业务层去处理其他的sentbody
         */
        outerRequestHandler.process(session, body);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        channelGroup.remove(ctx.channel().id().asShortText());

        YIMSession session = new YIMSession(ctx.channel());
        SentBody body = new SentBody();
        body.setKey(YIMConstant.CLIENT_CONNECT_CLOSED);
        outerRequestHandler.process(session, body);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        channelGroup.put(ctx.channel().id().asShortText(),ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state().equals(IdleState.WRITER_IDLE)) {
            ctx.channel().attr(AttributeKey.valueOf(YIMConstant.HEARTBEAT_KEY)).set(System.currentTimeMillis());
            ctx.channel().writeAndFlush(HeartbeatRequest.getInstance());
        }

        /*
         * 如果心跳请求发出30秒内没收到响应，则关闭连接
         */
        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state().equals(IdleState.READER_IDLE)) {

            Long lastTime = (Long) ctx.channel().attr(AttributeKey.valueOf(YIMConstant.HEARTBEAT_KEY)).get();
            if (lastTime != null && System.currentTimeMillis() - lastTime >= PING_TIME_OUT) {
                ctx.channel().close();
            }

            ctx.channel().attr(AttributeKey.valueOf(YIMConstant.HEARTBEAT_KEY)).set(null);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }


    public Channel getManagedSession(String id) {
        if (id == null) {
            return null;
        }
        return channelGroup.get(id);
    }
}
