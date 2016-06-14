package com.example.zuo.qq8.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.zuo.qq8.QQApplication;
import com.example.zuo.qq8.R;
import com.example.zuo.qq8.db.DBUtils;
import com.example.zuo.qq8.event.OnContactUpdate;
import com.example.zuo.qq8.utils.ThreadUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by taojin on 2016/6/7.11:01
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        initView();
//        initData();
//    }

    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            handleMsg(msg);
        }
    };

    protected abstract void handleMsg(Message msg);

    protected SharedPreferences mSharedPreferences;
    protected  static  final  String SP_KEY_USERNAME = "username";
    protected  static  final  String SP_KEY_PWD = "pwd";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        //将当前Activity添加到QQApplication的集合中
        QQApplication qqApplication = (QQApplication) getApplication();
        qqApplication.addActivity(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将当前Activity添加到QQApplication的集合中
        QQApplication qqApplication = (QQApplication) getApplication();
        qqApplication.removeActivity(this);
    }

    protected abstract void initView();

    protected abstract void initData();

    /**
     * 同步通讯录方法
     */
    protected void loadContactsAndConversations() {
        //加载通信录（去环信服务器上获取当前用户的好友）
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> allContactsFromServer = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    //将好友信息同步到本地数据库
                    DBUtils.updateContacts(getApplicationContext(),EMClient.getInstance().getCurrentUser(),allContactsFromServer);
                    //发出通知，通知本地联系人已经跟网络同步完成
                    EventBus.getDefault().post(new OnContactUpdate(allContactsFromServer));

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BaseActivity.this, R.string.get_contacts_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //加载会话记录
        EMClient.getInstance().chatManager().loadAllConversations();//将本地数据库（环信sdk自己维护的）中的 会话信息加载到内存中

    }

}
