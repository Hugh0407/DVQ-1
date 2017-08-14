package com.techscan.dvq.module.statusChange.tab;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by cloverss on 2017/8/14.
 */

public class StatusChangeFragmentPagerAdapter extends FragmentPagerAdapter {

    FragmentBefore before;
    FragmentAfter  after;

    public StatusChangeFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        before = new FragmentBefore();
        after = new FragmentAfter();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return before;
            case 1:
                return after;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
