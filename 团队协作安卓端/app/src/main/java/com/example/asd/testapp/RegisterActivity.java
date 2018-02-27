package com.example.asd.testapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asd.models.user.UserEntity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import static com.example.asd.netutils.HttpUtil.sendRequestWithHttpURLConnection;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName;
    private EditText password;
    private EditText rPassword;
    private FloatingActionButton register;
   // private FloatingActionButton duplicationCheck;
    private static Gson gson=new Gson();

    private static final int REGISTER=1;
   // private static final int DUP_CHECK=2;

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            //如果返现msg.what=SHOW_RESPONSE，则进行制定操作，如想进行其他操作，则在子线程里将SHOW_RESPONSE改变
            String response=(String)msg.obj;
            //进行UI操作，将结果显示到界面上
            JsonElement je=new JsonParser().parse(response);
            switch (msg.what){
                case REGISTER:
                    if(je.getAsJsonObject().get("result").getAsInt()==1) {
                        //清空EidtText下方的TextView
                        TextView name=(TextView)findViewById(R.id.name);
                        name.setText("");
                        TextView password1=(TextView)findViewById(R.id.password);
                        password1.setText("");
                        TextView password2=(TextView)findViewById(R.id.password1);
                        password2.setText("");
                        Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else
                    {

                        TextView name=(TextView)findViewById(R.id.name);
                        name.setText("注册失败,该用户名已被注册!");
                        TextView password1=(TextView)findViewById(R.id.password);
                        password1.setText("");
                        TextView password2=(TextView)findViewById(R.id.password1);
                        password2.setText("");
                        // /Toast.makeText(RegisterActivity.this,"该用户名已被注册",Toast.LENGTH_SHORT).show();
                    }
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
        register=(FloatingActionButton)findViewById(R.id.registerButton);
       // duplicationCheck=(FloatingActionButton)findViewById(R.id.userNameCheckButton);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        String userName=this.userName.getText().toString();
        String password=this.password.getText().toString();
        String rPassword=this.rPassword.getText().toString();
        if(password.matches("")){
            TextView password1=(TextView)findViewById(R.id.password);
            password1.setText("注册失败，密码不能为空!");
            //Toast.makeText(this,"注册失败，密码不能包含空格",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.matches("^.{8,}$")){
            //清空EidtText下方的TextView
            TextView name=(TextView)findViewById(R.id.name);
            name.setText("");
            TextView password1=(TextView)findViewById(R.id.password);
            password1.setText("注册失败,密码长度过短!");
            TextView password2=(TextView)findViewById(R.id.password1);
            password2.setText("注册失败,密码长度过短!");
           // Toast.makeText(this,"注册失败，密码长度过短",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!rPassword.equals(password)){
            //清空EidtText下方的TextView
            TextView name=(TextView)findViewById(R.id.name);
            name.setText("");
            TextView password1=(TextView)findViewById(R.id.password);
            password1.setText("注册失败,两次输入密码不相同!");
            TextView password2=(TextView)findViewById(R.id.password1);
            password2.setText("注册失败,两次输入密码不相同!");
            //Toast.makeText(this,"注册失败，两次输入密码不相同",Toast.LENGTH_SHORT).show();
            return;
        }
        UserEntity user=new UserEntity();
        user.setUserName(userName);
        user.setUserPassword(password);
        String data=gson.toJson(user);
        sendRequestWithHttpURLConnection(handler,"register",data,REGISTER);
    }
}

