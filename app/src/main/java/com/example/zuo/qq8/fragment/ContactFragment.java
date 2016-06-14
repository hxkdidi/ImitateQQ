package com.example.zuo.qq8.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.zuo.qq8.R;
import com.example.zuo.qq8.activity.AddFriendActivity;
import com.example.zuo.qq8.activity.ChatActivity;
import com.example.zuo.qq8.adapter.ContactAdapter;
import com.example.zuo.qq8.db.DBUtils;
import com.example.zuo.qq8.ui.ContactListView;
import com.example.zuo.qq8.utils.ThreadUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    private ContactListView contactListView;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact,container,false);
    }

    @Override
    protected void initData(View view) {
        ivLeft.setVisibility(View.GONE);
        tvTitle.setText(R.string.contact);
        tvTitle.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);

        contactListView = (ContactListView) view.findViewById(R.id.contactListView);

        initListener();

        refreshContacts();
    }

    private void initListener() {
        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final String username = parent.getAdapter().getItem(position).toString();

                deleteContact(username);

                return true;
            }
        });
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到聊天界面 Activity
                final String username = parent.getAdapter().getItem(position).toString();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });
    }

    private void deleteContact(final String username) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("您和"+username+"确定友尽了吗？");
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //删除用户
                ThreadUtils.runOnSubThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().contactManager().deleteContact(username);
                            //删除成功
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "删除"+username+"成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "删除"+username+"失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                            //删除失败
                        }
                    }
                });

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void refreshContacts() {
        //从数据库中获取当前用户的通讯录
        List<String> contactsFromDB = DBUtils.getContactsFromDB(getContext(), EMClient.getInstance().getCurrentUser());

        ContactAdapter contactAdapter = new ContactAdapter(contactsFromDB);

        contactListView.setAdapter(contactAdapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_right:
                startActivity(new Intent(getActivity(),AddFriendActivity.class));
                break;
        }
    }
}
