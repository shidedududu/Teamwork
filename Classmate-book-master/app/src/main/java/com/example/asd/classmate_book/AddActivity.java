package com.example.asd.classmate_book;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    private EditText name;
    private EditText address;
    private EditText phoneNumber;
    private EditText weChatNumber;
    private EditText mailBoxNumber;
    private EditText qqNumber;
    private EditText pensonalDescription;
    private SQLiteDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);

        //获取添加界面中输入的数据
        name=(EditText)findViewById(R.id.editTextName);
        address=(EditText)findViewById(R.id.editTextAddress);
        phoneNumber=(EditText)findViewById(R.id.editTextPhonerNumber);
        weChatNumber=(EditText)findViewById(R.id.editTextWeChatNumber);
        mailBoxNumber=(EditText)findViewById(R.id.editTextMailBoxNumber);
        qqNumber=(EditText)findViewById(R.id.editTextQQNumber);
        pensonalDescription=(EditText)findViewById(R.id.editTextPensonalDesciption);

        //按下添加按钮后对数据库进行操作
        Button addBtn=(Button)findViewById(R.id.addButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dataSave();
            }
        }
        );
    }

    public void dataSave(){
        String name=this.name.getText().toString();
        String address=this.address.getText().toString();
        String phoneNumber=this.phoneNumber.getText().toString();
        String weChatNumber=this.weChatNumber.getText().toString();
        String mailBoxNumber=this.mailBoxNumber.getText().toString();
        String qqNumber=this.qqNumber.getText().toString();
        String personalDescription=this.pensonalDescription.getText().toString();

        try {
            dataInsert(name, address, phoneNumber, weChatNumber, mailBoxNumber, qqNumber, personalDescription);
        }
        catch (Exception e){
            tableCreate();
            dataInsert(name, address, phoneNumber, weChatNumber, mailBoxNumber, qqNumber, personalDescription);
        }
        Toast.makeText(this, "Save Success", Toast.LENGTH_SHORT).show();

        //清空输入栏
        this.name.setText("");
        this.address.setText("");
        this.phoneNumber.setText("");
        this.weChatNumber.setText("");
        this.mailBoxNumber.setText("");
        this.qqNumber.setText("");
        this.pensonalDescription.setText("");
    }

    public void dataInsert(String name, String address, String phoneNumber, String weChatNumber, String mailBoxNumber, String qqNumber,
                        String personalDescription) {
        String insertSql="insert into classmate(name,address,phonenumber,wechatnumber,mailboxnumber,qqnumber,personaldescription) " +
                "values(?,?,?,?,?,?,?)";
        DB.execSQL(insertSql,new String[]{name,address,phoneNumber,weChatNumber,mailBoxNumber,qqNumber,personalDescription});
    }

    public void tableCreate(){
        String createSql= "create table classmate(_id integer primary key autoincrement,name,address,phonenumber,wechatnumber,mailboxnumber," +
                "qqnumber,personaldescription)";
        DB.execSQL(createSql);

    }
}
