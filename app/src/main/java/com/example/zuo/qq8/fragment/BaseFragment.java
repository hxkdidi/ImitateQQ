package com.example.zuo.qq8.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zuo.qq8.R;

/**
 * Created by taojin on 2016/6/7.16:11
 */
public abstract  class BaseFragment extends Fragment implements View.OnClickListener {

    protected ImageView ivLeft;
    protected ImageView ivRight;
    protected TextView tvTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater, container, savedInstanceState);
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ivLeft = (ImageView) getView().findViewById(R.id.iv_left);
        ivRight = (ImageView) getView().findViewById(R.id.iv_right);
        tvTitle = (TextView) getView().findViewById(R.id.tv_title);
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);

        initData(getView());
    }

    protected abstract void initData(View view);


}
