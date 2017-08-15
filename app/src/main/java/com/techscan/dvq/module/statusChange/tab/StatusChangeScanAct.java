package com.techscan.dvq.module.statusChange.tab;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.techscan.dvq.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class StatusChangeScanAct extends FragmentActivity implements View.OnKeyListener {


    Activity activity = this;
    @InjectView(R.id.ed_bar_code)
    EditText    edBarCode;
    @InjectView(R.id.before)
    RadioButton before;
    @InjectView(R.id.after)
    RadioButton after;
    @InjectView(R.id.viewPager)
    ViewPager   viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change_scan);
        ButterKnife.inject(this);
        FragmentManager                  manager = getSupportFragmentManager();
        StatusChangeFragmentPagerAdapter adapter = new StatusChangeFragmentPagerAdapter(manager);
        viewPager.setAdapter(adapter);
        edBarCode.setOnKeyListener(this);
        viewPager.setOnPageChangeListener(pagerListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.before, R.id.after})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.before:
                viewPager.setCurrentItem(0);
                break;
            case R.id.after:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            String Bar = edBarCode.getText().toString().trim();
            if (Bar.contains("\n")) {
                Bar = Bar.replace("\n", "");
            }
            edBarCode.setText(Bar);
            EventBus.getDefault().post(Bar);
            return true;
        }
        return false;
    }

    ViewPager.OnPageChangeListener pagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    before.setChecked(true);
                    break;
                case 1:
                    after.setChecked(true);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
