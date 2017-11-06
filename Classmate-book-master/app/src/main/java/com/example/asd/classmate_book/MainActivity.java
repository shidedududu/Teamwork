package com.example.asd.classmate_book;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //按下增加按钮后的响应，跳转增加界面
        Button addBtn=(Button)findViewById(R.id.AddButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent=new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });

        //按下查询按钮后的响应，跳转查询界面
        Button selectBtn=(Button)findViewById(R.id.SelectButton);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent=new Intent(MainActivity.this,SelectActivity.class);
                startActivity(intent);
            }
        });

        //按下删除按钮后的响应，跳转删除界面
        Button deleteBtn=(Button)findViewById(R.id.DeleteButton);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent=new Intent(MainActivity.this,DeleteActivity.class);
                startActivity(intent);
            }
        });

        //按下修改按钮之后的响应，跳转删除界面
        Button alterBtn=(Button)findViewById(R.id.AlterButton);
        alterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent=new Intent(MainActivity.this,AlterActivity.class);
                startActivity(intent);
            }
        });
    }
}
