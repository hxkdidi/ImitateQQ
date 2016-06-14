package com.example.zuo.qq8.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zuo.qq8.R;
import com.example.zuo.qq8.entity.User;

import java.util.List;

/**
 * Created by taojin on 2016/6/8.09:37
 */
public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyViewHolder> {

    private List<User> data;//搜索出来的用户
    //已经成为的好友
    private List<String> contacts;

    public SearchUserAdapter(List<User> data,List<String> contacts) {
        this.data = data;
        this.contacts = contacts;
    }

    public interface  OnItemButtonClickListener{
        public void onItemButtonClick(User user);
    }
    private OnItemButtonClickListener mOnItemButtonClickListener;
    public void setOnItemButtonClickListener(OnItemButtonClickListener listener){
        this.mOnItemButtonClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_user, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final User user = data.get(position);
        holder.tvUsername.setText(user.getUsername());
        holder.tvTime.setText(user.getCreatedAt());
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemButtonClickListener!=null){
                    mOnItemButtonClickListener.onItemButtonClick(user);
                }
            }
        });
        if (contacts.contains(user.getUsername())){
            holder.btnAdd.setEnabled(false);
            holder.btnAdd.setText("已是好友");
        }else {
            holder.btnAdd.setEnabled(true);
            holder.btnAdd.setText("添加");
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;
        Button btnAdd;
        TextView tvTime;
        TextView tvUsername;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            btnAdd = (Button) itemView.findViewById(R.id.btn_add);

        }
    }
}
