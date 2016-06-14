package com.example.zuo.qq8;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zuo.qq8.activity.BaseActivity;
import com.example.zuo.qq8.event.OnContactUpdate;
import com.example.zuo.qq8.fragment.BaseFragment;
import com.example.zuo.qq8.fragment.ContactFragment;
import com.example.zuo.qq8.fragment.ConversationFragment;
import com.example.zuo.qq8.fragment.PluginFragment;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private ImageView iv_contact;
    private ImageView iv_plugin;
    private ImageView iv_conversation;

    private List<BaseFragment> fragmentList = new ArrayList<BaseFragment>();
    private ConversationFragment conversationFragment;
    private ContactFragment contactFragment;
    private PluginFragment pluginFragment;
    private FragmentManager fragmentManager;
    private int index;
    private EMContactListener emContactListener;
    private TextView tv_unread;
    private EMMessageListener messageListener;

    @Override
    protected void handleMsg(Message msg) {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);

        iv_conversation = (ImageView) findViewById(R.id.iv_conversation);
        iv_contact = (ImageView) findViewById(R.id.iv_contact);
        iv_plugin = (ImageView) findViewById(R.id.iv_plugin);
        tv_unread = (TextView) findViewById(R.id.tv_unread);


        iv_conversation.setOnClickListener(this);
        iv_contact.setOnClickListener(this);
        iv_plugin.setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void initData() {
        //初始化三个Fragment
        //不走任何生命周期方法
        conversationFragment = new ConversationFragment();
        contactFragment = new ContactFragment();
        pluginFragment = new PluginFragment();

        fragmentList.add(conversationFragment);
        fragmentList.add(contactFragment);
        fragmentList.add(pluginFragment);

        Fragment fragment0 = fragmentManager.findFragmentByTag("0");
        if (fragment0 != null) {
            fragmentManager.beginTransaction().remove(fragment0).commit();
        }
        Fragment fragment1 = fragmentManager.findFragmentByTag("1");
        if (fragment1 != null) {
            fragmentManager.beginTransaction().remove(fragment1).commit();
        }
        Fragment fragment2 = fragmentManager.findFragmentByTag("2");
        if (fragment2 != null) {
            fragmentManager.beginTransaction().remove(fragment2).commit();
        }

        //默认让消息Fragment选中
        fragmentManager.beginTransaction().add(R.id.fl_content, conversationFragment, "0").commit();
        currentIndex = 0;
        iv_conversation.setSelected(true);

        //将当前MainActivity作为消息的订阅者，当有消息被发送过来就会回调当前MainActivity的方法
        EventBus.getDefault().register(this);

        //开启好友监听
        initContactListener();

        //初始化未读消息
        initUnread();

        //添加消息监听器
        initMsgListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initUnread();
    }

    private void initMsgListener() {
        messageListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                //刷新未读消息个数
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initUnread();
                    }
                });
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


    private void initUnread() {
        //获取所有的未读消息
        int unreadMsgsCount = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        if (unreadMsgsCount > 99) {
            tv_unread.setVisibility(View.VISIBLE);
            tv_unread.setText("99+");
        } else if (unreadMsgsCount > 0) {
            tv_unread.setVisibility(View.VISIBLE);
            tv_unread.setText(unreadMsgsCount + "");
        } else {
            tv_unread.setVisibility(View.GONE);
        }
    }

    private void initContactListener() {
        emContactListener = new EMContactListener() {
            @Override
            public void onContactAdded(final String s) {
                //当添加好的时候
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //更新好友列表
                        loadContactsAndConversations();
                        Toast.makeText(MainActivity.this, "添加了新好友：" + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onContactDeleted(final String s) {
                //当删除了的时候
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadContactsAndConversations();
                        Toast.makeText(MainActivity.this, "删除了好友关系：" + s, Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onContactInvited(final String s, final String s1) {
                //同意请求
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(s);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                //当接收到好友添加邀请的时候
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "收到了s=" + s + ",s1=" + s1, Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onContactAgreed(final String s) {
                //当我发出的要有要求被别人同意的时候
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "邀请被同意了：s=" + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onContactRefused(String s) {
                //当我的请求被拒绝的时候
            }
        };
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (emContactListener != null) {
            EMClient.getInstance().contactManager().removeContactListener(emContactListener);
            emContactListener = null;
        }
        if (messageListener!=null){
            EMClient.getInstance().chatManager().removeMessageListener(messageListener);
            messageListener = null;
        }
    }

    //该方法是EventBus特定的方法，该方法会被主线程调用
    public void onEventMainThread(OnContactUpdate onContactUpdate) {
//        List<String> contacts = onContactUpdate.contacts;
//        //显示到ContactFragment上
//            for (int i = 0; i < contacts.size(); i++) {
//            Log.d(TAG, "onEventMainThread: "+contacts.get(i));
//
//        }
        if (contactFragment != null && contactFragment.isAdded()) {
            contactFragment.refreshContacts();
        }

    }


    //用于记录当前显示的Fragment
    private int currentIndex = 0;

    @Override
    public void onClick(View v) {
        iv_conversation.setSelected(false);
        iv_contact.setSelected(false);
        iv_plugin.setSelected(false);
        int index = 0;
        switch (v.getId()) {
            case R.id.iv_conversation:
                index = 0;
                iv_conversation.setSelected(true);
                break;
            case R.id.iv_contact:
                index = 1;
                iv_contact.setSelected(true);
                break;
            case R.id.iv_plugin:
                index = 2;
                iv_plugin.setSelected(true);
                break;
        }
        if (index == currentIndex) {
            return;
        }
        BaseFragment baseFragment = fragmentList.get(index);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //先判断该Fragment是否已经添加到Activity了，如果没有则添加，如果有，则显示
        if (baseFragment.isAdded()) {
            //则显示
            fragmentTransaction.show(baseFragment);
        } else {
            //添加
            fragmentTransaction.add(R.id.fl_content, baseFragment, index + "");
        }
        //隐藏之前显示的Fragment
        fragmentTransaction.hide(fragmentList.get(currentIndex));
        //提交事务
        fragmentTransaction.commit();

        currentIndex = index;

    }
}
