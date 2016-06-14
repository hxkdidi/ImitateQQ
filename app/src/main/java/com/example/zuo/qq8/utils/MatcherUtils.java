package com.example.zuo.qq8.utils;

import android.text.TextUtils;

/**
 * Created by taojin on 2016/6/7.11:30
 */
public class MatcherUtils {

    public static  boolean isMatchUsername(String username){
        if (TextUtils.isEmpty(username)){
            return  false;
        }
        return username.matches("^[a-zA-Z]\\w{2,19}$");
    }

    public static boolean isMatchPwd(String pwd){
        if (TextUtils.isEmpty(pwd)){
            return  false;
        }
        return pwd.matches("^[0-9]{3,20}$");
    }

}
