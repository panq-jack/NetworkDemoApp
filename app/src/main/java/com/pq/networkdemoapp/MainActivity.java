package com.pq.networkdemoapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.pq.networkdemoapp.fragment.CategoryFragment;
import com.pq.networkdemoapp.fragment.HomeFragment;
import com.pq.networkdemoapp.fragment.TipFragment;

public class MainActivity extends AppCompatActivity {

    private FrameLayout contentLayout;
    private FragmentManager mFragmentManager;
    private Fragment homeFragment, categoryFragment, tipFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (null==homeFragment){
                        homeFragment=new HomeFragment();
                    }
                    mFragmentManager.beginTransaction().replace(R.id.content,homeFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_dashboard:
                    if (null==categoryFragment){
                        categoryFragment=new CategoryFragment();
                    }
                    mFragmentManager.beginTransaction().replace(R.id.content,categoryFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_notifications:
                    if (null==tipFragment){
                        tipFragment=new TipFragment();
                    }
                    mFragmentManager.beginTransaction().replace(R.id.content,tipFragment).commitAllowingStateLoss();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager=getSupportFragmentManager();

        contentLayout =(FrameLayout)findViewById(R.id.content);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_home);
    }

}
