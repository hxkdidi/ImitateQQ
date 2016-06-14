package com.example.zuo.qq8.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zuo.qq8.R;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by taojin on 2016/6/10.11:42
 */
public class ConversationAdapter extends BaseAdapter {

    private List<EMConversation> data;
    public ConversationAdapter(List<EMConversation> data){
        this.data =data;
    }
    @Override
    public int getCount() {
        return data==null?0:data.size();
    }

    @Override
    public EMConversation getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_conversation,parent,false);

            viewHolder.tvMsg = (TextView) convertView.findViewById(R.id.tv_msg);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tvUnread = (TextView) convertView.findViewById(R.id.tv_unread);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tv_username);

            convertView.setTag(viewHolder);

        }else {
                viewHolder = (ViewHolder) convertView.getTag();
        }

        EMConversation conversation = data.get(position);
        EMMessage lastMessage = conversation.getLastMessage();
        long msgTime = lastMessage.getMsgTime();
        int unreadMsgCount = conversation.getUnreadMsgCount();

        viewHolder.tvUsername.setText(conversation.getUserName());

        viewHolder.tvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));

        if (unreadMsgCount>99){
            viewHolder.tvUnread.setText("99+");
            viewHolder.tvUnread.setVisibility(View.VISIBLE);
        }else if (unreadMsgCount>0){
            viewHolder.tvUnread.setText(unreadMsgCount+"");
            viewHolder.tvUnread.setVisibility(View.VISIBLE);
        }else {
            viewHolder.tvUnread.setVisibility(View.GONE);
        }

        if (lastMessage.getType()== EMMessage.Type.TXT){
            EMTextMessageBody messageBody = (EMTextMessageBody) lastMessage.getBody();
            viewHolder.tvMsg.setText(messageBody.getMessage());
        }else {
            viewHolder.tvMsg.setText("");
        }



        return convertView;
    }

    static class  ViewHolder {
        TextView tvUsername;
        TextView tvTime;
        TextView tvMsg;
        TextView tvUnread;

    }
}
