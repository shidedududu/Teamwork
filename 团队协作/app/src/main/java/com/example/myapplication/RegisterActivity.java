package com.example.asd.testapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asd.models.UserEntity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import static com.example.asd.netutils.HttpUtil.sendRequestWithHttpURLConnection;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName;
    private EditText password;
    private EditText rPassword;
    private Button register;
    private Button duplicationCheck;
    private static Gson gson=new Gson();

    private static final int REGISTER=1;
    private static final int DUP_CHECK=2;

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            //如果返现msg.what=SHOW_RESPONSE，则进行制定操作，如想进行其他操作，则在子线程里将SHOW_RESPONSE改变
            String response=(String)msg.obj;
            //进行UI操作，将结果显示到界面上
            JsonElement je=new JsonParser().parse(response);
            switch (msg.what){
                case REGISTER:
                    if(je.getAsJsonObject().get("result").getAsInt()==1) {
                        Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                    break;
                case DUP_CHECK:
                    if(je.getAsJsonObject().get("result").getAsInt()==1){
                        Toast.makeText(RegisterActivity.this,"用户名可用",Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(RegisterActivity.this,"用户名已存在",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName=(EditText) findViewById(R.id.ruserNameEditText);
        password=(EditText)findViewById(R.id.rPasswordEditText);
        rPassword=(EditText)findViewById(R.id.rRPasswordEditText);
        register=(Button)findViewById(R.id.registerButton);
        duplicationCheck=(Button)findViewById(R.id.userNameCheckButton);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        duplicationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duplicationCheck();
            }
        });

    }

    private void duplicationCheck() {
        String userName=this.userName.getText().toString();
        UserEntity user=new UserEntity();
        user.setUserName(userName);
        String data=gson.toJson(user);
        sendRequestWithHttpURLConnection(handler,"duplicationCheck",data,DUP_CHECK);
    }

    private void register() {
        String userName=this.userName.getText().toString();
        String password=this.password.getText().toString();
        String rPassword=this.rPassword.getText().toString();
        UserEntity user=new UserEntity();
        user.setUserName(userName);
        user.setUserPassword(password);
        String data=gson.toJson(user);
        sendRequestWithHttpURLConnection(handler,"register",data,REGISTER);
    }
}
