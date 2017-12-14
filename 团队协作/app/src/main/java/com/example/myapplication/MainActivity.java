package com.example.administrator.team;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import java.util.ArrayList;

public  class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Fragment project=new Fragment();
    Fragment message=new Fragment();
    Fragment me=new Fragment();
    public SQLiteDatabase DB;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);
        TextView text1 = (TextView)findViewById(R.id.text1);
        TextView text2 = (TextView)findViewById(R.id.text2);
        TextView text3 = (TextView)findViewById(R.id.text3);
        text1.setOnClickListener(this);
        text2.setOnClickListener(this);
        text3.setOnClickListener(this);
        replaceFragment( project);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.text1:
                replaceFragment(project);
                break;
            case R.id.text2:
                replaceFragment(message);
                break;
            case R.id.text3:
                replaceFragment(me);
                break;
            default:break;
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.Fragment1 ,fragment);
        transaction.commit();
    }
    //项目表
    public void tableProjectCreate(){
        String createSql= "create table project(_id integer primary key autoincrement, ProjectName," +
                "DeadlineTime,ProjectDescription)";
        DB.execSQL(createSql);
    }
    //用户表
    public void tableUserCreate(){
        String createSql= "create table user(_id integer primary key autoincrement, UserName," +
                "UserPassword)";
        DB.execSQL(createSql);
    }
    //成员表
    public void tableMemberCreate(){
        String createSql= "create table member(_id integer primary key autoincrement, ProID," +
                "UserID,Authority)";
        DB.execSQL(createSql);
    }
    //任务表
    public void tableTaskCreate(){
        String createSql= "create table task(_id integer primary key autoincrement, ProID," +
                "Deadline，TaskDescription，Completion)";
        DB.execSQL(createSql);
    }
    //子任务表
    public void tableSubtaskCreate(){
        String createSql= "create table subtask(_id integer primary key autoincrement,TaskID," +
                "SubTaskName,IsFinished)";
        DB.execSQL(createSql);
    }
    //执行者表
    public void tableExecutorCreate(){
        String createSql= "create table executor(_id integer primary key autoincrement,TaskID," +
                "UserID)";
        DB.execSQL(createSql);
    }
}