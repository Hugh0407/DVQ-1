package com.techscan.dvq.MaterialOut;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.techscan.dvq.R;

public class MaterialOutScanAct extends Activity {
    EditText txtPurOrderNo;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_out_scan);
        initView();
    }

    private void initView() {
        txtPurOrderNo = (EditText) findViewById(R.id.txtPurOrderNo);
        txtPurOrderNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
