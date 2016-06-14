package com.example.zuo.qq8.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zuo.qq8.R;
import com.example.zuo.qq8.entity.User;
import com.example.zuo.qq8.utils.DialogUtils;
import com.example.zuo.qq8.utils.MatcherUtils;
import com.example.zuo.qq8.utils.ThreadUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends BaseActivity implements TextView.OnEditorActionListener {

    private static final String TAG = "RegistActivity";
    private static final int REGIST_FAIL = 1;
    private static final int REGIST_OK = 2;
    private EditText et_pwd;
    private EditText et_username;
    private Button btn_regist;

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what){
            case REGIST_FAIL:
                HyphenateException e = (HyphenateException) msg.obj;
                Toast.makeText(RegistActivity.this, getResources().getString(R.string.regist_fail)+"e="+e.toString(), Toast.LENGTH_SHORT).show();
                break;
            case REGIST_OK:
                //跳转到登录界面
                Intent intent = new Intent(RegistActivity.this,LoginActivity.class);
                //把注册成功的用户名带过去
                intent.putExtra("username",msg.obj.toString());
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_regist);
        et_username = (EditText) findViewById(R.id.et_username);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_regist = (Button) findViewById(R.id.btn_regist);
        btn_regist.setOnClickListener(this);

        et_username.setOnEditorActionListener(this);
        et_pwd.setOnEditorActionListener(this);
    }

    @Override
    protected void initData() {

    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.et_username) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                String username = et_username.getText().toString().trim();
                //3-20位，字母开头，后面可以是字母数字下划线
                //校验用户名合法性，如果合法将焦点定位到密码框，否则依然定位带当前
                if (MatcherUtils.isMatchUsername(username)) {
                    et_pwd.requestFocus();//让密码输入框获取焦点
                } else {
                    Toast.makeText(RegistActivity.this, R.string.username_invalil, Toast.LENGTH_SHORT).show();
                    et_username.requestFocus();
                }
            }
        } else if (v.getId() == R.id.et_pwd) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String psw = et_pwd.getText().toString().trim();
                //对密码校验，然后执行注册的业务逻辑
                if (MatcherUtils.isMatchPwd(psw)) {
                    regist();
                } else {
                    Toast.makeText(RegistActivity.this, R.string.psw_invali, Toast.LENGTH_SHORT).show();
                    et_pwd.requestFocus();
                }
            }
        }

        return true;
    }

    private void regist() {
         //校验用户名和密码
        final String username = et_username.getText().toString().trim();
        final String pwd = et_pwd.getText().toString().trim();
        if (!(MatcherUtils.isMatchUsername(username)&&MatcherUtils.isMatchPwd(pwd))){
            Toast.makeText(RegistActivity.this, R.string.username_pwd_invali, Toast.LENGTH_SHORT).show();
            return;
        }
//        富血 贫血模式

        final User user = new User();
        user.setUsername(username);
        user.setPassword(pwd);

        //显示进度条对话框
        final ProgressDialog progressDialog = DialogUtils.makeProgressDialog(this);
        progressDialog.show();
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                // 然后注册环信后台
                ThreadUtils.runOnSubThread(new Runnable() {
                    @Override
                    public void run() {
                        //用户名，密码
                        try {
                            EMClient.getInstance().createAccount(username,pwd);

                            //代表环信注册成功了
                            mHandler.obtainMessage(REGIST_OK,username).sendToTarget();
                        } catch (HyphenateException e) {
                            //环信注册失败了
                            mHandler.obtainMessage(REGIST_FAIL,e).sendToTarget();
                            //删除Bmob云数据库上注册成功的数据，目的是为了保证注册的一致性
                            user.delete(RegistActivity.this);
                            e.printStackTrace();
                        }finally {
                            progressDialog.dismiss();
                        }
                    }
                });

            }

            @Override
            public void onFailure(int i, String s) {

                progressDialog.dismiss();

                Log.d(TAG, "onFailure: i="+i+",s="+s);
                Toast.makeText(RegistActivity.this,getResources().getString(R.string.regis_failure).replace("%%","i="+i+",s="+s), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist:
                regist();
                break;
        }
    }
}
