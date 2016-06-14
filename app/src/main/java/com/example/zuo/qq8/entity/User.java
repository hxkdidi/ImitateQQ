package com.example.zuo.qq8.entity;

import cn.bmob.v3.BmobUser;

/**
 * Created by taojin on 2016/6/7.14:36
 */
public class User extends BmobUser {

    @Override
    public String toString() {
        return getUsername().toString();
    }
}
