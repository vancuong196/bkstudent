package com.kuon.bkstudent.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.kuon.bkstudent.R;
import com.kuon.bkstudent.api.API;
import com.kuon.bkstudent.exceptions.LoginFailedException;
import com.kuon.bkstudent.exceptions.MessageCannotBeSentException;
import com.kuon.bkstudent.exceptions.MissingApiParametersException;
import com.kuon.bkstudent.models.Message;
import com.kuon.bkstudent.ultils.SettingManager;

import org.json.JSONException;

import java.io.IOException;

import co.intentservice.chatui.models.ChatMessage;

public class ChangePasswordActivity extends AppCompatActivity {
    Button btnChangePassword;
    Button btnCancel;
    EditText edtOldPass;
    EditText edtNewPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        btnChangePassword = findViewById(R.id.btn_change);
        btnCancel = findViewById(R.id.btn_cancel);
        edtNewPass = findViewById(R.id.edt_new_password);
        edtOldPass = findViewById(R.id.edt_old_password);
        final String token = SettingManager.getInstance(this).getToken();
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass= edtOldPass.getText().toString();
                String newPass = edtNewPass.getText().toString();
                if (!oldPass.isEmpty()&&newPass.length()>6){
                    new ChangePasswordTask(token,oldPass,newPass).execute();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Vui lòng nhập đúng thông tin",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public class ChangePasswordTask extends AsyncTask<Void, Void, Boolean> {

        private final String token;
        int errorCode;
        String oldPass;
        String newPass;
        String conservationId;
        private static final int CONNECTION_FAILED=1;
        private static final int LOGIN_FAILED=2;
        private static final int API_FAILED=3;
        private static final int JSON_FAILED=4;

        ChangePasswordTask(String token,String oldPass,String newPass) {
            this.token = token;
            this.oldPass = oldPass;
            this.newPass = newPass;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            try {
                boolean isOk = API.changePassword(token,oldPass,newPass);
                if (isOk){
                    return true;
                } else
                {
                    errorCode = 6;
                    return false;
                }

            } catch (IOException e) {
                errorCode = 1;
                return false;

            } catch (JSONException e) {
                errorCode =4;
                e.printStackTrace();
                return false;

            } catch (MissingApiParametersException e) {
                errorCode = API_FAILED;
                e.printStackTrace();
                return false;

            }


        }

        @Override
        protected void onPostExecute(final Boolean success) throws NullPointerException{


            if (success) {
                Intent myIntent = new Intent(ChangePasswordActivity.this,LoginActivity.class);
                SettingManager.getInstance(getApplicationContext()).setToken("");
                ChangePasswordActivity.this.startActivity(myIntent);
                ChangePasswordActivity.this.finish();
                // TODO add show code here
            } else {
                switch (errorCode){
                    case API_FAILED:
                        Toast.makeText(ChangePasswordActivity.this,"Missing api parameter",Toast.LENGTH_SHORT).show();
                        break;
                    case JSON_FAILED:
                        Toast.makeText(getApplicationContext(),"Unknown respond from server",Toast.LENGTH_SHORT).show();

                        break;
                    case CONNECTION_FAILED:
                        Toast.makeText(getApplicationContext(),"Không thể kết nối đến server",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),"Mật khẩu củ không đúng",Toast.LENGTH_SHORT).show();
                        break;
                }


            }
        }

        @Override
        protected void onCancelled() {

        }
    }


}
