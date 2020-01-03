import com.baiyu.yim.sdk.client.YIMEventBroadcastReceiver;
import com.baiyu.yim.sdk.client.YIMEventListener;
import com.baiyu.yim.sdk.client.YIMPushManager;
import com.baiyu.yim.sdk.client.model.Message;
import com.baiyu.yim.sdk.client.model.ReplyBody;

/**
 * @author baiyu
 * @data 2020-01-02 15:50
 */
public class YIMJavaClient implements YIMEventListener {
    public static void startup() {
        /**
         * 第一步 设置运行时参数
         */
        YIMPushManager.setClientVersion("1.0.0");// 客户端程序版本


        /**
         * 第二步 设置全局的事件监听器
         */
        YIMEventBroadcastReceiver.getInstance().setGlobalYIMEventListener(new YIMJavaClient());


        /**
         * 第三步 连接到服务器
         */
        YIMPushManager.connect("127.0.0.1", 23456);

    }


    @Override
    public void onConnectionClosed() {
        System.out.println("onConnectionClosed");
        /**
         * 在此可以将事件分发到各个监听了YIMEventBroadcastReceiver的地方
         * 第一步 连接到服务器 在需要监听事件的类调用YIMListenerManager.registerMessageListener(listener);
         * 第二部 在此调用YIMListenerManager.notifyOnConnectionClosed()
         */
    }

    @Override
    public void onConnectionFailed() {
        System.out.println("onConnectionFailed");
        /**
         * 在此可以将事件分发到各个监听了YIMEventBroadcastReceiver的地方
         * 第一步 连接到服务器 在需要监听事件的类调用YIMListenerManager.registerMessageListener(listener);
         * 第二部 在此调用YIMListenerManager.notifyOnConnectionFailed(e)
         */
    }

    @Override
    public void onConnectionSuccessed(boolean hasAutoBind) {
        System.out.println("onConnectionSuccessed");
        if(!hasAutoBind){
            YIMPushManager.bindAccount("10000");
        }
        /**
         * 在此可以将事件分发到各个监听了YIMEventBroadcastReceiver的地方
         * 第一步 连接到服务器 在需要监听事件的类调用YIMListenerManager.registerMessageListener(listener);
         * 第二部 在此调用YIMListenerManager.notifyOnConnectionSuccessed(hasAutoBind)
         */
    }

    @Override
    public void onMessageReceived(Message message) {
        System.out.println(message.toString());
        /**
         * 在此可以将事件分发到各个监听了YIMEventBroadcastReceiver的地方
         * 第一步 连接到服务器 在需要监听事件的类调用YIMListenerManager.registerMessageListener(listener);
         * 第二部 在此调用YIMListenerManager.notifyOnMessageReceived(message)
         */
    }


    @Override
    public void onReplyReceived(ReplyBody replybody) {
        System.out.println(replybody.toString());
        /**
         * 在此可以将事件分发到各个监听了YIMEventBroadcastReceiver的地方
         * 第一步 连接到服务器 在需要监听事件的类调用YIMListenerManager.registerMessageListener(listener);
         * 第二部 在此调用YIMListenerManager.notifyOnReplyReceived(replybody)
         */
    }

    public static void main(String[] a){
        startup();
    }


    @Override
    public int getEventDispatchOrder() {
        // TODO Auto-generated method stub
        return 0;
    }
}
