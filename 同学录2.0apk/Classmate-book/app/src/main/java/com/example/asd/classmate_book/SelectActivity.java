package com.example.asd.classmate_book;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SelectActivity extends AppCompatActivity {

    private EditText name;
    private Button selectBtn;
    private String nameStr;
    private SQLiteDatabase DB;
    private ListView values;
    private Button selectAllBtn;
    //判断是查找名字还是查询所有
    private boolean isSelectAll=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        //
        name = (EditText) findViewById(R.id.nameEditText);
        selectAllBtn = (Button) findViewById(R.id.SelectAllButton);
        values = (ListView) findViewById(R.id.selectResultView);
        selectBtn = (Button) findViewById(R.id.SelAndDelAndAltButton);


        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);

        //
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    read(v, selectAll());
                } catch (Exception e) {
                    tableCreate();
                    Toast.makeText(SelectActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                nameStr = name.getText().toString();
                if (nameStr.trim().isEmpty()) {
                    Toast.makeText(SelectActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        read(v, select(nameStr));isSelectAll=false;
                    } catch (Exception e) {
                        tableCreate();
                        Toast.makeText(SelectActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        values.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                // 获取所点击项的_id
                TextView tv = (TextView) arg1.findViewById(R.id.idView);
                final String id = tv.getText().toString();
                // 通过Dialog提示是否删除
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        SelectActivity.this);
                builder.setMessage("确定要删除吗？");
                // 确定按钮点击事件
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete(id);
                        replaceList(selectAll());// 删除后刷新列表
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

        values.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // 获取_id,username,password项
                TextView tvId = (TextView) arg1.findViewById(R.id.idView);

                final String id = tvId.getText().toString();

                // 通过Dialog弹出修改界面
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectActivity.this);
                builder.setTitle("修改");

                // 自定义界面包括七个文本输入框
                View v = View.inflate(SelectActivity.this, R.layout.alertdialog,
                        null);
                final EditText etName = (EditText) v
                        .findViewById(R.id.alter_name);
                final EditText etAdd = (EditText) v
                        .findViewById(R.id.alter_address);
                final EditText etPhone = (EditText) v
                        .findViewById(R.id.alter_phonenumber);
                final EditText etWechat = (EditText) v
                        .findViewById(R.id.alter_wechat);
                final EditText etMailbox = (EditText) v
                        .findViewById(R.id.alter_mailbox);
                final EditText etQQ = (EditText) v
                        .findViewById(R.id.alter_qqnumber);
                final EditText etPerDes = (EditText) v
                        .findViewById(R.id.alter_personaldescription);

                // Dialog弹出就显示原内容
                Cursor cr=selectByID(id);
                int nameColumn=cr.getColumnIndex("Name");
                int addColumn=cr.getColumnIndex("Address");
                int phoneColumn=cr.getColumnIndex("PhoneNumber");
                int wechatColumn=cr.getColumnIndex("WechatNumber");
                int mailboxColumn=cr.getColumnIndex("MailboxNumber");
                int qqColumn=cr.getColumnIndex("QQNumber");
                int perdesColumn=cr.getColumnIndex("PersonalDescription");

                cr.moveToFirst();
                etName.setText(cr.getString(nameColumn));
                etAdd.setText(cr.getString(addColumn));
                etPhone.setText(cr.getString(phoneColumn));
                etWechat.setText(cr.getString(wechatColumn));
                etMailbox.setText(cr.getString(mailboxColumn));
                etQQ.setText(cr.getString(qqColumn));
                etPerDes.setText(cr.getString(perdesColumn));





                builder.setView(v);
                // 确定按钮点击事件
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = etName.getText().toString();
                        String newAdd = etAdd.getText().toString();
                        String newPhone=etPhone.getText().toString();
                        String newWechat=etWechat.getText().toString();
                        String newMailbox=etMailbox.getText().toString();
                        String newQQ=etQQ.getText().toString();
                        String newPerdes=etPerDes.getText().toString();
                        updata(newName, newAdd,newPhone,newWechat,newMailbox,newQQ,newPerdes, id);

                        if(isSelectAll==true){
                            replaceList(selectAll());// 更新修改后的查询所有列表
                        }
                        else{
                            replaceList(selectByID(id));
                        }
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
            }
        });
    }

    private void delete(String id) {
        String deleteSql = "delete from classmate where _id=?";
        DB.execSQL(deleteSql, new String[] { id });
        //更新id，使id大于要删除的id的往前移动一位。
        DB.execSQL("update classmate set _id=_id-1 where _id > ?",new String[]{id});
    }

    private void updata(String name, String add, String phone, String wechat, String mailbox, String qq, String perdes, String id) {
        String updataSql = "update classmate set Name=?,Address=?,PhoneNumber=?,WechatNumber=?,MailboxNumber=?," +
                "QQNumber=?,PersonalDescription=? where _id=?";
        DB.execSQL(updataSql, new String[] { name, add,phone,wechat,mailbox,qq,perdes, id });
    }

    /**
     * 读取按钮点击事件，以列表的形式显示所有内容
     */
    public void read(View v, Cursor cursor) {
        replaceList(cursor);
    }

    /**
     * ListView的适配器
     */

    public void replaceList(Cursor cursor) {
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "查无此人或数据库中暂无数据", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.values_items, cursor, new String[]{"_id", "Name", "PhoneNumber",
                "QQNumber"}, new int[]{R.id.idView, R.id.nameView, R.id.phoneNumberView,
                R.id.qqNumberView},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        values.setAdapter(adapter);
    }

    public Cursor selectAll() {

        String selectAllSql = "select _id,Name,PhoneNumber,QQNumber from classmate";
        Cursor cursor = DB.rawQuery(selectAllSql, null);// 我们需要查处所有项故不需要查询条件
        return cursor;
    }

    public Cursor select(String name) {
        String selectSql = "select _id,Name,PhoneNumber,QQNumber from classmate where name=?";
        Cursor cursor = DB.rawQuery(selectSql, new String[]{name});
        return cursor;
    }

    public Cursor selectByID(String id) {
        String selectSql = "select * from classmate where _id=?";
        Cursor cursor = DB.rawQuery(selectSql, new String[]{id});
        return cursor;
    }

    //判断表是否存在
    private boolean isTableExist() {
        boolean isTableExist = true;
        Cursor c = DB.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='classmate'", null);
        if (c.getInt(0) == 0) {
            isTableExist = false;
        }
        c.close();
        return isTableExist;
    }



    //创建数据库表
    public void tableCreate() {
        String createSql = "create table classmate(_id integer primary key autoincrement,Name,Address,PhoneNumber,WechatNumber,MailboxNumber," +
                "QQNumber,PersonalDescription)";
        DB.execSQL(createSql);

    }

}