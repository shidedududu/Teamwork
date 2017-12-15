package com.example.asd.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.asd.models.UserEntity;
import com.example.asd.netutils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


public class LoginActivity extends AppCompatActivity {
    EditText userName;
    EditText password;
    Button loginBtn;
    TextView register;
    TextView forgetPass;
    private SharedPreferences sp;

    public static final int SHOW_RESPONSE=0;


    //用于处理和发送消息的Hander
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            //如果返现msg.what=SHOW_RESPONSE，则进行制定操作，如想进行其他操作，则在子线程里将SHOW_RESPONSE改变
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;


                    //进行UI操作，将结果显示到界面上
                    JsonElement je=new JsonParser().parse(response);
                    if(je.getAsJsonObject().get("result").getAsInt()==1) {
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sp=this.getSharedPreferences("userinfo", Context.MODE_WORLD_READABLE);
        setContentView(R.layout.activity_login);

        userName=(EditText) findViewById(R.id.userNameEditText);
        password=(EditText)findViewById(R.id.userPasswordEditText);
        loginBtn=(Button)findViewById(R.id.loginButton);
        register=(TextView)findViewById(R.id.register);
        forgetPass=(TextView)findViewById(R.id.forgetPass);

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,AddProjectActivity.class);
                startActivity(intent);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void login(){
        String userName=this.userName.getText().toString();
        String userPassword=this.password.getText().toString();
        UserEntity user=new UserEntity();
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        Gson gson=new Gson();
        String data=gson.toJson(user);

        // Message message = new Message();
        //message.what = SHOW_RESPONSE;
        //message.obj =
        HttpUtil.sendRequestWithHttpURLConnection(handler,"login",data,SHOW_RESPONSE);
        //handler.sendMessage(message);
    }
}
