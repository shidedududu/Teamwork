package com.example.asd.testapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asd.models.project.ProjectEntity;
import com.example.asd.models.user.UserEntity;
import com.example.asd.netutils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Calendar;

import static com.example.asd.testapp.LoginActivity.SHOW_RESPONSE;
import static com.example.asd.testapp.LoginActivity.name;

public class AddProjectActivity extends AppCompatActivity {
    private SQLiteDatabase DB;
    private EditText projectname;
    private boolean isNoneOfInput=true;
    private EditText projectdescription;
    //private String deadline;
    //String deadline;
    TextView btn;
    //private EditText et;
    //用于处理和发送消息的Hander

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            //如果返现msg.what=SHOW_RESPONSE，则进行制定操作，如想进行其他操作，则在子线程里将SHOW_RESPONSE改变
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;


                    //进行UI操作，将结果显示到界面上
                    JsonElement je=new JsonParser().parse(response);
                    if(je.getAsJsonObject().get("result").getAsInt()==1) {
                        /*Intent intent = new Intent(AddProjectActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();*/
                        Toast.makeText(AddProjectActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddProjectActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(AddProjectActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                    }

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        btn = (TextView) findViewById(R.id.dateBtn);
        //et = (EditText) findViewById(R.id.et);
        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);

        btn.setOnClickListener(new View.OnClickListener() {
            Calendar c = Calendar.getInstance();

            @Override
            public void onClick(View v) {
                // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
                new DoubleDatePickerDialog(AddProjectActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet( DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                           int endDayOfMonth) {
                        String textString = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                        btn.setText(textString);
                        // deadline=textString;
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
            }
        });



        //获取添加界面中输入的数据
        projectname=(EditText)findViewById(R.id.project_name);
        btn=(TextView)findViewById(R.id.dateBtn);
        projectdescription=(EditText)findViewById(R.id.project_description);
        //按下添加按钮后对数据库进行操作
        FloatingActionButton addBtn=(FloatingActionButton)findViewById(R.id.add);
        addBtn.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          addProject();
                                         /* //dataSave();
                                          if(isNoneOfInput==false)
                                          {
                                              addProject();
                                              //返回projectActivity
                                             Intent intent = new Intent(AddProjectActivity.this,MainActivity.class);
                                             startActivity(intent);
                                             finish();
                                          }*/
                                      }
                                  }
        );
    }



    class ProjectParam{
        UserEntity user=new UserEntity();
        ProjectEntity project=new ProjectEntity();
    }
    //向服务器添加项目
    private void addProject(){
        String project=this.projectname.getText().toString();
        String deadline=this.btn.getText().toString();
        String description=this.projectdescription.getText().toString();
        if(project.trim().isEmpty()){
            Toast.makeText(this,"项目名称不能为空", Toast.LENGTH_SHORT).show();
            return ;
        }
        ProjectParam param=new ProjectParam();
        param.user.userName=name;
        param.project.projectName=project;
        param.project.proDescription=description;
        param.project.deadline=deadline;
        Gson gson=new Gson();
        String data=gson.toJson(param);
        HttpUtil.sendRequestWithHttpURLConnection(handler,"project/addProject",data,SHOW_RESPONSE);
        this.projectname.setText("");
        this.btn.setText("");
        this.projectdescription.setText("");
    }
}






