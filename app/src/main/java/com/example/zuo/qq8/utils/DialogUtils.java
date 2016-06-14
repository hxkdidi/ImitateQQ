package com.example.zuo.qq8.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.zuo.qq8.R;

/**
 * Created by taojin on 2016/6/7.15:26
 */
public class DialogUtils {

    public static ProgressDialog makeProgressDialog(Context context,String title,String msg){

        ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setIcon(R.mipmap.avatar3);

        progressDialog.setTitle(title);

        progressDialog.setMessage(msg);

        return  progressDialog;

    }

    public static ProgressDialog makeProgressDialog(Context context){
        return makeProgressDialog(context,context.getString(R.string.loading),context.getString(R.string.please_waiting));
        
    }

}
