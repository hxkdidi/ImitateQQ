package com.example.zuo.qq8.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zuo.qq8.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by taojin on 2016/6/10.09:46
 */
public class ChatAdapter extends BaseAdapter {

    private List<EMMessage> data;

    public ChatAdapter(List<EMMessage> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public EMMessage getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = data.get(position);
        //如果是收到的就返回0， 如果是发送的就返回1
        return message.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (getItemViewType(position) == 0) {//接收的

                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_receive, parent, false);
            } else {//发送的
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_send, parent, false);
                viewHolder.ivState = (ImageView) convertView.findViewById(R.id.iv_state);
            }
            viewHolder.tvMsg = (TextView) convertView.findViewById(R.id.tv_msg);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);


            //将ViewHolder绑定到convertView
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        EMMessage message = data.get(position);

        long msgTime = message.getMsgTime();
        if (position == 0) {
            viewHolder.tvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));
            viewHolder.tvTime.setVisibility(View.VISIBLE);
        } else {
            EMMessage preMsg = data.get(position - 1);//获取上一条消息
            long preMsgTime = preMsg.getMsgTime();
            if (DateUtils.isCloseEnough(msgTime, preMsgTime)) {
                //不显示
                viewHolder.tvTime.setVisibility(View.GONE);
            } else {
                viewHolder.tvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));
                viewHolder.tvTime.setVisibility(View.VISIBLE);
            }
        }
        if (message.getType() == EMMessage.Type.TXT) {
            EMTextMessageBody messageBody = (EMTextMessageBody) message.getBody();
            String messageString = messageBody.getMessage();
            viewHolder.tvMsg.setText(messageString);
        }

        //如果是发送的消息，才需要处理state
        if (getItemViewType(position) == 1) {
            if (message.status() == EMMessage.Status.SUCCESS) {//发送成功
                viewHolder.ivState.setVisibility(View.GONE);
            } else if (message.status() == EMMessage.Status.INPROGRESS) {//正在发送
                viewHolder.ivState.setVisibility(View.VISIBLE);
                viewHolder.ivState.setImageResource(R.drawable.send_msg_progress_anim);
                AnimationDrawable drawable = (AnimationDrawable) viewHolder.ivState.getDrawable();
                if (drawable.isRunning()){
                    drawable.stop();
                }
                drawable.start();
            } else {//失败
                viewHolder.ivState.setVisibility(View.VISIBLE);
                viewHolder.ivState.setImageResource(R.drawable.msg_error);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvMsg;
        TextView tvTime;
        ImageView ivState;
    }
}
