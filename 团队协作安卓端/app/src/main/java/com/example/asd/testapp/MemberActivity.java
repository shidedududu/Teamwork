package com.example.asd.testapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asd.models.member.MemberEntity;
import com.example.asd.models.project.ProjectEntity;
import com.example.asd.models.user.UserEntity;
import com.example.asd.netutils.HttpUtil;
import com.example.asd.sqlutils.TaskSqlUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.asd.testapp.LoginActivity.SHOW_RESPONSE;
import static com.example.asd.testapp.LoginActivity.name;

public class MemberActivity extends AppCompatActivity {
    private TextView ProjectOwnner;
    private FloatingActionButton addMember;
    private ListView member_list;
    String ProID;
    Gson gson=new Gson();
    private static final int GET_MASTER=1;
    private static final int DELETE_MEMBER=2;

    class Param{
        String result;
        List<UserEntity> data;
        List<Integer> member;
    }

    class ProParam{
        String result;
        UserEntity data;
    }
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            //如果返现msg.what=SHOW_RESPONSE，则进行制定操作，如想进行其他操作，则在子线程里将SHOW_RESPONSE改变
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;
                    //进行UI操作，将结果显示到界面上
                    JsonElement je=new JsonParser().parse(response);
                    if(je.getAsJsonObject().get("result").getAsInt()==1) {
                        Param param=gson.fromJson(response,Param.class);
                        replaceList(param.data,param.member);
                        Toast.makeText(MemberActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MemberActivity.this,"成员刷新失败",Toast.LENGTH_SHORT).show();
                    }break;
                case GET_MASTER:
                    String response1=(String)msg.obj;
                    //进行UI操作，将结果显示到界面上
                    JsonElement j=new JsonParser().parse(response1);
                    if(j.getAsJsonObject().get("result").getAsInt()==1) {
                        ProParam pp=gson.fromJson(response1,ProParam.class);
                        ProjectOwnner.setText(pp.data.userName);
                        Toast.makeText(MemberActivity.this,"项目创建者获取成功",Toast.LENGTH_SHORT).show();
                        //replaceList();
                    }
                    else{
                        Toast.makeText(MemberActivity.this,"项目创建者获取失败",Toast.LENGTH_SHORT).show();
                    }break;
                case DELETE_MEMBER:
                    String response2=(String)msg.obj;
                    //进行UI操作，将结果显示到界面上
                    JsonElement je2=new JsonParser().parse(response2);
                    if(je2.getAsJsonObject().get("result").getAsInt()==1) {
                        Toast.makeText(MemberActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        getMembers();
                    }
                    else{
                        Toast.makeText(MemberActivity.this,"成员删除失败",Toast.LENGTH_SHORT).show();
                    }break;
            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        ProjectOwnner=(TextView)findViewById(R.id.projectOwnner);
        addMember=(FloatingActionButton)findViewById(R.id.addMember);
        member_list=(ListView)findViewById(R.id.member_list);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        ProID=bundle.getString("ProID");

        //ProjectOwnner.setText("项目创建者的名字");
        getMaster();
        getMembers();
        //点击加号进入添加成员界面
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent intent=new Intent(MemberActivity.this,AddMemberActivity.class);
                intent.putExtra("ProID",ProID);
                startActivity(intent);
            }
        });


    }

    //获取服务器中member的数据
    private ArrayList<HashMap<String, Object>> getData(List<UserEntity> user,List<Integer> member){
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String,Object>>();
        //Cursor cursor = TaskSqlUtil.selectByProID(ProID); 获得member表的所有数据
        for(int i=0;i<user.size();i++) {
            HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
            tempHashMap.put("tv_id", String.valueOf(member.get(i)));
            tempHashMap.put("title", user.get(i).userName);
            arrayList.add(tempHashMap);
        }
        return arrayList;
    }

    private void getMembers(){
        ProjectEntity project=new ProjectEntity();
        project.id=Integer.valueOf(ProID);
        String data=gson.toJson(project);
        HttpUtil.sendRequestWithHttpURLConnection(handler,"member/getMembers",data,SHOW_RESPONSE);
    }

    private void getMaster(){
        ProjectEntity project=new ProjectEntity();
        project.id=Integer.valueOf(ProID);
        String data=gson.toJson(project);
        HttpUtil.sendRequestWithHttpURLConnection(handler,"member/getProMaster",data,GET_MASTER);
    }

    public void  replaceList(List<UserEntity> user,List<Integer> member){
        MyAdapter_member adapter=new MyAdapter_member(this,getData(user,member));
        member_list.setAdapter(adapter);
    }

    public class MyAdapter_member extends BaseAdapter {
        private ArrayList<HashMap<String, Object>> data;
        /**
         * LayoutInflater 类是代码实现中获取布局文件的主要形式
         *LayoutInflater layoutInflater = LayoutInflater.from(context);
         *View convertView = layoutInflater.inflate();
         *LayoutInflater的使用,在实际开发种LayoutInflater这个类还是非常有用的,它的作用类似于 findViewById(),
         不同点是LayoutInflater是用来找layout下xml布局文件，并且实例化！
         而findViewById()是找具体xml下的具体 widget控件(如:Button,TextView等)。
         */
        private LayoutInflater layoutInflater;
        private Context context;


        public MyAdapter_member(Context context, ArrayList<HashMap<String, Object>> data) {

            this.context = context;
            this.data = data;
            this.layoutInflater = LayoutInflater.from(context);
        }

        /**
         *获取列数
         */
        public int getCount() {
            return data.size();
        }
        /**
         *获取某一位置的数据
         */
        public Object getItem(int position) {
            return data.get(position);
        }
        /**
         *获取唯一标识
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * android绘制每一列的时候，都会调用这个方法
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            ZuJian zuJian = null;
            if(convertView==null){
                zuJian = new ZuJian();
                // 获取组件布局
                convertView = layoutInflater.inflate(R.layout.member_list_item, null);
                zuJian.member_id = (TextView) convertView.findViewById(R.id.member_id);
                zuJian.name = (TextView) convertView.findViewById(R.id.name);
                zuJian.delete = (FloatingActionButton) convertView.findViewById(R.id.delete);
                // 这里要注意，是使用的tag来存储数据的。
                convertView.setTag(zuJian);
            }
            else {
                zuJian = (ZuJian) convertView.getTag();
            }
            // 绑定数据、以及事件触发
            zuJian.member_id.setText((String)data.get(position).get("tv_id"));
            final int id=Integer.valueOf((String)data.get(position).get("tv_id"));
            zuJian.name.setText((String)data.get(position).get("title"));
            zuJian.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    AlertDialog dialog= new AlertDialog.Builder(context)
                            .setTitle("")
                            .setMessage("确定删除该成员？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //删除成员操作
                                    DeleteParam dp=new DeleteParam();
                                    UserEntity user=new UserEntity();
                                    ProjectEntity pro=new ProjectEntity();
                                    MemberEntity mem=new MemberEntity();
                                    pro.id=Integer.valueOf(ProID);
                                    user.userName=name;
                                    mem.id=id;
                                    dp.member=mem;
                                    dp.projectEntity=pro;
                                    dp.user=user;
                                    String data=gson.toJson(dp);
                                    HttpUtil.sendRequestWithHttpURLConnection(handler,"member/deleteMember",data,DELETE_MEMBER);
                                }
                            }).create();
                    dialog.show();
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setTextSize(16);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#8C8C8C"));
                    dialog.getButton(dialog.BUTTON_POSITIVE).setTextSize(16);
                    dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1DA6DD"));
                }
            });
            return convertView;
        }

        class DeleteParam{
            UserEntity user;
            MemberEntity member;
            ProjectEntity projectEntity;
        }

        class ZuJian {

            public TextView member_id;
            public TextView name;
            public FloatingActionButton delete;
        }

    }
}
