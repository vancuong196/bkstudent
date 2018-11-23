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

public class AddConservationDialog extends Dialog{
    public AddConservationDialog(@NonNull Context context, int themeId) {
        super(context,themeId);
    }
    Button btnOk;
    Button btnCancel;
    AutoCompleteTextView edtConservationTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_conservation_dialog_layout);
        btnOk = findViewById(R.id.btn_ok);
        btnCancel = findViewById(R.id.btn_cancel);
        edtConservationTitle = findViewById(R.id.edt_conservation_title);

    }
    public void setOkButtonListener(Button.OnClickListener e){
        btnOk.setOnClickListener(e);
    }
    public void setCancelButtonListener(Button.OnClickListener e){
        btnCancel.setOnClickListener(e);
    }
    public String getTitle(){
        return edtConservationTitle.getText().toString();
    }
    public void setError(String error){
        edtConservationTitle.setError(error);
        edtConservationTitle.requestFocus();

    }

}
