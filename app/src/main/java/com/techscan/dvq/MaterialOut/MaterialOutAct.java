package com.techscan.dvq.MaterialOut;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.techscan.dvq.R;

public class MaterialOutAct extends Activity {


    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_in);
        EditText editText = (EditText) findViewById(R.id.txtPurOrderNo);

    }
}
