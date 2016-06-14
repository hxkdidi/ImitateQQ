package com.example.zuo.qq8.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zuo.qq8.R;
import com.example.zuo.qq8.activity.ChatActivity;
import com.example.zuo.qq8.adapter.ConversationAdapter;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView lv_conversation;
    private List<EMConversation> conversationList = new ArrayList<>();
    private ConversationAdapter conversationAdapter;
    private EMMessageListener messageListener;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    protected void initData(View view) {
        ivLeft.setVisibility(View.GONE);
        tvTitle.setText(R.string.xiaoxi);
        tvTitle.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.GONE);
        lv_conversation = (ListView) view.findViewById(R.id.lv_conversation);

        //从数据库中读取所有的会话
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        if (conversations != null&&conversations.size()>0) {
            conversationList.addAll(conversations.values());
            if (conversationList.size()>0){
                //排序
                Collections.sort(conversationList, new Comparator<EMConversation>() {
                    @Override
                    public int compare(EMConversation lhs, EMConversation rhs) {
                        if (lhs==null||rhs==null){
                            return 0;
                        }else {
                            return (int) (rhs.getLastMessage().getMsgTime() - lhs.getLastMessage().getMsgTime());
                        }
                    }
                });
            }

        }

        conversationAdapter = new ConversationAdapter(conversationList);

        lv_conversation.setAdapter(conversationAdapter);

        lv_conversation.setOnItemClickListener(this);

        //添加新消息的监听，用于更新会话和未读个数
        initListener();

    }

    private void initListener() {
        messageListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> list) {
                //刷新未读消息个数
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshConversation();
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

    @Override
    public void onResume() {
        super.onResume();
        //刷新会话
        refreshConversation();
    }

    private void refreshConversation() {
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        if (conversations != null && conversations.size() > 0) {
            conversationList.clear();
            conversationList.addAll(conversations.values());
            //排序
            Collections.sort(conversationList, new Comparator<EMConversation>() {
                @Override
                public int compare(EMConversation lhs, EMConversation rhs) {

                    return (int) (rhs.getLastMessage().getMsgTime() - lhs.getLastMessage().getMsgTime());
                }
            });


            conversationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (messageListener!=null){
            EMClient.getInstance().chatManager().removeMessageListener(messageListener);
            messageListener = null;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EMConversation conversation  = (EMConversation) parent.getAdapter().getItem(position);
        String userName = conversation.getUserName();
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("username",userName);

        startActivity(intent);

    }
}
