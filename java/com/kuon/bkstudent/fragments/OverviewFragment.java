package com.kuon.bkstudent.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuon.bkstudent.R;
import com.kuon.bkstudent.activities.ChangePasswordActivity;
import com.kuon.bkstudent.activities.LoginActivity;
import com.kuon.bkstudent.adapters.DateHistoryAdapter;
import com.kuon.bkstudent.api.API;
import com.kuon.bkstudent.exceptions.LoginFailedException;
import com.kuon.bkstudent.exceptions.MissingApiParametersException;
import com.kuon.bkstudent.models.UserInfo;
import com.kuon.bkstudent.ultils.SettingManager;

import org.json.JSONException;

import java.io.IOException;


public class OverviewFragment extends Fragment {
    private static OverviewFragment fragment = new OverviewFragment();
    SettingManager settingManager;
    String token;
    TextView mEdtStudentName;
    TextView mEditStudentId;
    TextView mEdtStudentBirthday;
    TextView mEdtTotalDate;
    TextView mEdtCountDate;
    TextView mEdtPercent;
    ListView mHistoryListview;
    ListAdapter mAdpater;
    Button btnLogout;

    public OverviewFragment() {

    }


    public static OverviewFragment newInstance() {

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingManager= SettingManager.getInstance(getContext());
        token = settingManager.getToken();
        new FetchUserInfoTask(token).execute();
        if (getArguments() != null) {
            //      mParam1 = getArguments().getString(ARG_PARAM1);
            //      mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.overview_fragment_layout, container, false);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        mEditStudentId = view.findViewById(R.id.edt_student_id);
        mEdtStudentName = view.findViewById(R.id.edt_student_name);
        mEdtStudentBirthday = view.findViewById(R.id.edt_student_birthday);
        mEdtTotalDate = view.findViewById(R.id.edt_total_day);
        mEdtCountDate = view.findViewById(R.id.edt_count_date);
        mEdtPercent = view.findViewById(R.id.edt_percent);
        mHistoryListview = view.findViewById(R.id.listview_history);
        Button btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingManager.setToken("");
                Intent myIntent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(myIntent);
                getActivity().finish();
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), ChangePasswordActivity.class);
                getActivity().startActivity(myIntent);
                getActivity().finish();
            }
        });
    }

    public class FetchUserInfoTask extends AsyncTask<Void, Void, Boolean> {

        private final String token;
        int errorCode;
        UserInfo userInfo;
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


            try {
                userInfo = API.getInfo(token);
                if (token==null){
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
            }


        }

        @Override
        protected void onPostExecute(final Boolean success) {


            if (success) {

            System.out.println(this.userInfo);
            mEditStudentId.setText(userInfo.getId());
            mEdtStudentName.setText(userInfo.getName());
            //mEdtStudentBirthday.setText(userInfo.
            mEdtCountDate.setText(String.valueOf(userInfo.getCounted()));
            mEdtTotalDate.setText(String.valueOf(userInfo.getTotalDate()));
            mEdtPercent.setText(String.valueOf(userInfo.getPercent()));
            mAdpater = new DateHistoryAdapter(userInfo.getDates(),getContext());
            mHistoryListview.setAdapter(mAdpater);
            setListViewHeightBasedOnChildren(mHistoryListview);
            } else {
                switch (errorCode){
                    case API_FAILED:
                        Toast.makeText(getActivity(),"Missing api parameter",Toast.LENGTH_SHORT).show();
                        break;
                    case JSON_FAILED:
                        Toast.makeText(getActivity(),"Unknown respond from server",Toast.LENGTH_SHORT).show();

                        break;
                    case CONNECTION_FAILED:
                        Toast.makeText(getActivity(),"Khong the ket noi den server, kiem tra cai dat mang",Toast.LENGTH_SHORT).show();
                        break;
                    case LOGIN_FAILED:
                        Toast.makeText(getActivity(),"Login using token failed",Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }

        @Override
        protected void onCancelled() {

        }
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
