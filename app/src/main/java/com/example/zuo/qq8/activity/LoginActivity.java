package com.example.zuo.qq8.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zuo.qq8.MainActivity;
import com.example.zuo.qq8.R;
import com.example.zuo.qq8.utils.DialogUtils;
import com.example.zuo.qq8.utils.MatcherUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class LoginActivity extends BaseActivity {


    private static final String TAG = "LoginActivity";
    private TextView tv_newUser;
    private EditText et_username;
    private EditText et_pwd;
    private Button btn_login;

    @Override
    protected void handleMsg(Message msg) {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);
        tv_newUser = (TextView) findViewById(R.id.tv_newuser);
        tv_newUser.setOnClickListener(this);
        et_username = (EditText) findViewById(R.id.et_username);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_login = (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        //数据回显
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        if (!TextUtils.isEmpty(username)){
            //1 .可能是从注册界面跳转过来的，intent会携带username数据
            et_username.setText(username);
        }else {
            //2. 可能之前就登录过，保存了用户名和密码
            //从sp中获取数据
            String usr = mSharedPreferences.getString(SP_KEY_USERNAME, "");
            String pwd = mSharedPreferences.getString(SP_KEY_PWD, "");
            et_username.setText(usr);
            et_pwd.setText(pwd);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_newuser:
                startActivity(new Intent(this, RegistActivity.class));
                finish();
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        final String username = et_username.getText().toString().trim();
        final String pwd = et_pwd.getText().toString().trim();
        if (!(MatcherUtils.isMatchUsername(username)&&MatcherUtils.isMatchPwd(pwd))){
            //验证失败
            Toast.makeText(LoginActivity.this, R.string.username_pwd_invali, Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = DialogUtils.makeProgressDialog(this);

        progressDialog.show();

        EMClient.getInstance().login(username, pwd, new EMCallBack() {
            @Override
            public void onSuccess() {
                //在子线程中执行的该方法
                Log.d(TAG, "onSuccess: threadName="+Thread.currentThread().getName());

               //跳转到MainActivity界面
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                //将登录成功的用户名和密码保存到sp

                mSharedPreferences.edit().putString(SP_KEY_USERNAME,username).putString(SP_KEY_PWD,pwd).commit();

                finish();

                progressDialog.dismiss();


              loadContactsAndConversations();

            }

            @Override
            public void onError(final int i, final String s) {
                //通过Toast告诉用户失败的原因
                mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, getString(R.string.login_error)+"i="+i+",s="+s, Toast.LENGTH_SHORT).show();
                        }
                });
                progressDialog.dismiss();
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });

    }

}
