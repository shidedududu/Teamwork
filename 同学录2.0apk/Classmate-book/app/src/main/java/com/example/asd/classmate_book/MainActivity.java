package com.example.asd.classmate_book;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public SQLiteDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);

        //按下增加按钮后的响应，跳转增加界面
        Button addBtn=(Button)findViewById(R.id.AddButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent=new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });

        //按下查/删/改按钮后的响应，跳转查询界面
        Button selAndDelAndAltBtn=(Button)findViewById(R.id.SelAndDelAndAltButton);
        selAndDelAndAltBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent=new Intent(MainActivity.this,SelectActivity.class);
                startActivity(intent);
            }
        });



        //按下导出excel按钮后执行的事件
        Button exportBtn=(Button)findViewById(R.id.exportButton);
        exportBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DatabaseDump databaseDump = new DatabaseDump(DB, null);
                try {

                    databaseDump.writeExcel("classmate");
                    Toast.makeText(MainActivity.this, "Export Success", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    tableCreate();
                    databaseDump.writeExcel("classmate");
                    Toast.makeText(MainActivity.this, "Export Success", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void tableCreate(){
        String createSql= "create table classmate(_id integer primary key autoincrement,Name,Address,PhoneNumber,WechatNumber,MailboxNumber," +
                "QQNumber,PersonalDescription)";
        DB.execSQL(createSql);

    }
}
