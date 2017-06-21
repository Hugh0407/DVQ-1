package com.techscan.dvq.MaterialOut;

import android.app.Activity;
import android.os.Bundle;
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

    }

}
