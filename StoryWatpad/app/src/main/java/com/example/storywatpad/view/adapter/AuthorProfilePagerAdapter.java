package com.example.storywatpad.view.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.storywatpad.fragment.BioFragment;
import com.example.storywatpad.fragment.StoriesFragment;

public class AuthorProfilePagerAdapter extends FragmentStateAdapter {
    private final int authorId;

    public AuthorProfilePagerAdapter(@NonNull FragmentActivity fragmentActivity, int authorId) {
        super(fragmentActivity);
        this.authorId = authorId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Chọn fragment dựa trên position
        if (position == 0) {
            BioFragment bioFragment = new BioFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("author_id", authorId);
            bioFragment.setArguments(bundle);
            return bioFragment;
        } else {
            return new StoriesFragment(authorId);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Chúng ta chỉ có 2 tab: Bio và Author's stories
    }
}

