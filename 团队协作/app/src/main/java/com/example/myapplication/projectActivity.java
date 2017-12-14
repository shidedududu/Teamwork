package com.example.administrator.team;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.database.Cursor;
import android.widget.ImageView;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toast;
import android.content.Intent;
public class projectActivity extends AppCompatActivity {
    private SQLiteDatabase DB;
    private ListView add_project;
    private ImageView Icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Icon=(ImageView) findViewById(R.id.icon);
        add_project = (ListView) findViewById(R.id.create_list);
        Button refresh = (Button) findViewById(R.id.Refresh);
        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);
        //按下增加按钮后的响应，跳转增加界面
        Button addBtn = (Button) findViewById(R.id.project);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(projectActivity.this, AddProjectActivity.class);
                startActivity(intent);
            }
        });

        //更新项目列表
        replaceList();

      /*  //点击项目进入任务界面
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(projectActivity.this, taskActivity.class);
                startActivity(intent);
            }
        });*/

        //在listview显示数据库信息
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    refresh(v);
                } catch (Exception e) {
                    tableProjectCreate();
                    Toast.makeText(projectActivity.this, "暂无项目", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //长按删除项目
        add_project.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                // 获取所点击项的_id
                TextView tv = (TextView) arg1.findViewById(R.id.tv_id);
                final String id = tv.getText().toString();
                // 通过Dialog提示是否删除
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        projectActivity.this);
                builder.setMessage("删除该项目？");
                // 确定按钮点击事件
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete(id);
                        replaceList();// 删除后刷新列表
                    }
                });
                // 取消按钮点击事件
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                return true;
            }
        });
    }

    public void refresh(View v) {
        replaceList();
    }
    public void replaceList() {
        Cursor cursor=select();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.values_item, cursor, new String[]{"_id","ProjectName", "DeadlineTime",},
                new int[]{R.id.tv_id,R.id.project_name, R.id.etdeadline},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        add_project.setAdapter(adapter);
    }
    public void tableProjectCreate(){
        String createSql= "create table project(_id integer primary key autoincrement, ProjectName," +
                "DeadlineTime,ProjectDescription)";
        DB.execSQL(createSql);
    }
    public Cursor select() {
        String selectSql = "select _id,ProjectName,DeadlineTime from project";
        Cursor cursor = DB.rawQuery(selectSql, null);// 我们需要查处所有项故不需要查询条件
        return cursor;
    }
    private void delete(String id) {
        String deleteSql = "delete from project where _id=?";
        DB.execSQL(deleteSql, new String[] { id });
    }

}
