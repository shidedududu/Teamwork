package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/7 0007.
 */

public class Fragment1 extends Fragment{

    private List<String> projectname=new ArrayList<String>();
    private static final int ITEM1= Menu.FIRST;
    private static final int ITEM2=Menu.FIRST+1;
    private static final int ITEM3=Menu.FIRST+2;
    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment1,container,false);
        Button button =  (Button)view.findViewById(R.id.add_item);
        ListView listView=(ListView) view.findViewById(R.id.list);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(),AddProject_Activity.class);
                startActivityForResult(intent,1);
            }
        });
        this.registerForContextMenu(listView);
         adapter = new  ArrayAdapter<String> (getActivity(),android.R.layout.simple_list_item_1,projectname);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(getActivity(),TaskActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("提示信息");

        menu.add(0, ITEM1, 0, "删除Item");
        menu.add(0, ITEM2, 0, "查看Item");
        menu.add(0, ITEM3, 0, "关于我");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode ==getActivity().RESULT_OK){
                     projectname.add(data.getStringExtra("data_return"));
                     adapter.notifyDataSetChanged();
                }break;
            default:break;
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
       AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId())
        {
            case ITEM1:
                projectname.remove(menuInfo.position);
                adapter.notifyDataSetChanged();
                break;
            case ITEM2:
                break;
            case ITEM3:
                break;
            default:break;
        }
        return super.onContextItemSelected(item);
    }
}
