package com.example.asd.classmate_book;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SelectActivity extends AppCompatActivity {

    private EditText name;
    private Button selectBtn;
    private String nameStr;
    private SQLiteDatabase DB;
    private ListView values;
    private Button selectAllBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        //
        name=(EditText)findViewById(R.id.nameEditText);
        selectAllBtn=(Button)findViewById(R.id.SelectAllButton);
        values=(ListView)findViewById(R.id.selectResultView);
        selectBtn=(Button)findViewById(R.id.SelectButton);


        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);

        //
        selectAllBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                read(v,selectAll());
            }
        });

        //
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameStr=name.getText().toString();
              if(nameStr.trim().isEmpty()){
                  Toast.makeText(SelectActivity.this,"输入不能为空", Toast.LENGTH_SHORT).show();
              }
              else{
                  read(v,select(nameStr));
              }
            }
        });
    }

    /**
     * 读取按钮点击事件，以列表的形式显示所有内容
     */
    public void read(View v,Cursor cursor) {
        replaceList(cursor);
    }

    /**
     * ListView的适配器
     */

    public void replaceList(Cursor cursor) {
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "查无此人", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.values_items, cursor, new String[] { "name", "phonenumber",
                "qqnumber","wechatnumber" }, new int[] { R.id.nameView, R.id.phoneNumberView,
                R.id.qqNumberView,R.id.weChatNumberView },
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        values.setAdapter(adapter);
    }

    public Cursor selectAll() {
        String selectAllSql = "select _id,name,phonenumber,qqnumber,wechatnumber from classmate";
        Cursor cursor = DB.rawQuery(selectAllSql, null);// 我们需要查处所有项故不需要查询条件
        return cursor;
    }

    public Cursor select(String name){
        String selectSql="select _id,name,phonenumber,qqnumber,wechatnumber from classmate where name=?";
        Cursor cursor=DB.rawQuery(selectSql,new String[]{name});
        return  cursor;
    }

}