package com.example.asd.testapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asd.models.subtask.SubTaskEntity;
import com.example.asd.models.task.TaskEntity;
import com.example.asd.netutils.HttpUtil;
import com.example.asd.sqlutils.SubTaskUtil;
import com.example.asd.sqlutils.TaskSqlUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import static com.example.asd.testapp.LoginActivity.SHOW_RESPONSE;

public class AddSubtaskActivity extends AppCompatActivity {
    private SQLiteDatabase DB;
    private EditText subtaskname;
    // private LinearLayout projectnameLayout;
    TextView taskname;
    private boolean isNoneOfInput=true;
    String taskID;
    String taskName;
    Gson gson=new Gson();

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
                        Intent intent = new Intent(AddSubtaskActivity.this,SubtaskActivity .class);
                        intent.putExtra("TaskID",taskID);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(AddSubtaskActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                    }

            }
        }
    };
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_task);
        subtaskname=(EditText)findViewById(R.id.subtask_name);
        taskname=(TextView)findViewById(R.id.taskName);
        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);
        //AlertDialog 绑定
        taskname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(AddSubtaskActivity.this);
                final View view1 = factory.inflate(R.layout.proname_picker_dialog, null);
                ListView proname_picker= (ListView) view1.findViewById(R.id.proname_picker);

                Intent intent=getIntent();
                Bundle bundle=intent.getExtras();
                String ProID=bundle.getString("ProID");
                Cursor cursor= SubTaskUtil.select(ProID);

                // List<Map<String, String>> nameList = new ArrayList<>();//建立一个数组存储listview上显示的数据
                //cursor=select();

                SimpleCursorAdapter adapter = new SimpleCursorAdapter(AddSubtaskActivity.this,
                        R.layout.proname_value_item, cursor, new String[]{"_id","TaskName",},
                        new int[]{R.id.pro_id, R.id.proname},//获取任务id和任务名
                        SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                proname_picker.setAdapter(adapter);

                final AlertDialog dialog = new AlertDialog.Builder(AddSubtaskActivity.this)
                        .setTitle("选择所属任务").setView(view1)//在这里把写好的这个listview的布局加载dialog中
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
                                dialog.cancel();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
                dialog.show();
                //listview点击事件
                proname_picker.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {
                        //获取ProID和ProName
                        TextView tvTaskname = (TextView) arg1 .findViewById(R.id.proname);//取得每条item中的textview控件
                        TextView tvID=(TextView) arg1 .findViewById(R.id.pro_id);
                        taskID=tvID.getText().toString();
                        taskName=tvTaskname.getText().toString();
                        taskname.setText(taskName);
                        dialog.cancel();
                    }
                });
            }
        });



        //按下添加按钮后对数据库进行操作
        FloatingActionButton addBtn=(FloatingActionButton)findViewById(R.id.add);
        addBtn.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          addSubTask();

                                      }
                                  }
        );
    }

    class SubTaskBean{
        TaskEntity task;
        SubTaskEntity subTask;
    }
    public void addSubTask() {
        String subtask = this.subtaskname.getText().toString();
        String taskName=this.taskname.getText().toString();
        if (subtask.trim().isEmpty()) {
            Toast.makeText(this, "子任务名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (taskName.trim().isEmpty()) {
            Toast.makeText(this, "子任务所属任务不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        SubTaskBean stb=new SubTaskBean();
        SubTaskEntity st=new SubTaskEntity();
        st.subTaskName=subtask;
        TaskEntity t=new TaskEntity();
        t.id=Integer.valueOf(taskID);
        stb.subTask=st;
        stb.task=t;
        //清空输入栏
        String data=gson.toJson(stb);
        HttpUtil.sendRequestWithHttpURLConnection(handler,"subtask/addSubtask",data,SHOW_RESPONSE);
        this.subtaskname.setText("");
        this.taskname.setText("");
    }
}
