package com.baiyu.yim.sdk.client;

import com.baiyu.yim.sdk.client.coder.ClientMessageDecoder;
import com.baiyu.yim.sdk.client.coder.ClientMessageEncoder;
import com.baiyu.yim.sdk.client.coder.YIMLogger;
import com.baiyu.yim.sdk.client.constant.YIMConstant;
import com.baiyu.yim.sdk.client.model.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * @author baiyu
 * @data 2019-12-31 15:20
 */
class YIMConnectorManager {
    private static YIMConnectorManager manager;

    private final int READ_BUFFER_SIZE = 2048;
    private final int WRITE_BUFFER_SIZE = 1024;
    private final int CONNECT_TIME_OUT = 10 * 1000;

    private final YIMLogger LOGGER = YIMLogger.getLogger();

    private SocketChannel socketChannel ;

    private ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);

    private ExecutorService workerExecutor = Executors.newFixedThreadPool(1,new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"worker-");
        }
    });
    private ExecutorService bossExecutor = Executors.newFixedThreadPool(1,new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"boss-");
        }
    });
    private ExecutorService eventExecutor = Executors.newFixedThreadPool(1,new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"event-");
        }
    });

    private Semaphore semaphore = new Semaphore(1, true);


    private ClientMessageEncoder messageEncoder = new  ClientMessageEncoder();
    private ClientMessageDecoder messageDecoder = new  ClientMessageDecoder();



    public synchronized static YIMConnectorManager getManager() {

        if (manager == null) {
            manager = new YIMConnectorManager();
        }

        return manager;

    }

    public void connect(final String host, final int port) {


        if (isConnected()) {
            return;
        }

        bossExecutor.execute(new Runnable() {
            @Override
            public void run() {

                if (isConnected()) {
                    return;
                }

                LOGGER.startConnect(host, port);

                YIMCacheManager.getInstance().putBoolean(YIMCacheManager.KEY_YIM_CONNECTION_STATE, false);

                try {

                    semaphore.acquire();

                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.socket().setTcpNoDelay(true);
                    socketChannel.socket().setKeepAlive(true);
                    socketChannel.socket().setReceiveBufferSize(READ_BUFFER_SIZE);
                    socketChannel.socket().setSendBufferSize(WRITE_BUFFER_SIZE);

                    socketChannel.socket().connect(new InetSocketAddress(host, port),CONNECT_TIME_OUT);

                    semaphore.release();

                    handelConnectedEvent();


                    int result = -1;

                    while((result = socketChannel.read(readBuffer)) > 0) {

                        if(readBuffer.position() == readBuffer.capacity()) {
                            extendByteBuffer();
                        }

                        handelSocketReadEvent(result);

                    }

                    handelSocketReadEvent(result);

                }catch(ConnectException ignore){
                    semaphore.release();
                    handleConnectAbortedEvent();
                }catch(SocketTimeoutException ignore){
                    semaphore.release();
                    handleConnectAbortedEvent();
                }catch(IOException ignore) {
                    semaphore.release();
                    handelDisconnectedEvent();
                }catch (InterruptedException ignore) {
                    semaphore.release();
                }
            }
        });
    }



    private void handelDisconnectedEvent() {
        closeSession();
    }

    private void handleConnectAbortedEvent() {

        long interval = YIMConstant.RECONN_INTERVAL_TIME - (5 * 1000 - new Random().nextInt(15 * 1000));

        LOGGER.connectFailure(interval);

        Intent intent = new Intent();
        intent.setAction(YIMConstant.IntentAction.ACTION_CONNECTION_FAILED);
        intent.putExtra("interval", interval);
        sendBroadcast(intent);

    }

    private void handelConnectedEvent() throws IOException {

        sessionCreated();
    }

    private void handelSocketReadEvent(int result) throws IOException   {

        if(result == -1) {
            closeSession();
            return;
        }


        readBuffer.position(0);

        Object message = messageDecoder.doDecode(readBuffer);

        if(message == null) {
            return;
        }

        LOGGER.messageReceived(socketChannel,message);

        if(isHeartbeatRequest(message)) {

            send(getHeartbeatResponse());

            return;
        }

        this.messageReceived(message);
    }


    private void extendByteBuffer() {

        ByteBuffer newBuffer = ByteBuffer.allocate(readBuffer.capacity() + READ_BUFFER_SIZE / 2);
        readBuffer.position(0);
        newBuffer.put(readBuffer);

        readBuffer.clear();
        readBuffer = newBuffer;
    }


    public void send(final Protobufable body) {

        if(!isConnected()) {
            return;
        }

        workerExecutor.execute(new Runnable() {

            @Override
            public void run() {
                int result = 0;
                try {

                    semaphore.acquire();

                    ByteBuffer buffer =  messageEncoder.encode(body);
                    while(buffer.hasRemaining()){
                        result += socketChannel.write(buffer);
                    }

                } catch (Exception e) {
                    result = -1;
                }finally {

                    semaphore.release();

                    if(result <= 0) {
                        closeSession();
                    }else {
                        messageSent(body);
                    }
                }
            }
        });
    }


    public void sessionCreated() {

        LOGGER.sessionCreated(socketChannel);

        Intent intent = new Intent();
        intent.setAction(YIMConstant.IntentAction.ACTION_CONNECTION_SUCCESSED);
        sendBroadcast(intent);

    }

    public void sessionClosed() {

        LOGGER.sessionClosed(socketChannel);

        readBuffer.clear();

        if(readBuffer.capacity() > READ_BUFFER_SIZE) {
            readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
        }

        Intent intent = new Intent();
        intent.setAction(YIMConstant.IntentAction.ACTION_CONNECTION_CLOSED);
        sendBroadcast(intent);

    }

    public void messageReceived(Object obj) {

        if (obj instanceof Message) {

            Intent intent = new Intent();
            intent.setAction(YIMConstant.IntentAction.ACTION_MESSAGE_RECEIVED);
            intent.putExtra(Message.class.getName(), (Message) obj);
            sendBroadcast(intent);

        }
        if (obj instanceof ReplyBody) {

            Intent intent = new Intent();
            intent.setAction(YIMConstant.IntentAction.ACTION_REPLY_RECEIVED);
            intent.putExtra(ReplyBody.class.getName(), (ReplyBody) obj);
            sendBroadcast(intent);
        }
    }


    public void messageSent(Object message) {

        LOGGER.messageSent(socketChannel, message);

        if (message instanceof SentBody) {
            Intent intent = new Intent();
            intent.setAction(YIMConstant.IntentAction.ACTION_SENT_SUCCESSED);
            intent.putExtra(SentBody.class.getName(), (SentBody) message);
            sendBroadcast(intent);
        }
    }

    public HeartbeatResponse getHeartbeatResponse() {
        return HeartbeatResponse.getInstance();
    }

    public boolean isHeartbeatRequest(Object data) {
        return data instanceof HeartbeatRequest;
    }

    public void destroy() {
        closeSession();
    }

    public boolean isConnected() {
        return socketChannel != null && socketChannel.isConnected();
    }

    public void closeSession() {

        if(!isConnected()) {
            return;
        }

        try {
            socketChannel.close();
        } catch (IOException ignore) {
        }finally {
            this.sessionClosed();
        }
    }


    private void sendBroadcast(final Intent intent) {
        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                YIMEventBroadcastReceiver.getInstance().onReceive(intent);
            }
        });
    }

}
