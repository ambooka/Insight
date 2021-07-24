package com.msah.insight.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.msah.insight.fragments.ClassFragment;
import com.msah.insight.fragments.NotesFragment;
import com.msah.insight.fragments.SolutionsFragment;

import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(Fragment fa) {
        super(fa);
    }



    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
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
    public int getItemCount() {
        return 3;
    }
}
