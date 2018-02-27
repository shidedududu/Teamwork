package com.example.asd.testapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asd.models.project.ProjectEntity;
import com.example.asd.models.user.UserEntity;
import com.example.asd.netutils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.jar.Attributes;

import static com.example.asd.testapp.LoginActivity.SHOW_RESPONSE;

public class AddMemberActivity extends AppCompatActivity {
    private EditText name;
    private TextView Member;
    private  TextView result;
    Button addMember;
    String userName;
    String Result;
    FloatingActionButton search;
    Gson gson=new Gson();
    private static final int ADD_MEMBER=2;

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            //如果返现msg.what=SHOW_RESPONSE，则进行制定操作，如想进行其他操作，则在子线程里将SHOW_RESPONSE改变
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;
                    //进行UI操作，将结果显示到界面上
                    JsonElement je=new JsonParser().parse(response);
                    if(je.getAsJsonObject().get("result").getAsInt()==1) {
                        Member.setText(userName);
                        result.setText("");
                    }
                    else{
                        Toast.makeText(AddMemberActivity.this,"查无此人",Toast.LENGTH_SHORT).show();
                    }
                    Result=result.getText().toString();
                    break;
                case ADD_MEMBER:
                    String response1=(String)msg.obj;
                    //进行UI操作，将结果显示到界面上
                    JsonElement je1=new JsonParser().parse(response1);
                    if(je1.getAsJsonObject().get("result").getAsInt()==1) {
                        Toast.makeText(AddMemberActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                        name.setText("");
                        Member.setText("");
                        result.setText("暂无结果");
                    }
                    else{
                        Toast.makeText(AddMemberActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                    }

            }
        }
    };
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        name = (EditText) findViewById(R.id.name);
        result = (TextView) findViewById(R.id.result);
        Member = (TextView) findViewById(R.id.member);
        addMember=(Button)findViewById(R.id.addMember);
        search=(FloatingActionButton)findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                userName= name.getText().toString();
                //查找这个名字的用户
                UserEntity user=new UserEntity();
                user.userName=userName;
                String data=gson.toJson(user);
                HttpUtil.sendRequestWithHttpURLConnection(handler,"member/findUser",data,SHOW_RESPONSE);
            }
        });
            addMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    //当result的内容为空时，代表查到了，就可以进行添加成员操作
                    Result=result.getText().toString();
                    if(Result=="") {
                        AlertDialog dialog= new AlertDialog.Builder(AddMemberActivity.this)

                            .setTitle("")
                            .setMessage("确定该该用户为项目成员？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //添加成员操作
                                    AddMemberParam ap=new AddMemberParam();
                                    Intent intent=getIntent();
                                    String proId=intent.getExtras().getString("ProID");
                                    UserEntity user=new UserEntity();
                                    UserEntity member=new UserEntity();
                                    ProjectEntity project=new ProjectEntity();
                                    user.userName=LoginActivity.name;
                                    member.userName=userName;
                                    project.id=Integer.valueOf(proId);
                                    ap.user=user;
                                    ap.projectEntity=project;
                                    ap.member=member;
                                    String data=gson.toJson(ap,AddMemberParam.class);
                                    HttpUtil.sendRequestWithHttpURLConnection(handler,"member/addMember",data,ADD_MEMBER);

                                }
                            }).create();
                        dialog.show();
                        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextSize(16);
                        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#8C8C8C"));
                        dialog.getButton(dialog.BUTTON_POSITIVE).setTextSize(16);
                        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1DA6DD"));
                        //添加该用户为项目成员
                    }
                }
            });

    }
    class AddMemberParam{
        UserEntity user;
        UserEntity member;
        ProjectEntity projectEntity;
    }
}
