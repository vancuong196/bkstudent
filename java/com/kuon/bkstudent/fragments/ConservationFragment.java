package com.kuon.bkstudent.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kuon.bkstudent.R;
import com.kuon.bkstudent.activities.ChatActivty;
import com.kuon.bkstudent.adapters.ConservationAdapter;
import com.kuon.bkstudent.api.API;
import com.kuon.bkstudent.dialogs.AddConservationDialog;
import com.kuon.bkstudent.exceptions.CanNotMakeConservationException;
import com.kuon.bkstudent.exceptions.ConservationNotFound;
import com.kuon.bkstudent.exceptions.LoginFailedException;
import com.kuon.bkstudent.exceptions.MissingApiParametersException;
import com.kuon.bkstudent.models.Conservation;
import com.kuon.bkstudent.ultils.SettingManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


public class ConservationFragment extends Fragment {
    SettingManager settingManager;
    String token;
    ListView mConservationListview;
    ListAdapter mAdpater;
    SwipeRefreshLayout mSwipeRefreshLayout;
    FloatingActionButton mBtnAddConservation;
    AddConservationDialog dialog;
    public ConservationFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        new FetchConservationTask(token).execute();

    }

    public static ConservationFragment newInstance(String param1, String param2) {
        ConservationFragment fragment = new ConservationFragment();
        //  Bundle args = new Bundle();
        //  args.putString(ARG_PARAM1, param1);
        // args.putString(ARG_PARAM2, param2);
        //  fragment.setArguments(args);
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
        return inflater.inflate(R.layout.conservation_fragment_layout, container, false);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        mConservationListview = view.findViewById(R.id.lv_conservation);
        mBtnAddConservation = view.findViewById(R.id.btn_add_conservation);
        mConservationListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conservation conservation = (Conservation) mConservationListview.getAdapter().getItem(position);
                Intent myIntent = new Intent(getActivity(), ChatActivty.class);
                myIntent.putExtra("conservationId",conservation.getId());
                getActivity().startActivity(myIntent);

            }
        });
        mBtnAddConservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AddConservationDialog(getActivity(),R.style.dialog_theme);
                dialog.show();
                dialog.setOkButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = dialog.getTitle();
                        if (title==null||title.isEmpty()||title.length()<10){
                            dialog.setError("Vui long nhap noi dung lon hon 10 ki tu");
                        }
                        else {
                            Toast.makeText(getActivity(),"add",Toast.LENGTH_LONG).show();
                            new MakeConservationTask(token,title).execute();
                            dialog.hide();
                        }
                    }
                });
                dialog.setCancelButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });

            }
        });

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchConservationTask(token).execute();

            }
        });
        new FetchConservationTask(token).execute();

    }
    public void refreshList(ArrayList<Conservation> conservations){
        mAdpater = new ConservationAdapter(conservations,getActivity());
        mConservationListview.setAdapter(mAdpater);
    }

    public class FetchConservationTask extends AsyncTask<Void, Void, Boolean> {

        private final String token;
        int errorCode;
        ArrayList<Conservation> conservations;
        private static final int CONNECTION_FAILED=1;
        private static final int LOGIN_FAILED=2;
        private static final int API_FAILED=3;
        private static final int JSON_FAILED=4;

        FetchConservationTask(String token) {
            this.token = token;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                conservations = API.getConservation(token);
                System.out.print("Done ------------------------------");
                if (conservations==null){
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

            } catch (ConservationNotFound conservationNotFound) {
                errorCode = 5;
                conservationNotFound.printStackTrace();
                return false;
            }


        }

        @Override
        protected void onPostExecute(final Boolean success) {


            if (success) {
                if (conservations!=null&& conservations.size()>0){
                  //to-dos
                    refreshList(conservations);
                }

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
                        Toast.makeText(getActivity(),"Khong the ket noi den server, kiem tra cai dat mang",Toast.LENGTH_SHORT).show();
                        break;
                    case LOGIN_FAILED:
                        Toast.makeText(getActivity(),"Login using token failed",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getActivity(),"New notification not found",Toast.LENGTH_SHORT).show();

                        break;
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }

        @Override
        protected void onCancelled() {

        }
    }


    public class MakeConservationTask extends AsyncTask<Void, Void, Boolean> {

        private final String token;
        private String title;
        int errorCode;
        boolean isOk;
        private static final int CONNECTION_FAILED=1;
        private static final int LOGIN_FAILED=2;
        private static final int API_FAILED=3;
        private static final int JSON_FAILED=4;

        MakeConservationTask(String token, String title) {
            this.token = token;
            this.title = title;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                isOk = API.makeConservation(token,title);
                System.out.print("Done ------------------------------");
                return isOk;
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

            } catch (CanNotMakeConservationException e) {
                e.printStackTrace();
                errorCode =5;
                return false;
            }


        }

        @Override
        protected void onPostExecute(final Boolean success) {


            if (success) {
                new FetchConservationTask(token).execute();
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
                        Toast.makeText(getActivity(),"Khong the ket noi den server, kiem tra cai dat mang",Toast.LENGTH_SHORT).show();
                        break;
                    case LOGIN_FAILED:
                        Toast.makeText(getActivity(),"Login using token failed",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getActivity(),"New notification not found",Toast.LENGTH_SHORT).show();

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
