package com.invictus.reehbayse.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.invictus.reehbayse.fragments.WizardFragment;


public class WizardAdapter extends FragmentPagerAdapter {

    private int WIZARD_PAGES_COUNT = 3;

    public WizardAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return WizardFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return WIZARD_PAGES_COUNT;
    }
}
