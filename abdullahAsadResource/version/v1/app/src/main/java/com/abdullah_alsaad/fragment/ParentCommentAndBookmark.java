package com.abdullah_alsaad.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdullah_alsaad.JavaClass.BookItem;
import com.abdullah_alsaad.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ParentCommentAndBookmark extends Fragment {

    private BookItem currentBookItem;
    public static final String COMMENT = "comment";
    public static final String BOOKMARK = "bookmark";
    public ParentCommentAndBookmark() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentBookItem = (BookItem) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_comment_and_bookmark, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new CommentFragment(), getActivity().getString(R.string.comment), currentBookItem, COMMENT);
        adapter.addFragment(new CommentFragment(), getActivity().getString(R.string.bookmark), currentBookItem,BOOKMARK);
        viewPager.setAdapter(adapter);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }
    static class Adapter extends FragmentPagerAdapter {
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

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}


