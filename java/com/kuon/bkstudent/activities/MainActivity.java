package com.kuon.bkstudent.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.kuon.bkstudent.R;
import com.kuon.bkstudent.fragments.ChatFragment;
import com.kuon.bkstudent.fragments.NotificationFragment;
import com.kuon.bkstudent.fragments.OverviewFragment;

public class MainActivity extends AppCompatActivity {


    private Fragment chatFragment;
    private Fragment notificationFragment;
    private Fragment overviewFragment;
    Handler handler = new Handler();
    boolean isStop = false;
    @Override
    public void onBackPressed() {
        if (isStop) {
            System.exit(0);
        } else {
            isStop = true;
            Toast.makeText(this,"Press back again to exit",Toast.LENGTH_SHORT).show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isStop = false;
                }
            },2000);

        }
    }
    private void showChatFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (chatFragment.isAdded()) { // if the fragment is already in container
            ft.show(chatFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.root_layout, chatFragment, "A");
        }
        // Hide fragment B
        if (notificationFragment.isAdded()) { ft.hide(notificationFragment); }
        // Hide fragment C
        if (overviewFragment.isAdded()) { ft.hide(overviewFragment); }
        // Commit changes
        ft.commit();
    }

    private void showOverviewFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (overviewFragment.isAdded()) { // if the fragment is already in container
            ft.show(overviewFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.root_layout, overviewFragment, "A");
        }
        // Hide fragment B
        if (chatFragment.isAdded()) { ft.hide(chatFragment); }
        // Hide fragment C
        if (notificationFragment.isAdded()) { ft.hide(notificationFragment); }
        // Commit changes
        ft.commit();
    }

    private void showNotificationFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (notificationFragment.isAdded()) { // if the fragment is already in container
            ft.show(notificationFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.root_layout, notificationFragment, "A");
        }
        // Hide fragment B
        if (chatFragment.isAdded()) { ft.hide(chatFragment); }
        // Hide fragment C
        if (overviewFragment.isAdded()) { ft.hide(overviewFragment); }
        // Commit changes
        ft.commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        chatFragment = new ChatFragment();
        notificationFragment = new NotificationFragment();
        overviewFragment = new OverviewFragment();
        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction().add(R.id.root_layout, chatFragment);
        manager.beginTransaction().add(R.id.root_layout, overviewFragment);
        manager.beginTransaction().add(R.id.root_layout, notificationFragment);

        showOverviewFragment();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        FragmentManager m = getSupportFragmentManager();

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_home:

                        showOverviewFragment();
                        return true;
                    case R.id.navigation_dashboard:

                        showChatFragment();
                        return true;
                    case R.id.navigation_notifications:
                        showNotificationFragment();
                        return true;
                }
                return false;
            }
        });
    }


}
