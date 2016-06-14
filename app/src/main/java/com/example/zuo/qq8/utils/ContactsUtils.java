package com.example.zuo.qq8.utils;

import android.text.TextUtils;

import com.hyphenate.util.HanziToPinyin;

import java.util.ArrayList;

/**
 * Created by taojin on 2016/6/8.15:22
 */
public class ContactsUtils {

    public static String getInitialChar(String username){

        if (TextUtils.isEmpty(username)){
            return  "";
        }

        //张三  ZHANG     SAN
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(username);
        String result = "";
        if (tokens!=null&&tokens.size()>0){
            for (int i = 0; i < tokens.size(); i++) {
                String a = tokens.get(i).target;

            }
            HanziToPinyin.Token token = tokens.get(0);
            result = token.target;//ZHANG
        }

        return result.substring(0,1);

    }

}

