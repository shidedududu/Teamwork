package com.example.asd.testapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author chenzheng_java
 * @description 该类的部分实现模仿了SimpleAdapter
 */
public class MyAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, Object>> data;
    /**
     * LayoutInflater 类是代码实现中获取布局文件的主要形式
     *LayoutInflater layoutInflater = LayoutInflater.from(context);
     *View convertView = layoutInflater.inflate();
     *LayoutInflater的使用,在实际开发种LayoutInflater这个类还是非常有用的,它的作用类似于 findViewById(),
     不同点是LayoutInflater是用来找layout下xml布局文件，并且实例化！
     而findViewById()是找具体xml下的具体 widget控件(如:Button,TextView等)。
     */
    private LayoutInflater layoutInflater;
    private Context context;


    public MyAdapter(Context context, ArrayList<HashMap<String, Object>> data) {

        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     *获取列数
     */
    public int getCount() {
        return data.size();
    }
    /**
     *获取某一位置的数据
     */
    public Object getItem(int position) {
        return data.get(position);
    }
    /**
     *获取唯一标识
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * android绘制每一列的时候，都会调用这个方法
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ZuJian zuJian = null;
        if(convertView==null){
            zuJian = new ZuJian();
            // 获取组件布局
            convertView = layoutInflater.inflate(R.layout.task_list_item, null);
            zuJian.tv_id = (TextView) convertView.findViewById(R.id.tv_id);
            zuJian.title = (TextView) convertView.findViewById(R.id.title);
            zuJian.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            zuJian.percentageRing = (PercentageRing) convertView.findViewById(R.id.Completion);
            // 这里要注意，是使用的tag来存储数据的。
            convertView.setTag(zuJian);
        }
        else {
            zuJian = (ZuJian) convertView.getTag();
        }
        // 绑定数据、以及事件触发
        zuJian.tv_id.setText((String)data.get(position).get("tv_id"));
        zuJian.title.setText((String)data.get(position).get("title"));
        zuJian.subtitle.setText((String)data.get(position).get("subtitle"));
        String completion=(String)data.get(position).get("percentageRing");
        double temp=Double.valueOf(completion);
        int temp1=(int)temp;
        zuJian.percentageRing.setTargetPercent(temp1);
        return convertView;
    }

    /**
     *当用户点击按钮时触发的事件，会弹出一个确认对话框
     */
    public void showInfo(){

        new AlertDialog.Builder(context)

                .setTitle("我的listview")

                .setMessage("介绍...")

                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }

                })

                .show();



    }
       class ZuJian {

        public TextView tv_id;
        public TextView title;
        public TextView subtitle;
        public PercentageRing percentageRing;


    }

}
