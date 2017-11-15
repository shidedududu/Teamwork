package com.example.myapplication;

import android.content.Intent;
import android.support.v4.app.SupportActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public  class AddProject_Activity extends AppCompatActivity implements View.OnClickListener {
    private EditText project_name;
    private EditText deadline;
    private EditText item_describe;
    private Button back;
    private Button submit;
    String projectname;
    String deadlinetime;
    String itemdescribe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project_);
        project_name=(EditText)findViewById(R.id.project_name);
        deadline=(EditText)findViewById(R.id.deadline);
        item_describe=(EditText)findViewById(R.id.item_describe);
        back = (Button) findViewById(R.id.back);
        submit = (Button)findViewById(R.id.submit);
         projectname=project_name.getText().toString();
         deadlinetime=deadline.getText().toString();
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v){
        switch (v.getId()){
            case  R.id.back :
                finish();
                break;
            case  R.id.submit:
                projectname=project_name.getText().toString();
                deadlinetime=deadline.getText().toString();
                itemdescribe=item_describe.getText().toString();
                Intent intent= new Intent();
                intent.putExtra("name",projectname);
                intent.putExtra("deadline",deadlinetime);
                intent.putExtra("describe",itemdescribe);
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:break;
        }

    }
}