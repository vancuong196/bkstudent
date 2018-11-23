package com.kuon.bkstudent.activities;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.kuon.bkstudent.R;

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
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

}
