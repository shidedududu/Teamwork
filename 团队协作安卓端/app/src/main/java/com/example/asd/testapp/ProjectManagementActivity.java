package com.example.asd.testapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asd.models.project.ProjectEntity;
import com.example.asd.models.user.UserEntity;
import com.example.asd.netutils.HttpUtil;
import com.example.asd.sqlutils.ProjectSqlUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.util.Calendar;

import static com.example.asd.sqlutils.ProjectSqlUtil.select;
import static com.example.asd.sqlutils.TaskSqlUtil.selectByProID;
import static com.example.asd.testapp.LoginActivity.SHOW_RESPONSE;
import static com.example.asd.testapp.LoginActivity.name;

public class ProjectManagementActivity extends AppCompatActivity {
    private EditText ProjectName;
    private TextView Deadline;
    private EditText ProjectDescription;
    String projectName;
    String deadline;
    String projectDescription;
    String ProID;
    FloatingActionButton alterprojectName;
    FloatingActionButton alterDeadline;
    FloatingActionButton alterprojectDescription;
    FloatingActionButton updateData;
    Button delete;
    Button memberManagement;
    private final static int ALTER_PROJECT=1;
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
                        Toast.makeText(ProjectManagementActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProjectManagementActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(ProjectManagementActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                    }break;
                case ALTER_PROJECT:
                    String response1=(String)msg.obj;
                    //进行UI操作，将结果显示到界面上
                    JsonElement j=new JsonParser().parse(response1);
                    if(j.getAsJsonObject().get("result").getAsInt()==1) {
                        Toast.makeText(ProjectManagementActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ProjectManagementActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                    }break;

            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_management);
        //控件一一对应
        ProjectName=(EditText)findViewById(R.id.project_name);

        ProjectName.setCursorVisible(false);

        ProjectName.setFocusable(false);

        ProjectName.setFocusableInTouchMode(false);



        Deadline=(TextView)findViewById(R.id.dateBtn);

        ProjectDescription=(EditText)findViewById(R.id.project_description);

        //EditText鍙偣鍑讳笉鍙紪杈?
        ProjectDescription.setCursorVisible(false);

        ProjectDescription.setFocusable(false);

        ProjectDescription.setFocusableInTouchMode(false);

        //EditText鍙偣鍑讳笉鍙紪杈?
        alterprojectName=(FloatingActionButton)findViewById(R.id.alterProname);

        alterDeadline=(FloatingActionButton)findViewById(R.id.alterDeadline);

        alterprojectDescription=(FloatingActionButton)findViewById(R.id.alterProdescription);

        updateData=(FloatingActionButton)findViewById(R.id.dataSave);

        delete=(Button)findViewById(R.id.delete);

        memberManagement=(Button)findViewById(R.id.memberManagemrnt);


        //显示项目信息
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        ProID=bundle.getString("ProID");
        Cursor cursor= ProjectSqlUtil.select(ProID);
        if(cursor.getCount()==0)
            cursor=ProjectSqlUtil.selectFromOtherById(ProID);
        while(cursor.moveToNext()) {
            ProjectName.setText(cursor.getString(cursor.getColumnIndex("ProjectName")));
            Deadline.setText(cursor.getString(cursor.getColumnIndex("DeadlineTime")));
            ProjectDescription.setText(cursor.getString(cursor.getColumnIndex("ProjectDescription")));
        }
        //点击删除项目
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                //删除项目
                AlertDialog dialog = new AlertDialog.Builder(ProjectManagementActivity.this)
                        .setTitle("")
                        .setMessage("确定要删除吗")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProject();
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        //点击进入成员管理界面
        memberManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent intent=new Intent(ProjectManagementActivity.this,MemberActivity.class);
                intent.putExtra("ProID",ProID);
                startActivity(intent);
            }
        });

        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                String newProjectName = ProjectName.getText().toString();
                String newDeadline = Deadline.getText().toString();
                String newProjectDescription=ProjectDescription.getText().toString();
                updateProject(newProjectName,newDeadline,newProjectDescription);
            }
        });

        alterprojectName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                ProjectName.setFocusable(true);
                ProjectName.setCursorVisible(true);
                ProjectName.setFocusableInTouchMode(true);
                ProjectName.requestFocus();
            }
        });

        alterDeadline.setOnClickListener(new View.OnClickListener() {
            Calendar c = Calendar.getInstance();
            @Override
            public void onClick( View v ) {
                new DoubleDatePickerDialog(ProjectManagementActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker endDatePicker, int endYear, int endMonthOfYear, int endDayOfMonth) {
                        String textString = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                        Deadline.setText(textString);
                    }

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();

            }

        });



        alterprojectDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                ProjectDescription.setFocusable(true);
                ProjectDescription.setCursorVisible(true);
                ProjectDescription.setFocusableInTouchMode(true);
                ProjectDescription.requestFocus();
            }
        });
    }

    class DeleteProjectParam{
        UserEntity user;
        ProjectEntity project;
    }
    private  void updateProject(String newName,String deadline,String description){
        DeleteProjectParam param=new DeleteProjectParam();
        UserEntity user=new UserEntity();
        user.userName=name;
        ProjectEntity project=new ProjectEntity();
        project.id=Integer.valueOf(ProID);
        project.projectName=newName;
        project.deadline=deadline;
        project.proDescription=description;
        param.user=user;
        param.project=project;
        String data=gson.toJson(param);
        HttpUtil.sendRequestWithHttpURLConnection(handler,"project/updateProject",data,ALTER_PROJECT);
    }

    private void deleteProject(){
        DeleteProjectParam param=new DeleteProjectParam();
        UserEntity user=new UserEntity();
        user.userName=name;
        ProjectEntity project=new ProjectEntity();
        project.id=Integer.valueOf(ProID);
        param.user=user;
        param.project=project;
        String data=gson.toJson(param);
        HttpUtil.sendRequestWithHttpURLConnection(handler,"project/deleteProject",data,SHOW_RESPONSE);
    }
}
