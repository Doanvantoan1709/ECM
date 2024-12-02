package com.example.demoassignment2;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behaviorResumeOnlyCurrentFragment) {
        super(fm, behaviorResumeOnlyCurrentFragment);
        // Khởi tạo các Fragment chỉ một lần
        fragmentList.add(new HomeFragment());
        fragmentList.add(new NewExpenseFragment());
        fragmentList.add(new ListExpenseFragment());
        fragmentList.add(new NewBudgetFragment());
        fragmentList.add(new ListBudgetFragment());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Trả về instance đã được tạo trước đó
        return fragmentList.get(position);
    }


    @Override
    public int getCount() {
        return fragmentList.size(); // Sử dụng kích thước danh sách để xác định số lượng tab
    }

}
