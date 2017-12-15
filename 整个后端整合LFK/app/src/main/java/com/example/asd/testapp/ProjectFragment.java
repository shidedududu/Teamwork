package com.example.asd.testapp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
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

import com.race604.flyrefresh.FlyRefreshLayout;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.asd.testapp.MainActivity.DB;

public class ProjectFragment extends Fragment implements FlyRefreshLayout.OnPullRefreshListener,OnMenuItemClickListener, OnMenuItemLongClickListener {

    private FlyRefreshLayout mFlylayout;
    private RecyclerView mListView;
    private ItemAdapter mAdapter;

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;

    private ArrayList<ItemData> mDataSet = new ArrayList<>();
    private Handler mHandler = new Handler();
    private LinearLayoutManager mLayoutManager;

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

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mListView.setLayoutManager(mLayoutManager);
        mAdapter = new ItemAdapter(this.getActivity());

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
        return rootView;
    }

    private void initDataSet(Cursor cursor) {
        while(cursor.moveToNext()){
            String title;
            String time;
            title=cursor.getString(cursor.getColumnIndex("ProjectName"));
            time=cursor.getString(cursor.getColumnIndex("DeadlineTime"));
            mDataSet.add(new ItemData(Color.parseColor("#76A9FC"), R.mipmap.ic_assessment_white_24dp, title, time));
        }
        /*mDataSet.add(new ItemData(Color.parseColor("#76A9FC"), R.mipmap.ic_assessment_white_24dp, "Meeting Minutes", "2017/5/4"));
        mDataSet.add(new ItemData(Color.GRAY, R.mipmap.ic_folder_white_24dp, "Favorites Photos", "2017/12/14"));
        mDataSet.add(new ItemData(Color.GRAY, R.mipmap.ic_folder_white_24dp, "Photos", "2018/1/1"));*/
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
            ItemData itemData = new ItemData(Color.parseColor("#76A9FC"), R.mipmap.ic_assessment_white_24dp, title, time);
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

        MenuObject send = new MenuObject("Send message");
        send.setResource(R.mipmap.sub);

        MenuObject like = new MenuObject("Like profile");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.sub);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(),R.mipmap.sub));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Add to favorites");
        addFav.setResource(R.mipmap.sub);

        MenuObject block = new MenuObject("Block user");
        block.setResource(R.mipmap.sub);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);
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

    /* protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
         invalidateOptionsMenu();
         String backStackName = fragment.getClass().getName();
         boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
         if (!fragmentPopped) {
             FragmentTransaction transaction = fragmentManager.beginTransaction();
             transaction.add(containerId, fragment, backStackName)
                     .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
             if (addToBackStack)
                 transaction.addToBackStack(backStackName);
             transaction.commit();
         }
     }*/
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
        Toast.makeText(this.getActivity(), "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
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
        public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.view_list_item, viewGroup, false);
            return new ItemViewHolder(view);
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
}
