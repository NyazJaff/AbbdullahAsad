package com.abdullah_alsaad.generic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.abdullah_alsaad.JavaClass.BookItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Adapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public Adapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title, BookItem currentBookItem, String type) {
        Bundle arguments = new Bundle();
        arguments.putSerializable("data", (Serializable) currentBookItem);
        arguments.putString("type", type);
        fragment.setArguments(arguments);
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    public void addFragment(Fragment fragment, String title, String type) {
        Bundle arguments = new Bundle();
        arguments.putString("type", type);
        fragment.setArguments(arguments);
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}