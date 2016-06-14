package com.example.zuo.qq8.adapter;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.zuo.qq8.R;
import com.example.zuo.qq8.utils.ContactsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taojin on 2016/6/8.15:13
 */
public class ContactAdapter extends BaseAdapter implements SectionIndexer{
    //用户名 zhansan
    private List<String> data;
    private SparseIntArray positionOfSection = new SparseIntArray();

    public ContactAdapter(List<String> data){
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact,parent,false);
            viewHolder = new ViewHolder();

            viewHolder.tvSection = (TextView) convertView.findViewById(R.id.tv_section);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tv_username);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String username = data.get(position);
        String initialChar = ContactsUtils.getInitialChar(username);

        viewHolder.tvUsername.setText(username);
        if (position==0){
            viewHolder.tvSection.setText(initialChar);
            viewHolder.tvSection.setVisibility(View.VISIBLE);
        }else {
            String preUsr = data.get(position - 1);
            String preInitial = ContactsUtils.getInitialChar(preUsr);
            if (preInitial.equals(initialChar)){
                viewHolder.tvSection.setVisibility(View.GONE);
            }else {
                viewHolder.tvSection.setVisibility(View.VISIBLE);
                viewHolder.tvSection.setText(initialChar);
            }
        }



        return convertView;
    }

    static class ViewHolder{
        TextView tvSection;
        TextView tvUsername;
    }



    //获取当前的数据总共分多少个section
    @Override
    public String[] getSections() {
       //key section value positon
        //用于存储所有的section
        List<String> sections = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            String username = data.get(i);
            //获取到username的首字母 张三 Z abc a
            String initialChar = ContactsUtils.getInitialChar(username).toUpperCase();

            if (!sections.contains(initialChar)){
                sections.add(initialChar);
                //将section和postion放进来
                //key是section的索引
                //value是item的脚标
                positionOfSection.put(sections.size()-1,i);//0=0  1=2
            }

        }

        return sections.toArray(new String[sections.size()]);
    }
    //你给我传一个section的脚标，我还你一个该section下第一个条目的脚标
    @Override
    public int getPositionForSection(int sectionIndex) {
//        key  section
//        value postion
//        HashMap<Integer,Integer>

//        SparseIntArray

        return positionOfSection.get(sectionIndex);
    }

    //你给我传一个条目的postion的脚标，我还你一个该该postion所在的section的脚标
    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}
