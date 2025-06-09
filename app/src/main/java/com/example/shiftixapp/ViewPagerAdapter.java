package com.example.shiftixapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final String userId;
    private final String userRole;

    public ViewPagerAdapter(FragmentActivity fragmentActivity, String userId, String userRole) {
        super(fragmentActivity);
        this.userId = userId;
        this.userRole = userRole;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return NotificationsFragment.newInstance(userId);
            case 1:
                return ShiftsFragment.newInstance(userId, userRole);
            case 2:
                return SwapRequestsFragment.newInstance(userId, userRole);
            default:
                return NotificationsFragment.newInstance(userId);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Notifications, Shifts, Swap Requests
    }
}