package com.kuon.bkstudent.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.kuon.bkstudent.R;
import com.kuon.bkstudent.ultils.SettingManager;

public class EditHostDialog extends Dialog{
    public EditHostDialog(@NonNull Context context, int themeId) {
        super(context,themeId);
    }
    Button btnOk;
    Button btnCancel;
    AutoCompleteTextView edtServerIp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edithost_dialog_layout);
        btnOk = findViewById(R.id.btn_ok);
        btnCancel = findViewById(R.id.btn_cancel);
        edtServerIp = findViewById(R.id.edt_server_ip);
        String hostname = SettingManager.getInstance(getContext()).getHost();
        if (!hostname.isEmpty()){
            edtServerIp.setText(hostname);
        }
    }
    public void setOkButtonListener(Button.OnClickListener e){
        btnOk.setOnClickListener(e);
    }
    public void setCancelButtonListener(Button.OnClickListener e){
        btnCancel.setOnClickListener(e);
    }
    public String getHost(){
        return edtServerIp.getText().toString();
    }
    public void setError(String error){
        edtServerIp.setError(error);
        edtServerIp.requestFocus();

    }

}
