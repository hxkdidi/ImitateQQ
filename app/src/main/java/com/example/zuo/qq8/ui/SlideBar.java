package com.example.zuo.qq8.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.zuo.qq8.R;
import com.hyphenate.util.DensityUtil;

/**
 * Created by taojin on 2016/6/8.14:41
 */
public class SlideBar extends View {

    private String[] sections = {getContext().getString(R.string.sou),"A","B","C","D","E","F","G","H","I","J"
    ,"K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private Paint paint;
    private int height;
    private TextView tv_float;

    public SlideBar(Context context) {
        super(context);
        initView();
    }
    public SlideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SlideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlideBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#9c9c9c"));
        paint.setTextAlign(Paint.Align.CENTER);//文字居中
        paint.setTextSize(DensityUtil.sp2px(getContext(),10));//10sp
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //设置背景为灰色
                setBackgroundResource(R.drawable.slide_bar_pre);
                if (tv_float==null){
                    ViewGroup viewGroup = (ViewGroup) getParent();
                    tv_float = (TextView) viewGroup.findViewById(R.id.tv_float);
                }
                tv_float.setVisibility(VISIBLE);
                //获取一下按下去的是哪个section
                //显示float TextView，同时让ListView定位到指定的位置
                setHeaderAndScroll(event);

                break;
            case MotionEvent.ACTION_MOVE:
                //获取一下move到的的是哪个section
                //显示float TextView，同时让ListView定位到指定的位置
                setHeaderAndScroll(event);
                break;
            case MotionEvent.ACTION_UP:
                //设置背景为透明
                setBackgroundColor(Color.TRANSPARENT);
                //隐藏float TextView
                tv_float.setVisibility(GONE);
                break;
        }

        return  true;
    }

    private ListView mListView;
    public void setListView(ListView listView){
        this.mListView = listView;
    }

    private void setHeaderAndScroll(MotionEvent event) {
        String section = getSection(event);
        tv_float.setText(section);
        if (mListView==null){
                return;
        }
        //根据section找到ListView的该section下的第一个条目的position
        ListAdapter adapter = mListView.getAdapter();

        if (adapter!=null && (adapter instanceof SectionIndexer)){
            SectionIndexer sectionIndexer = (SectionIndexer) adapter;
            //与当前关联的ListView中的 数据总共分了这么多Section
            String[] sections = (String[]) sectionIndexer.getSections();
            //判断当前用户点击到的section在sections中有没有，有的话section的脚标是多少
            for (int i = 0; i < sections.length; i++) {
                if (section.equals(sections[i])){
                    //说明：当前用户点击到的section在sections中是存在的，并且脚标是i

                    //根据section的索引获取该section下面item的索引
                    int position = sectionIndexer.getPositionForSection(i);
                    //将ListView滚动到该postion
                    mListView.setSelection(position);
                    break;
                }
            }

        }

    }



    private String getSection(MotionEvent event) {
        int index = (int) (event.getY() / height);
        if (index<0){
            index =0;
        }else if (index>sections.length-1){
            index = sections.length- 1;
        }

        return sections[index];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //获取SlideBar的宽度
        int x = getWidth() / 2;
        height = getHeight() / sections.length;

        for (int i = 0; i < sections.length; i++) {
            String section = sections[i];
            canvas.drawText(section,x, height *(i+1),paint);
        }

    }
}
