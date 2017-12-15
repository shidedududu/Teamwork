package com.example.asd.testapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class PersonalActivity extends AppCompatActivity {
    private TextView logout;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        logout=(TextView)findViewById(R.id.logout);
        logout.setClickable(true);



        logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(PersonalActivity.this)
                        .setTitle("")
                        .setMessage("确定要退出登录吗")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(PersonalActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .create();
                dialog.show();
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextSize(16);
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#8C8C8C"));
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextSize(16);
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1DA6DD"));
            }
        });



    }



}
/*
    // 通过Dialog提示是否删除
    AlertDialog.Builder builder = new AlertDialog.Builder(
            PersonalActivity.this);
                builder.setMessage("确定要退出吗？");
                        // 确定按钮点击事件
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

@Override
public void onClick( DialogInterface dialog, int which ) {
        Intent intent = new Intent(PersonalActivity.this, LoginActivity.class);
        startActivity(intent);
        }
        });
        // 取消按钮点击事件
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

@Override
public void onClick( DialogInterface dialog, int which ) {
        dialog.dismiss();
        }
        });
        builder.show();
        builder.show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
        builder.show().getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
*/