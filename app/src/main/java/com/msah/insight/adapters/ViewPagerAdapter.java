package com.msah.insight.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.msah.insight.fragments.ClassFragment;
import com.msah.insight.fragments.NotesFragment;
import com.msah.insight.fragments.SolutionsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(
            @NonNull FragmentManager fm)
    {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;
        if (position == 0)
            fragment = new ClassFragment();
        else if (position == 1)
            fragment = new NotesFragment();
        else if (position == 2)
            fragment = new SolutionsFragment();
        return fragment;
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        String title = null;
        if (position == 0)
            title = "CHATS";
        else if (position == 1)
            title = "NOTES";
        else if (position == 2)
            title = "CLASS";

        return title;
    }

}
