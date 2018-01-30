package com.example.aditya.mysocialapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;



public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("POSITIONNUM", position+"");
        switch (position) {
            case 0:
                AllFriends tab1 = new AllFriends();
                return tab1;
            case 1:
                AddNewFriends tab2 = new AddNewFriends();
                return tab2;
            case 2:
                PendingRequests tab3 = new PendingRequests();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}