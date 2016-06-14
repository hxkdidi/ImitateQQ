package com.example.zuo.qq8.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zuo.qq8.R;
import com.example.zuo.qq8.adapter.SearchUserAdapter;
import com.example.zuo.qq8.db.DBUtils;
import com.example.zuo.qq8.entity.User;
import com.example.zuo.qq8.utils.DialogUtils;
import com.example.zuo.qq8.utils.ThreadUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class AddFriendActivity extends BaseActivity {

    private TextView tv_title;
    private EditText et_username;
    private ImageView iv_search;
    private RelativeLayout rl_nodata;
    private RecyclerView recyclerView;
    private String currentUser;
    private ImageView iv_left;
    private ImageView iv_right;
    private List<String> contactsFromDB;

    @Override
    protected void handleMsg(Message msg) {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_add_friend);
        tv_title = (TextView) findViewById(R.id.tv_title);
        et_username = (EditText) findViewById(R.id.et_username);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        rl_nodata = (RelativeLayout) findViewById(R.id.rl_nodata);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        tv_title.setText(R.string.add_friend);
        tv_title.setVisibility(View.VISIBLE);

        iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_right.setVisibility(View.GONE);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_left.setOnClickListener(this);
        //给搜索按钮绑定监听事件
        iv_search.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        //获取当前用户的用户名
        currentUser = EMClient.getInstance().getCurrentUser();
        if (TextUtils.isEmpty(currentUser)){
            Toast.makeText(AddFriendActivity.this, R.string.empty_username, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoginActivity.class));
            finish();
            return;
        }
        //获取当前的好友

        contactsFromDB = DBUtils.getContactsFromDB(this, EMClient.getInstance().getCurrentUser());
    }

    private void search() {

        String username = et_username.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            Toast.makeText(AddFriendActivity.this, R.string.empty_username, Toast.LENGTH_SHORT).show();
            return;
        }
        //去Bmob云数据库上搜索数据
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        //搜素要求：以username开头，并且不用搜索自己的用户名
        bmobQuery.addWhereStartsWith("username",username);//搜素所有以username开头的用户
        bmobQuery.addWhereNotEqualTo("username",currentUser);//搜素结果中排除自己
        final ProgressDialog progressDialog = DialogUtils.makeProgressDialog(this);
        progressDialog.show();
        bmobQuery.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                progressDialog.dismiss();
                if (list==null||list.size()==0){
                    //把recyclerView异常
                    //把nodata界面显示出来
                    recyclerView.setVisibility(View.GONE);
                    rl_nodata.setVisibility(View.VISIBLE);
                    return;
                }
                recyclerView.setVisibility(View.VISIBLE);
                rl_nodata.setVisibility(View.GONE);

                recyclerView.setLayoutManager(new LinearLayoutManager(AddFriendActivity.this));

                SearchUserAdapter searchUserAdapter = new SearchUserAdapter(list,contactsFromDB);
                recyclerView.setAdapter(searchUserAdapter);

                searchUserAdapter.setOnItemButtonClickListener(new SearchUserAdapter.OnItemButtonClickListener() {
                    @Override
                    public void onItemButtonClick(User user) {
                        addFriend(user);
                    }
                });

            }

            @Override
            public void onError(int i, String s) {
                progressDialog.dismiss();
                Toast.makeText(AddFriendActivity.this, R.string.nodata, Toast.LENGTH_SHORT).show();
                //把recyclerView异常
                //把nodata界面显示出来
                recyclerView.setVisibility(View.GONE);
                rl_nodata.setVisibility(View.VISIBLE);
            }
        });




    }

    private void addFriend(final User user) {
        final ProgressDialog progressDialog = DialogUtils.makeProgressDialog(this);
        progressDialog.show();
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(user.getUsername(),"我是"+currentUser+",想加您为好友。");
                    //添加请求发送成功
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddFriendActivity.this, R.string.send_ok, Toast.LENGTH_SHORT).show();
                            }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //添加请求发送失败
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddFriendActivity.this, getResources().getString(R.string.add_friend_error)+",e="+e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }finally {
                    progressDialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_search:
                search();
                break;
            case R.id.iv_left:
                finish();
                break;
        }
    }


}
