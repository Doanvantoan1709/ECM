package com.example.demoassignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager=findViewById(R.id.view_pager);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);

        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);


        Intent intent = getIntent();
        //route tu activity BudgetUpdate ve main acitivy roi route sang fragment budgetlist
          if (intent != null && intent.hasExtra("BudgetList")) {
            String fragmentToShow = intent.getStringExtra("BudgetList");
            if ("BudgetList".equals(fragmentToShow)) {
                viewPager.setCurrentItem(4); // 4 là chỉ số của ListFragment
                bottomNavigationView.getMenu().findItem(R.id.listBudget).setChecked(true);
            }
         }


        //route tu activity DetailExpense ve main acitivy roi route sang fragment expenselist
        if (intent != null && intent.hasExtra("ExpenseList")) {
            String fragmentToShow = intent.getStringExtra("ExpenseList");
            if ("ExpenseList".equals(fragmentToShow)) {
                viewPager.setCurrentItem(2); // 4 là chỉ số của ListFragment
                bottomNavigationView.getMenu().findItem(R.id.listExpense).setChecked(true);
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.newExpense).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.listExpense).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.newBudget).setChecked(true);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.listBudget).setChecked(true);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

      bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              int id = item.getItemId();
              if (id == R.id.home) {
                  viewPager.setCurrentItem(0);
                  return true;
              } else if (id == R.id.newExpense) {
                  viewPager.setCurrentItem(1);
                  return true;
              } else if (id == R.id.listExpense) {
                  viewPager.setCurrentItem(2);
                  return true;
              } else if (id == R.id.newBudget) {
                  viewPager.setCurrentItem(3);
                  return true;
              } else if (id == R.id.listBudget) {
                  viewPager.setCurrentItem(4);
              }
              return false;
          }
      });

    }



}