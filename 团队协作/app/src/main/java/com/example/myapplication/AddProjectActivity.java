package com.example.administrator.team;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Calendar;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.TextView;

public class AddProjectActivity extends AppCompatActivity {
    private SQLiteDatabase DB;
    private EditText projectname;

    private EditText projectdescription;
    //private String deadline;
    //String deadline;
    TextView btn;
    private EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

            btn = (TextView) findViewById(R.id.dateBtn);
            et = (EditText) findViewById(R.id.et);
        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);

            btn.setOnClickListener(new View.OnClickListener() {
                Calendar c = Calendar.getInstance();

                @Override
                public void onClick(View v) {
                    // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
                    new DoubleDatePickerDialog(AddProjectActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet( DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                              int endDayOfMonth) {
                            String textString = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                            et.setText(textString);
                           // deadline=textString;
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
                }
            });



        //获取添加界面中输入的数据
        projectname=(EditText)findViewById(R.id.project_name);
        et=(EditText)findViewById(R.id.et);
        projectdescription=(EditText)findViewById(R.id.project_description);

        //按下添加按钮后对数据库进行操作
        Button addBtn=(Button)findViewById(R.id.add);
        addBtn.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          dataSave();
                                      }
                                  }
        );
        }
    public void dataSave(){
        String project=this.projectname.getText().toString();
        String deadline=this.et.getText().toString();
        String description=this.projectdescription.getText().toString();
        if(project.trim().isEmpty()){
            Toast.makeText(this,"项目名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            dataInsert(project, deadline,description);
        }
        catch (Exception e){
            tableProjectCreate();
            dataInsert(project, deadline,description);
        }
        Toast.makeText(this, "Save Success", Toast.LENGTH_SHORT).show();



        //清空输入栏
       this.projectname.setText("");
        this.et.setText("");
       this.projectdescription.setText("");

    }
    public void dataInsert(String projectName, String deadline,String projectDescription) {
        String insertSql="insert into project(ProjectName,DeadlineTime,ProjectDescription) " +
                "values(?,?,?)";
        DB.execSQL(insertSql,new String[]{projectName,deadline,projectDescription});
    }

    //项目表
    public void tableProjectCreate(){
        String createSql= "create table project(_id integer primary key autoincrement, ProjectName," +
                "DeadlineTime,ProjectDescription)";
        DB.execSQL(createSql);
    }



}






