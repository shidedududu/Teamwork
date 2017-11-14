package com.example.myapplication;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import java.util.ArrayList;

public  class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Fragment1 fragment1=new Fragment1();
    Fragment2 fragment2=new Fragment2();
    Fragment3 fragment3=new Fragment3();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView text1 = (TextView)findViewById(R.id.text1);
        TextView text2 = (TextView)findViewById(R.id.text2);
        TextView text3 = (TextView)findViewById(R.id.text3);
        text1.setOnClickListener(this);
        text2.setOnClickListener(this);
        text3.setOnClickListener(this);
        replaceFragment( fragment1);
    }
@Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.text1:
                replaceFragment(fragment1);
                break;
            case R.id.text2:
                replaceFragment(fragment2);
                break;
            case R.id.text3:
                replaceFragment(fragment3);
                break;
                default:break;
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentlayout1 ,fragment);
        transaction.commit();
    }
}
