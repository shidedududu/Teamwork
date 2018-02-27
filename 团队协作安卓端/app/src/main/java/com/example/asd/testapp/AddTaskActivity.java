package com.example.asd.testapp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asd.models.project.ProjectEntity;
import com.example.asd.models.task.TaskEntity;
import com.example.asd.models.user.UserEntity;
import com.example.asd.netutils.HttpUtil;
import com.example.asd.sqlutils.ProjectSqlUtil;
import com.example.asd.sqlutils.TaskSqlUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Calendar;

import static com.example.asd.sqlutils.ProjectSqlUtil.select;
import static com.example.asd.testapp.LoginActivity.SHOW_RESPONSE;
import static com.example.asd.testapp.LoginActivity.name;

public class AddTaskActivity extends AppCompatActivity {
    private SQLiteDatabase DB;
    private EditText taskname;
   // private LinearLayout projectnameLayout;
    TextView projectName;
    private boolean isNoneOfInput=true;
    private EditText taskdescription;
    TextView btn;
    String proID;
    String proName;
    Cursor cursor;
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
                        Toast.makeText(AddTaskActivity.this,"添加任务成功",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(AddTaskActivity.this,TaskActivity.class);
                        intent.putExtra("ProID",proID);
                        intent.putExtra("Proname",proName);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(AddTaskActivity.this,"添加任务失败",Toast.LENGTH_SHORT).show();
                    }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        btn = (TextView) findViewById(R.id.dateBtn);
        taskname=(EditText)findViewById(R.id.task_name);
        taskdescription=(EditText)findViewById(R.id.project_description);
        projectName=(TextView)findViewById(R.id.projectName);
        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);
        // proNameView=(ListView) findViewById(R.id.proNameList);


        //获得任务截止日期
        btn.setOnClickListener(new View.OnClickListener() {
            Calendar c = Calendar.getInstance();
            //任务截止日期
            @Override
            public void onClick(View v) {
                // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
                new DoubleDatePickerDialog(AddTaskActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth) {
                        String textString = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                        btn.setText(textString);
                        // deadline=textString;
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
            }
        });

        //点击所属项目进入dialog

       // replaceList();
       //AlertDialog 绑定
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null) {
                proID = bundle.getString("ProID");
                Cursor cursor = ProjectSqlUtil.select(proID);
                cursor.moveToFirst();
                projectName.setText(cursor.getString(cursor.getColumnIndex("ProjectName")));
        }
        projectName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(AddTaskActivity.this);
                final View view1 = factory.inflate(R.layout.proname_picker_dialog, null);
                ListView proname_picker= (ListView) view1.findViewById(R.id.proname_picker);
                Cursor cursor= TaskSqlUtil.select();
               /* */

               // List<Map<String, String>> nameList = new ArrayList<>();//建立一个数组存储listview上显示的数据
                //cursor=select();

                SimpleCursorAdapter adapter = new SimpleCursorAdapter(AddTaskActivity.this,
                        R.layout.proname_value_item, cursor, new String[]{"_id","ProjectName",},
                        new int[]{R.id.pro_id, R.id.proname},
                        SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                proname_picker.setAdapter(adapter);

                final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this)
                        .setTitle("选择所属项目").setView(view1)//在这里把写好的这个listview的布局加载dialog中
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                        TextView tvProname = (TextView) arg1 .findViewById(R.id.proname);//取得每条item中的textview控件
                        TextView tvID=(TextView) arg1 .findViewById(R.id.pro_id);
                        proID=tvID.getText().toString();
                        proName=tvProname.getText().toString();
                        projectName.setText(proName);
                        dialog.cancel();
                    }
                });
            }
        });

        //按下添加按钮后对数据库进行操作
        FloatingActionButton addBtn=(FloatingActionButton)findViewById(R.id.add);
       addBtn.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          addTask();
                                      }
                                  }
        );
    }

    class TaskAddRequestParam{
        UserEntity user;
        ProjectEntity project;
        TaskEntity task;
    }
    public void addTask(){
        String task = this.taskname.getText().toString();
        String deadline = this.btn.getText().toString();
        String description = this.taskdescription.getText().toString();
        if (task.trim().isEmpty()) {
            Toast.makeText(this, "任务名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        TaskAddRequestParam param=new TaskAddRequestParam();
        UserEntity us=new UserEntity();
        us.setUserName(name);
        ProjectEntity pro=new ProjectEntity();
        pro.id=Integer.valueOf(proID);
        TaskEntity t=new TaskEntity();
        t.taskName=task;
        t.deadline=deadline;
        t.taskDesciption=description;
        param.user=us;
        param.project=pro;
        param.task=t;
        HttpUtil.sendRequestWithHttpURLConnection(handler,"task/addTask",gson.toJson(param),SHOW_RESPONSE);
        //清空输入栏
        this.taskname.setText("");
        this.btn.setText("");
        this.projectName.setText("");
        this.taskdescription.setText("");
    }
}
