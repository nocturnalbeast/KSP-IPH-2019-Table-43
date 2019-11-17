package com.invictus.reehbayse.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.invictus.reehbayse.fragments.InboxPagerFragment;


public class InboxAdapter extends FragmentStatePagerAdapter {

    private int PAGE_COUNT = 2;

    public InboxAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return InboxPagerFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        switch (position){
//            case 0:
//                return "Inbox";
//            case 1:
//                return "Groups";
//            default:
//                return super.getPageTitle(position);
//        }
//    }
}
