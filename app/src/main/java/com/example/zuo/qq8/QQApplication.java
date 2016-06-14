package com.example.zuo.qq8;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.zuo.qq8.activity.ChatActivity;
import com.example.zuo.qq8.activity.LoginActivity;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.Bmob;

/**
 * Created by taojin on 2016/6/7.10:16
 */
public class QQApplication extends Application {
    private static final String TAG = "QQApplication";
    private EMMessageListener messageListener;
    private SoundPool soundPool;
    private int sortSoundID;
    private int longSoundID;
    private NotificationManager notificationManager;
    private Bitmap bitmap;
    private Handler handler = new Handler();
    private List<Activity> activityList = new ArrayList<>();

    @Override
    public void onCreate() {//被调用可能不止一次，如果应用中有service，并且该服务是在一个独立的进程中的。
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar5);
        initHuanxin();//消息转发平台
        initBmob();//云数据库
        initSoudPool();//初始化音效
        initMsgListener();//初始化消息监听
        initConectListener();//初始化连接监听

    }

    private void initConectListener() {
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
                //连接成功
            }

            @Override
            public void onDisconnected(int i) {
                //连接失败
                if (i== EMError.USER_LOGIN_ANOTHER_DEVICE){//用户被其他设备登录，挤掉了
                    //首先判断当前应用是否在后台
                    //如果是后台 Toast
                    //如果不在后台，跳转到登录界面，让用户重新登录
                    if (isBackgroundRunning()){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(QQApplication.this, "被其他设备给挤掉了", Toast.LENGTH_SHORT).show();
                                //清除所有的其他Activity
                                finishAllActivity();
                            }
                        });
                    }else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(QQApplication.this, "被其他设备给挤掉了，请重新登录", Toast.LENGTH_SHORT).show();
                                //跳转到LoginActivity界面
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //先清除其他所有的Activity
                                finishAllActivity();
                                startActivity(intent);
                            }
                        });
                    }
                }else if (i==EMError.USER_REMOVED){//用户被后台给删除掉了
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QQApplication.this, "你被删除了，请重新登录", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {//网络异常
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
////                            Toast.makeText(QQApplication.this, "没有网络可用", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
            }
        });
    }

    public void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    public void addActivity(Activity activity){
        if (!activityList.contains(activity)){
            activityList.add(activity);
        }
    }
    //清除所有的没有finish掉的Activity
    private void finishAllActivity() {
        for (int i = 0; i < activityList.size(); i++) {
            Activity activity = activityList.get(i);
            activity.finish();
        }
        activityList.clear();
    }

    private void initSoudPool() {
        //参数1：总共要加载几个音效
        //参数2：音乐类型
        //参数3：音乐的比特率，目前此功能还未实现，传入0。
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        //将音乐资源加载到内存
        //参数3：音乐的优先级，此功能为实现，传入1.
        sortSoundID = soundPool.load(this, R.raw.sort, 1);
        longSoundID = soundPool.load(this, R.raw.long_sound, 1);
    }

    private void initMsgListener() {
        messageListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> list) {
                //当收到消息的时候
                //1 发送声音(如果是后台播放长声音，前台播放短声音)
                if (isBackgroundRunning()){
                    soundPool.play(longSoundID,1,1,0,0,1);
                    //2弹出通知（在后台的情况下）
                    sendNotify(list.get(0));
                }else {
                    soundPool.play(sortSoundID,1,1,0,0,1);
                }

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        };
        EMClient.getInstance().chatManager().addMessageListener(messageListener);

    }

    //通知栏通知消息
    private void sendNotify(EMMessage emMessage) {
        String message = "";
        if (emMessage.getType()== EMMessage.Type.TXT){
            EMTextMessageBody messageBody = (EMTextMessageBody) emMessage.getBody();
            message = messageBody.getMessage();
        }
        Intent mainIntent = new Intent(this,MainActivity.class);
        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("username",emMessage.getUserName());
        Intent[] intents = {mainIntent,chatIntent};
        //点击进入后创建两个Activity，一个是MainActivity，一个是ChatActivity
        PendingIntent pendingIntent = PendingIntent.getActivities(this,1,intents,PendingIntent.FLAG_UPDATE_CURRENT);

        //参数1：通知的标识
        //参数2：Notification

        Notification notification = new Notification.Builder(this)
                .setWhen(System.currentTimeMillis())//必须设置消息的产生时间
                .setContentTitle("您有一条新消息")
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.mipmap.avatar3)
                .setAutoCancel(true)//设置允许自动销毁
                .setContentInfo(emMessage.getFrom())//右侧的文本内容 显示用户名
                .setContentText(message)//左侧的文本内容 显示消息内容
                .setContentIntent(pendingIntent)//设置点击事件，点击后进入会话界面
                .build();
        notificationManager.notify(1,notification);

    }

    private boolean isBackgroundRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(100);//需要添加权限
        if (runningTasks!=null){
            //获取第一个任务
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
            ComponentName topActivity = runningTaskInfo.topActivity;
            String packageName = topActivity.getPackageName();
            //如果顶端Activity的包名跟自己应用的包名一致，则说明是前台运行，否则就是后台
            if (packageName.equals(getPackageName())){
                return  false;
            }else {
                return  true;
            }
        }

        return false;
    }

    private void initBmob() {
        Bmob.initialize(this,"b9b30abc18129778347c9f863186cf07");
    }

    //进程名称就是包名
    //进程名称就是包名：remote

    private void initHuanxin() {
        //判断是否已经初始化
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(getPackageName())) {
            Log.e(TAG, "enter the service process!");
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        EMOptions options = new EMOptions();
        options.setAutoLogin(true);//设置自动登录
        options.setAcceptInvitationAlways(false);//设置是否自动添加别人的好友请求，true代表自动同意,false代表需要手动同意，走监听器
        EMClient.getInstance().init(this, options);
        EMClient.getInstance().setDebugMode(true);
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
//        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

}
