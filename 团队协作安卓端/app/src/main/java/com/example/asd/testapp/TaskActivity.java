package com.example.asd.testapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.AlteredCharSequence;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asd.models.project.ProjectEntity;
import com.example.asd.models.task.TaskEntity;
import com.example.asd.models.user.UserEntity;
import com.example.asd.netutils.HttpUtil;
import com.example.asd.sqlutils.TaskSqlUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.asd.testapp.LoginActivity.SHOW_RESPONSE;
import static com.example.asd.testapp.LoginActivity.name;

public class TaskActivity extends AppCompatActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener {
    private Gson gson=new Gson();
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private ListView task_list;
    private SQLiteDatabase DB;
    String ProID;
    String TaskID;
    String TaskName;
    String ProName;
    private PercentageRing mPercentageRing;
    class TaskBean{
        String result;
        List<TaskEntity> data;
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
                        TaskBean tb=gson.fromJson(response,TaskBean.class);

                        TaskSqlUtil.refreshTablcTask(tb.data, ProID);

                        replaceList();
                    }
                    else{
                        Toast.makeText(TaskActivity.this,"刷新失败",Toast.LENGTH_SHORT).show();
                    }

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();
        // 获取SQLiteDatabase以操作SQL语句
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);
        task_list=(ListView)findViewById(R.id.task_list);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        ProID=bundle.getString("ProID");
        ProName=bundle.getString("Proname");
        TextView projectname=(TextView)findViewById(R.id.projectname);
        projectname.setText(ProName);

        getTasks();
        /*//刷新任务列表
        try {
            replaceList();
        }catch (Exception e){
            tableTaskCreate();
            replaceList();
        }*/
        //点击listview进入子任务界面
        task_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {//响应listview中的item的点击事件

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //获取ProID和ProName
                TextView taskID=(TextView)arg1.findViewById(R.id.tv_id);
                TextView taskName=(TextView)arg1.findViewById(R.id.title);
                TaskID=taskID.getText().toString();
                TaskName=taskName.getText().toString();
                Intent intent = new Intent(TaskActivity.this,SubtaskActivity .class);
                intent.putExtra("TaskID",TaskID);
                intent.putExtra("Taskname",TaskName);
                startActivity(intent);
            }
        });
        final SwipeRefreshLayout mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_ly);
        //设置在listview上下拉刷新的监听
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //replaceList();
                getTasks();
                mSwipeLayout.setRefreshing(false);

            }
        });
    }

    //ArrayList<HashMap<String, Object>> data = getData();
    //    task_list.setAdapter(       );
    // MyAdapter adapter = new MyAdapter(this, data);
    private ArrayList<HashMap<String, Object>> getData(){
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String,Object>>();
        Cursor cursor = TaskSqlUtil.selectByProID(ProID);
        if(cursor!=null){
            while(cursor.moveToNext()) {
                HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
                tempHashMap.put("tv_id", cursor.getString(cursor.getColumnIndex("_id")));
                tempHashMap.put("title", cursor.getString(cursor.getColumnIndex("TaskName")));
                tempHashMap.put("subtitle", cursor.getString(cursor.getColumnIndex("Deadline")));
                tempHashMap.put("percentageRing", cursor.getString(cursor.getColumnIndex("Completion")));
                arrayList.add(tempHashMap);
            }
        }

        return arrayList;
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.mipmap.close);

        MenuObject addtask = new MenuObject("添加任务");
        addtask.setResource(R.mipmap.add2);

        MenuObject addsubtask = new MenuObject("添加子任务");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.add3);
        addsubtask.setBitmap(b);

        MenuObject projectManagement = new MenuObject("项目管理");
        projectManagement.setResource(R.mipmap.teamwork);

        menuObjects.add(close);
        menuObjects.add(addtask);
        menuObjects.add(addsubtask);
        menuObjects.add(projectManagement);
        return menuObjects;
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // mToolbar.setNavigationIcon(R.drawable.btn_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            finish();
        }
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch(position)
        {
            case 1:{
                Intent intent = new Intent(TaskActivity.this,AddTaskActivity .class);
                intent.putExtra("ProID",ProID);
                startActivity(intent);
                break;
            }
            case 2:{
                Intent intent = new Intent(TaskActivity.this,AddSubtaskActivity .class);//添加子任务
                intent.putExtra("ProID",ProID);
                startActivity(intent);
                break;
            }
            case 3:{
                Intent intent = new Intent(TaskActivity.this,ProjectManagementActivity .class);//项目管理
                intent.putExtra("ProID",ProID);
                startActivity(intent);
                break;
            }
            default:break;
        }
        // Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }
    public void replaceList(){
        MyAdapter adapter=new MyAdapter(this,getData());
        task_list.setAdapter(adapter);
       /* Cursor cursor = TaskSqlUtil.selectByProID(ProID);

        SimpleCursorAdapter adapter=new SimpleCursorAdapter
                (this,R.layout.task_list_item,cursor,new String[]{"_id","TaskName","Deadline"},
                        new int[]{R.id.tv_id,R.id.title,R.id.subtitle},
                        SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        task_list.setAdapter(adapter);
        for(int i=0;i<task_list.getChildCount();i++)
        {
            mPercentageRing=(PercentageRing)task_list.getChildAt(0).findViewById(R.id.Completion);
            mPercentageRing.setTargetPercent(34);
            // LinearLayout linearLayout=(LinearLayout)task_list.getChildAt(i);
            //  mPercentageRing=(PercentageRing)linearLayout.findViewById(R.id.Completion);
            //  mPercentageRing.setTargetPercent(34);
        }*/
    }

    class TaskGetRequestParam{
        UserEntity user;
        ProjectEntity project;
    }
    public void getTasks(){
        TaskGetRequestParam param=new TaskGetRequestParam();
        UserEntity user=new UserEntity();
        user.setUserName(name);
        ProjectEntity pro=new ProjectEntity();
        pro.id=Integer.valueOf(ProID);
        param.user=user;
        param.project=pro;
        HttpUtil.sendRequestWithHttpURLConnection(handler,"task/getTasks",gson.toJson(param),SHOW_RESPONSE);
    }
}
