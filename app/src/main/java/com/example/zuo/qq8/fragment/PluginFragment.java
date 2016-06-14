package com.example.zuo.qq8.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.zuo.qq8.R;
import com.example.zuo.qq8.activity.LoginActivity;
import com.example.zuo.qq8.utils.DialogUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class PluginFragment extends BaseFragment {

    private Button btn_logout;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plugin, container, false);
    }

    @Override
    protected void initData(View view) {
        ivLeft.setVisibility(View.GONE);
        tvTitle.setText(R.string.dongtai);
        tvTitle.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.GONE);

        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);

        //数据回显
        //获取已经登录成功的用户
        String currentUser = EMClient.getInstance().getCurrentUser();
        if (!TextUtils.isEmpty(currentUser)) {
            btn_logout.setText(getResources().getString(R.string.logout_exit).replace("%%",currentUser));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                logout();
                break;
        }
    }

    //退出登录
    private void logout() {

        final ProgressDialog progressDialog = DialogUtils.makeProgressDialog(getActivity());
        progressDialog.show();
        //参数1:代表解除跟设备的绑定，然后登出
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                //跳转到登录界面
                startActivity(new Intent(getActivity(), LoginActivity.class));
                //销毁MainActivity
                getActivity().finish();
            }

            @Override
            public void onError(final int i, final String s) {
                progressDialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getString(R.string.logout_error)+",i="+i+",s="+s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });

    }
}
