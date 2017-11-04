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

public class SelectActivity extends AppCompatActivity {

    private EditText name;
    private EditText phoneNumber;
    private EditText weChatNumber;
    private EditText qqNumber;
    private SQLiteDatabase DB;
    private ListView values;
    private Button selectAllBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        //
        name=(EditText)findViewById(R.id.nameEditText);
        phoneNumber=(EditText)findViewById(R.id.phoneNumberEditText);
        qqNumber=(EditText)findViewById(R.id.qqNumberEditText);
        selectAllBtn=(Button)findViewById(R.id.SelectAllButton);
        values=(ListView)findViewById(R.id.selectResultListView);

        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);

        //
        selectAllBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                read(v);
            }
        });

    }

    /**
     * 读取按钮点击事件，以列表的形式显示所有内容
     */
    public void read(View v) {
        replaceList();
    }

    /**
     * ListView的适配器
     */
    public void replaceList() {
        Cursor cursor = select();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.values_items, cursor, new String[] { "name", "phonenumber",
                "qqnumber","wechatnumber" }, new int[] { R.id.nameView, R.id.phoneNumberView,
                R.id.qqNumberView,R.id.weChatNumberView },
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        values.setAdapter(adapter);
    }

    public Cursor select() {
        String selectSql = "select _id,name,phonenumber,qqnumber,wechatnumber from classmate";
        Cursor cursor = DB.rawQuery(selectSql, null);// 我们需要查处所有项故不需要查询条件
        return cursor;
    }
}
