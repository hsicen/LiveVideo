package com.utouu.livevideo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Create by 黄思程 on 2017/2/16   14:41
 * Function：
 * Desc：TableLayout和Fragment的适配器
 */
public class TableFragmentAdapter extends FragmentPagerAdapter{

    private final List<Fragment> fragmentList;
    private String[] titles = {"评论间","礼物榜"};

    public TableFragmentAdapter(FragmentManager fm, List<Fragment> list){
        super(fm);
        this.fragmentList = list;
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
