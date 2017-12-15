package com.example.asd.testapp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.race604.flyrefresh.FlyRefreshLayout;

import java.text.DateFormat;
import java.util.ArrayList;

public class MainFragment extends Fragment {
/*
    private FlyRefreshLayout mFlylayout;
    private RecyclerView mListView;
    private SQLiteDatabase DB;
    private ItemAdapter mAdapter;

    private ArrayList<ItemData> mDataSet = new ArrayList<>();
    private Handler mHandler = new Handler();
    private LinearLayoutManager mLayoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_project, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/info.db",
                null);
        try{
            initDataSet(select());}
        catch (Exception e){
            tableProjectCreate();
            initDataSet(select());

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFlylayout = (FlyRefreshLayout) findViewById(R.id.fly_layout);

        mFlylayout.setOnPullRefreshListener(this);

        mListView = (RecyclerView) findViewById(R.id.list);

        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
        mAdapter = new ProjectActivity.ItemAdapter(this);

        mListView.setAdapter(mAdapter);

        mListView.setItemAnimator(new SampleItemAnimator());

        View actionButton = mFlylayout.getHeaderActionButton();
        if (actionButton != null) {
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFlylayout.startRefresh();
                }
            });
        }
    }
    private void initDataSet(Cursor cursor) {
        while(cursor.moveToNext()){
            String title;
            String time;
            title=cursor.getString(cursor.getColumnIndex("ProjectName"));
            time=cursor.getString(cursor.getColumnIndex("DeadlineTime"));
            mDataSet.add(new ItemData(Color.parseColor("#76A9FC"), R.mipmap.ic_assessment_white_24dp, title, time));
        }
        mDataSet.add(new ItemData(Color.parseColor("#76A9FC"), R.mipmap.ic_assessment_white_24dp, "Meeting Minutes", "2017/5/4"));
        mDataSet.add(new ItemData(Color.GRAY, R.mipmap.ic_folder_white_24dp, "Favorites Photos", "2017/12/14"));
        mDataSet.add(new ItemData(Color.GRAY, R.mipmap.ic_folder_white_24dp, "Photos", "2018/1/1"));
    }

    private void addItemData(Cursor cursor) {
        mListView.removeAllViews();
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        while(cursor.moveToNext()) {
            String title;
            String time;
            title=cursor.getString(cursor.getColumnIndex("ProjectName"));
            time=cursor.getString(cursor.getColumnIndex("DeadlineTime"));
            ItemData itemData = new ItemData(Color.parseColor("#FFC970"), R.mipmap.ic_smartphone_white_24dp, title, time);
            mDataSet.add(0, itemData);
            mAdapter.notifyItemInserted(0);
            mLayoutManager.scrollToPosition(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        addItemData(select());
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private LayoutInflater mInflater;
        private DateFormat dateFormat;

        public ItemAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            // dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH);
        }

        @Override
        public ItemViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.view_list_item, viewGroup, false);
            return new ProjectActivity.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder( ItemViewHolder itemViewHolder, int i) {
            final ItemData data = mDataSet.get(i);
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(data.color);
            itemViewHolder.icon.setBackgroundDrawable(drawable);
            itemViewHolder.icon.setImageResource(data.icon);
            itemViewHolder.title.setText(data.title);
            itemViewHolder.subTitle.setText(data.time);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
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

    //数据库操作部分
    public void tableProjectCreate(){
        String createSql= "create table project(_id integer primary key autoincrement, ProjectName," +
                "DeadlineTime,ProjectDescription)";
        DB.execSQL(createSql);
    }
    public Cursor select() {
        String selectSql = "select _id,ProjectName,DeadlineTime from project";
        Cursor cursor = DB.rawQuery(selectSql, null);// 我们需要查处所有项故不需要查询条件
        return cursor;
    }
    private void delete(String id) {
        String deleteSql = "delete from project where _id=?";
        DB.execSQL(deleteSql, new String[] { id });
    }
    */
}
