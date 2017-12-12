package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Look_Up_Activity extends AppCompatActivity {
    String itemname;
    String deadlinetime;
    String itemdescribe;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look__up_);
        Intent intent=getIntent();
        itemname=intent.getStringExtra("name");
        deadlinetime=intent.getStringExtra("deadline");
        itemdescribe= intent.getStringExtra("describe");
        TextView text1=(TextView)findViewById(R.id.lookupname);
        TextView text2=(TextView)findViewById(R.id.lookupdeadline);
        TextView text3=(TextView)findViewById(R.id.lookupdescribe);
        button=(Button)findViewById(R.id.modified_item) ;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Look_Up_Activity.this,Item_modified_Activity.class);
                startActivity(intent);
            }
        });
        text1.setText(itemname);
        text2.setText(deadlinetime);
        text3.setText(itemdescribe);
    }}