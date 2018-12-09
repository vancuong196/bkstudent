package com.kuon.bkstudent.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kuon.bkstudent.R;
import com.kuon.bkstudent.adapters.NotificationAdapter;
import com.kuon.bkstudent.api.API;
import com.kuon.bkstudent.database.NotificationDb;
import com.kuon.bkstudent.exceptions.LoginFailedException;
import com.kuon.bkstudent.exceptions.MissingApiParametersException;
import com.kuon.bkstudent.exceptions.NewNotificationNotFoundException;
import com.kuon.bkstudent.models.Notification;
import com.kuon.bkstudent.ultils.SettingManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


public class NotificationFragment extends Fragment {
    SettingManager settingManager;
    String token;
    ListView mNotificationListview;
    ListAdapter mAdpater;
    NotificationDb notificationDb;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public NotificationFragment() {

    }


    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingManager= SettingManager.getInstance(getContext());
        token = settingManager.getToken();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.notification_fragment, container, false);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
            mNotificationListview = view.findViewById(R.id.lv_notification);
            mSwipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new FetchUserInfoTask(token).execute();

                }
            });
            notificationDb = new NotificationDb(getActivity());
            refreshList();
            new FetchUserInfoTask(token).execute();

    }
    public void refreshList(){
        ArrayList<Notification> notifications = notificationDb.getAllNotifications();
        mAdpater = new NotificationAdapter(notifications,getActivity());
        mNotificationListview.setAdapter(mAdpater);
    }

    public class FetchUserInfoTask extends AsyncTask<Void, Void, Boolean> {

        private final String token;
        int errorCode;
        ArrayList<Notification> newNotification;
        private static final int CONNECTION_FAILED=1;
        private static final int LOGIN_FAILED=2;
        private static final int API_FAILED=3;
        private static final int JSON_FAILED=4;

        FetchUserInfoTask(String token) {
            this.token = token;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String time = notificationDb.getMaxTime();
            System.out.println(time);
            try {
                this.newNotification = API.getNewNotification(token,time);
                System.out.print("Done ------------------------------");
                if (newNotification==null){
                    errorCode = 1;
                    return false;
                }
                else {
                    return true;
                }
            } catch (IOException e) {
                errorCode = 1;
                return false;

            } catch (JSONException e) {
                errorCode =4;
                e.printStackTrace();
                return false;

            } catch (LoginFailedException e) {
                errorCode = 2;
                return false;

            } catch (MissingApiParametersException e) {
                errorCode = 3;
                return false;
            } catch (NewNotificationNotFoundException e) {
                errorCode = 5;

                e.printStackTrace();
                return false;
            }


        }

        @Override
        protected void onPostExecute(final Boolean success) {


            if (success) {
                if (this.newNotification!=null&& this.newNotification.size()>0){
                    System.out.println("new notification found");
                    for (Notification notification:newNotification){
                        notificationDb.addNotification(notification);
                    }
                }
                refreshList();
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                switch (errorCode){
                    case API_FAILED:
                        Toast.makeText(getActivity(),"Missing api parameter",Toast.LENGTH_SHORT).show();
                        break;
                    case JSON_FAILED:
                        Toast.makeText(getActivity(),"Unknown respond from server",Toast.LENGTH_SHORT).show();

                        break;
                    case CONNECTION_FAILED:
                        Toast.makeText(getActivity(),"Không thể kết nối đến server",Toast.LENGTH_SHORT).show();
                        break;
                    case LOGIN_FAILED:
                        Toast.makeText(getActivity(),"Lỗi xác thực",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getActivity(),"Không có thông báo mới",Toast.LENGTH_SHORT).show();

                        break;
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }

        @Override
        protected void onCancelled() {

        }
    }

}
