package com.example.zuo.qq8.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.zuo.qq8.MainActivity;
import com.example.zuo.qq8.R;
import com.hyphenate.chat.EMClient;

public class SplashActivity extends AppCompatActivity {
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断是否已经登录，如果已经登录了，则直接进入主界面，否则等2s进入登录界面
       if ( EMClient.getInstance().isLoggedInBefore()&&EMClient.getInstance().isConnected()){
           startActivity(new Intent(this, MainActivity.class));
           finish();
           return;
       }else {
           setContentView(R.layout.activity_splash);
           //等2s进入登录界面
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                   finish();
               }
           },2000);
       }


    }
}
