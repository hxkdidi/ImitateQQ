package com.example.zuo.qq8.activity;

import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zuo.qq8.R;
import com.example.zuo.qq8.adapter.ChatAdapter;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity implements TextView.OnEditorActionListener, TextWatcher {

    private ImageView iv_left;
    private TextView tv_title;
    private ListView lv_chats;
    private EditText et_msg;
    private Button btn_send;
    private String username;
    private ChatAdapter chatAdapter;
    private List<EMMessage> messageList = new ArrayList<>();
    private EMMessageListener messageListener;

    @Override
    protected void handleMsg(Message msg) {

    }


    @Override
    protected void initView() {
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(ChatActivity.this, R.string.empty_username, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        iv_left = (ImageView) findViewById(R.id.iv_left);
        findViewById(R.id.iv_right).setVisibility(View.GONE);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.chatwith).replace("%%", username));
        tv_title.setVisibility(View.VISIBLE);
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setOnClickListener(this);

        lv_chats = (ListView) findViewById(R.id.lv_chats);
        et_msg = (EditText) findViewById(R.id.et_msg);
        et_msg.setOnEditorActionListener(this);
        et_msg.addTextChangedListener(this);

        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        btn_send.setEnabled(false);
    }

    @Override
    protected void initData() {
        //获取环信的会话对象
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        if (conversation != null) {
            //根据会话获取该会话的消息，是从本地数据库（环信设计的）中获取
            List<EMMessage> messages = conversation.getAllMessages();
            if (messages != null && messages.size() > 0) {
                messageList.clear();
                messageList.addAll(messages);
                //将消息标记为已读
                conversation.markAllMessagesAsRead();
            }
        }

        chatAdapter = new ChatAdapter(messageList);
        lv_chats.setAdapter(chatAdapter);
        //将listView定位到最后一条
        lv_chats.setSelection(messageList.size() - 1);

        //开启消息的监听
        initMsgListener();
    }

    private void initMsgListener() {
        messageListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                final EMMessage message = list.get(0);
                //当接收到消息的时候
                //刷新数据，刷新Adapter

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        refreshMsg();

                        Toast.makeText(ChatActivity.this, "接收到新消息了" + message.toString(), Toast.LENGTH_SHORT).show();
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

    private void refreshMsg() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        if (conversation == null) {
            return;
        }
        List<EMMessage> allMessages = conversation.getAllMessages();
        if (allMessages != null && allMessages.size() > 0) {
            messageList.clear();
            ;
            messageList.addAll(allMessages);
        }
        if (chatAdapter != null) {
            chatAdapter.notifyDataSetChanged();
            lv_chats.smoothScrollToPosition(messageList.size() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.btn_send:
                sendMsg();
                break;
        }
    }

    //发送消息的核心方法
    private void sendMsg() {
        String msg = et_msg.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(ChatActivity.this, R.string.empty_msg, Toast.LENGTH_SHORT).show();
            return;
        }
        et_msg.getText().clear();
        //发送消息
        EMMessage emMessage = EMMessage.createTxtSendMessage(msg, username);
        //将消息添加到集合中
        messageList.add(emMessage);
        chatAdapter.notifyDataSetChanged();
        //让ListView滚动到最后一行
        lv_chats.smoothScrollToPosition(messageList.size() - 1);

        //给发送的消息添加状态的监听
        emMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        chatAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                        chatAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        EMClient.getInstance().chatManager().sendMessage(emMessage);


    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.et_msg && actionId == EditorInfo.IME_ACTION_SEND) {
            sendMsg();
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString().trim())) {
            btn_send.setEnabled(false);
        } else {
            btn_send.setEnabled(true);
        }
    }
}
