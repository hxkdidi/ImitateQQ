package com.example.zuo.qq8.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.zuo.qq8.R;

/**
 * Created by taojin on 2016/6/8.14:34
 */
public class ContactListView extends RelativeLayout {

    private ListView lv_contacts;
    private SlideBar slideBar;

    public ContactListView(Context context) {
        super(context);
        initView(context);
    }

    public ContactListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ContactListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ContactListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.contact_view,this,true);
        lv_contacts = (ListView) findViewById(R.id.lv_contacts);
        slideBar = (SlideBar) findViewById(R.id.slideBar);

        slideBar.setListView(lv_contacts);
    }

    public void setAdapter(ListAdapter adapter){
        lv_contacts.setAdapter(adapter);
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener){
        lv_contacts.setOnItemLongClickListener(onItemLongClickListener);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener){
        lv_contacts.setOnItemClickListener(onItemClickListener);
    }


}
