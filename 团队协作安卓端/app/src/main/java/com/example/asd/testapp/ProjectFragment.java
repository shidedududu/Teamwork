package com.example.asd.testapp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asd.models.project.ProjectEntity;
import com.example.asd.models.user.UserEntity;
import com.example.asd.netutils.HttpUtil;
import com.example.asd.sqlutils.ProjectSqlUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.race604.flyrefresh.FlyRefreshLayout;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.asd.testapp.LoginActivity.SHOW_RESPONSE;
import static com.example.asd.testapp.LoginActivity.name;

public class ProjectFragment extends Fragment implements FlyRefreshLayout.OnPullRefreshListener,OnMenuItemClickListener, OnMenuItemLongClickListener {

    private FlyRefreshLayout mFlylayout;
    private RecyclerView mListView;
    private ItemAdapter mAdapter;

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;

    private ArrayList<ItemData> mDataSet = new ArrayList<>();
    private Handler mHandler = new Handler();
    private LinearLayoutManager mLayoutManager;
    private Gson gson=new Gson();
    public static final int SHOW_OTHER=1;
    class ProjectBean{
        String result;
        List<ProjectEntity> data;
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
                        ProjectBean pb=gson.fromJson(response,ProjectBean.class);
                        ProjectSqlUtil.refreshTableProject(pb.data);
                        addItemData(ProjectSqlUtil.selectFromMyProject());
                    }
                    else{
                        Toast.makeText(getActivity(),"查询失败",Toast.LENGTH_SHORT).show();
                    }break;
                case SHOW_OTHER:
                    String rresponse=(String)msg.obj;
                    //进行UI操作，将结果显示到界面上
                    JsonElement j=new JsonParser().parse(rresponse);
                    if(j.getAsJsonObject().get("result").getAsInt()==1) {
                        ProjectBean pb=gson.fromJson(rresponse,ProjectBean.class);
                        ProjectSqlUtil.refreshTableOtherProject(pb.data);
                        addItemData(ProjectSqlUtil.selectFromMyProject());
                    }
                    else{
                        Toast.makeText(getActivity(),"查询失败",Toast.LENGTH_SHORT).show();
                    }break;

            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_project, container, false);

        fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
        /*Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);*/
        initToolbar(rootView);
        initMenuFragment();
        mFlylayout = rootView.findViewById(R.id.fly_layout);

        mFlylayout.setOnPullRefreshListener(this);

        mListView = rootView.findViewById(R.id.list);

        //点击进入任务界面
        /*mListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),TaskActivity .class);
                startActivity(intent);
            }
        });*/
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mListView.setLayoutManager(mLayoutManager);
        mAdapter = new ItemAdapter(this.getActivity());
        mAdapter.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){

                Intent intent = new Intent(getActivity(), TaskActivity.class);
                intent.putExtra("ProID", mDataSet.get(position).id );
                intent.putExtra("Proname", mDataSet.get(position).title );
                startActivity(intent);

            }
        });
        mListView.setAdapter(mAdapter);
        mListView.setItemAnimator(new SampleItemAnimator());
        try {
            addItemData(ProjectSqlUtil.selectFromMyProject());
        }catch (Exception e){
            ProjectSqlUtil.tableMyProjectCreate();
           addItemData(ProjectSqlUtil.selectFromMyProject());
        }
        View actionButton = mFlylayout.getHeaderActionButton();
        if (actionButton != null) {
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFlylayout.startRefresh();
                }
            });
        }
        return rootView;
    }

    private void initDataSet(Cursor cursor) {
        while(cursor.moveToNext()){
            String title;
            String time;
            String id;
            id=cursor.getString(cursor.getColumnIndex("_id"));
            title=cursor.getString(cursor.getColumnIndex("ProjectName"));
            time=cursor.getString(cursor.getColumnIndex("DeadlineTime"));
            mDataSet.add(new ItemData(id,Color.parseColor("#76A9FC"), R.mipmap.ic_assessment_white_24dp, title, time));
        }
    }

    private void addItemData(Cursor cursor) {
        mListView.removeAllViews();
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        while(cursor.moveToNext()) {
            String title;
            String time;
            String id;
            id=cursor.getString(cursor.getColumnIndex("_id"));
            title=cursor.getString(cursor.getColumnIndex("ProjectName"));
            time=cursor.getString(cursor.getColumnIndex("DeadlineTime"));
            ItemData itemData = new ItemData(id,Color.parseColor("#76A9FC"), R.mipmap.ic_assessment_white_24dp, title, time);
            mDataSet.add(0, itemData);
        }
        cursor=ProjectSqlUtil.selectFromOtherProject();
        while (cursor.moveToNext()) {
                String title;
                String time;
                String id;
                id = cursor.getString(cursor.getColumnIndex("_id"));
                title = cursor.getString(cursor.getColumnIndex("ProjectName"));
                time = cursor.getString(cursor.getColumnIndex("DeadlineTime"));
                ItemData itemData = new ItemData(id, Color.parseColor("#F4B63E"), R.mipmap.ic_assessment_white_24dp, title, time);
                mDataSet.add(0, itemData);
            }
        mAdapter.notifyItemRangeInserted(0,mDataSet.size());
        mLayoutManager.scrollToPosition(0);
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
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.mipmap.sub);

        MenuObject addProject = new MenuObject("添加项目");
        addProject.setResource(R.mipmap.add1);

        MenuObject addTask = new MenuObject("添加任务");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.add2);
        addTask.setBitmap(b);

       /* MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(),R.mipmap.sub));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Add to favorites");
        addFav.setResource(R.mipmap.sub);

        MenuObject block = new MenuObject("Block user");
        block.setResource(R.mipmap.sub);*/

        menuObjects.add(close);
        menuObjects.add(addProject);
        menuObjects.add(addTask);
       /* menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);*/
        return menuObjects;
    }

    private void initToolbar(View v) {
        Toolbar mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        //TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        /*mToolbar.setNavigationIcon(R.mipmap.close);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        */
        //mToolBarTextView.setText("Samantha");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;*/
        inflater.inflate(R.menu.menu_main,menu);
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
    public void onMenuItemClick(View clickedView, int position) {

        //Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
        switch(position)
        {
            case 1:{
                Intent intent = new Intent(this.getActivity(),AddProjectActivity .class);
                startActivity(intent);
                break;
            }
            case 2:{
                Intent intent = new Intent(this.getActivity(),AddTaskActivity .class);
                startActivity(intent);
                break;
            }
            default:break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        Toast.makeText(this.getActivity(), "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh(FlyRefreshLayout view) {
        View child = mListView.getChildAt(0);
        if (child != null) {
            bounceAnimateView(child.findViewById(R.id.icon));
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFlylayout.onRefreshFinish();
            }
        }, 2000);
    }

    private void bounceAnimateView(View view) {
        if (view == null) {
            return;
        }

        Animator swing = ObjectAnimator.ofFloat(view, "rotationX", 0, 30, -20, 0);
        swing.setDuration(400);
        swing.setInterpolator(new AccelerateInterpolator());
        swing.start();
    }

    @Override
    public void onRefreshAnimationEnd(FlyRefreshLayout view) {
        UserEntity user=new UserEntity();
        user.setUserName(name);
        String data=gson.toJson(user);
        HttpUtil.sendRequestWithHttpURLConnection(handler,"project/getMyProjects",data,SHOW_RESPONSE);
        HttpUtil.sendRequestWithHttpURLConnection(handler,"project/getJoinedProjects",data,SHOW_OTHER);
    }

    public static interface OnItemClickListener {
        void onItemClick( View view, int position );
    }
    private class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> implements View.OnClickListener{

        private LayoutInflater mInflater;
        private DateFormat dateFormat;
        private OnItemClickListener mOnItemClickListener = null;

        public ItemAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            // dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH);
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.view_list_item, viewGroup, false);
            ItemViewHolder viewHolder=new ItemViewHolder(view);
            view.setOnClickListener(this);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
            final ItemData data = mDataSet.get(i);
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(data.color);

            itemViewHolder.icon.setBackgroundDrawable(drawable);
            itemViewHolder.icon.setImageResource(data.icon);
            itemViewHolder.title.setText(data.title);
            itemViewHolder.subTitle.setText(data.time);

            itemViewHolder.itemView.setTag(i);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                //注意这里使用getTag方法获取position
                mOnItemClickListener.onItemClick(v, (int) v.getTag());

            }
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }


    }
    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title;
        TextView subTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subtitle);
        }

    }



}
