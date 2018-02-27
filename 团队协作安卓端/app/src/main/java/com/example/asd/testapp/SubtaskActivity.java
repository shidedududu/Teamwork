package com.example.asd.testapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asd.models.subtask.SubTaskEntity;
import com.example.asd.models.task.TaskEntity;
import com.example.asd.netutils.HttpUtil;
import com.example.asd.sqlutils.SubTaskUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.asd.testapp.LoginActivity.SHOW_RESPONSE;

public class SubtaskActivity extends AppCompatActivity {
    String TaskID;
    String TaskName;
    private ListView subtask_list;
    private SQLiteDatabase DB;
    private List<String> mList = new ArrayList<>();
    String IsFinished;
    TextView taskname;
    TextView Isfinished;
    Gson gson=new Gson();
    private ToggleButton toggleButton=null;
    public final static int SET_FINISHED=1;

    class Response{
        String result;
        List<SubTaskEntity> data;
    }
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            //如果返现msg.what=SHOW_RESPONSE，则进行制定操作，如想进行其他操作，则在子线程里将SHOW_RESPONSE改变
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;
                    //进行UI操作，将结果显示到界面上
                    JsonElement je=new JsonParser().parse(response);
                    if(je.getAsJsonObject().get("result").getAsInt()==1) {
                        //返回子任务界面
                        Response r=gson.fromJson(response,Response.class);
                        SubTaskUtil.refreshTableSubTask(r.data,TaskID);
                        replaceList();

                    }
                    else{
                        Toast.makeText(SubtaskActivity.this,"刷新失败",Toast.LENGTH_SHORT).show();
                    }break;
                case SET_FINISHED:
                    String response1=(String)msg.obj;
                    //进行UI操作，将结果显示到界面上
                    JsonElement j=new JsonParser().parse(response1);
                    if(j.getAsJsonObject().get("result").getAsInt()==1) {
                        //返回子任务界面
                        Toast.makeText(SubtaskActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                        getSubtasks();
                    }else
                        Toast.makeText(SubtaskActivity.this,"设置失败",Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtask);
        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);
        subtask_list=(ListView)findViewById(R.id.subtask_list);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        TaskName=bundle.getString("Taskname");
        taskname =(TextView)findViewById(R.id.taskname);
        toggleButton=(ToggleButton)findViewById(R.id.togglebutton);
        Isfinished=(TextView)findViewById(R.id.subtitle);
        taskname.setText(TaskName);
        //刷新任务列表
        getSubtasks();
        ListView mListView = (ListView) findViewById(R.id.task_list);
        final SwipeRefreshLayout mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_ly);
        //设置在listview上下拉刷新的监听
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSubtasks();
                //replaceList();
                mSwipeLayout.setRefreshing(false);

            }
        });
    }
    private List<HashMap<String,Object>> getData(){
        List<HashMap<String,Object>> list=new ArrayList<>();
        Cursor cursor= SubTaskUtil.selectByTaskID(TaskID);
        if(cursor!=null){
            while(cursor.moveToNext()){
                HashMap<String,Object> tempMap=new HashMap<>();
                tempMap.put("id",cursor.getString(cursor.getColumnIndex("_id")));
                tempMap.put("name",cursor.getString(cursor.getColumnIndex("SubTaskName")));
                tempMap.put("isfinished",cursor.getString(cursor.getColumnIndex("IsFinished")));
                list.add(tempMap);
            }
        }
        return list;
    }

    public void getSubtasks(){
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        TaskID=bundle.getString("TaskID");
        TaskEntity task=new TaskEntity();
        task.id=Integer.valueOf(TaskID);
        String data=gson.toJson(task);
        HttpUtil.sendRequestWithHttpURLConnection(handler,"subtask/getSubtasks",data,SHOW_RESPONSE);
    }

    /*public void setSubTaskFinished(String data){
        HttpUtil.sendRequestWithHttpURLConnection(handler,"subtask/finishSubtask",data,)
    }*/

    public void replaceList(){
        //获取TaskID
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        TaskID=bundle.getString("TaskID");
        /*Cursor cursor= SubTaskUtil.selectByTaskID(TaskID);
        SimpleCursorAdapter adapter=new SimpleCursorAdapter
                (this,R.layout.subtask_list_item,cursor,new String[]{"_id","SubTaskName","IsFinished"},
                        new int[]{R.id.tv_id,R.id.title,R.id.subtitle},
                        SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        subtask_list.setAdapter(adapter);*/
        MyAdapter_Subtask adapter=new MyAdapter_Subtask(this,getData());
        subtask_list.setAdapter(adapter);
    }

    public class MyAdapter_Subtask extends BaseAdapter {
        private Context mContext;
        private List<HashMap<String,Object>> data = new ArrayList<>();
        private LayoutInflater mlayoutInflater;
        Gson gson=new Gson();
        public MyAdapter_Subtask(Context context, List<HashMap<String,Object>> list) {
            mContext = context;
            data = list;
            mlayoutInflater=LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem( int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.subtask_list_item, null);
                viewHolder.subTaskId=(TextView)view.findViewById(R.id.tv_id);
                viewHolder.subTaskName = (TextView) view.findViewById(R.id.title);
                viewHolder.isFinished=(TextView)view.findViewById(R.id.subtitle);
                viewHolder.mButton = (ToggleButton) view.findViewById(R.id.togglebutton);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.subTaskId.setText((String)data.get(i).get("id"));
            final int id=Integer.valueOf((String)data.get(i).get("id"));
            viewHolder.subTaskName.setText((String)data.get(i).get("name"));
            viewHolder.isFinished.setText((String)data.get(i).get("isfinished"));
            String temp=(String)data.get(i).get("isfinished");
            if(Integer.valueOf(temp)==0){
                viewHolder.mButton.setToggleOff();
            }
            else
                viewHolder.mButton.setToggleOn();
            viewHolder.mButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                @Override
                public void onToggle(boolean on) {
                    SubTaskEntity s=new SubTaskEntity();
                    s.id=id;
                    if(on==true){
                        s.isFinished=(short)1;
                    }else{
                        s.isFinished=(short)0;
                    }
                    String data=gson.toJson(s);
                    HttpUtil.sendRequestWithHttpURLConnection(handler,"subtask/finishSubtask",data,SET_FINISHED);
                }
            });
            return view;
        }

        /**
         * 删除按钮的监听接口
         */

        /*private com.example.asd.testapp.MyAdapter_Subtask.onItemDeleteListener mOnItemDeleteListener;

        public void setOnItemDeleteClickListener(com.example.asd.testapp.MyAdapter_Subtask.onItemDeleteListener mOnItemDeleteListener) {
            this.mOnItemDeleteListener = mOnItemDeleteListener;
        }*/

        class ViewHolder {
            TextView subTaskId;
            TextView subTaskName;
            TextView isFinished;
            ToggleButton mButton;
        }

    }
}
