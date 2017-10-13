package candra.bukupengeluaran.Views.Activities;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.kobakei.ratethisapp.RateThisApp;

import candra.bukupengeluaran.R;
import candra.bukupengeluaran.Supports.Utils.FragmentAdapter;
import candra.bukupengeluaran.Views.Fragments.Home.HomeFragment;
import candra.bukupengeluaran.Views.Fragments.Home.ReportFragment;
import candra.bukupengeluaran.Views.Fragments.Home.SettingFragment;
import candra.bukupengeluaran.databinding.ActivityHomeBinding;

/**
 * Created by Candra Triyadi on 06/10/2017.
 */

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = DataBindingUtil.setContentView(this, R.layout.activity_home);
        setView();
    }

    private void rateThisApp(){
        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this);
    }

    private void setView(){
        final FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), true);
        fragmentAdapter.addFragment(new HomeFragment(),"Home");
        fragmentAdapter.addFragment(new ReportFragment(),"Report");
        fragmentAdapter.addFragment(new SettingFragment(),"Setting");

        content.tab.addTab(content.tab.newTab().setText(fragmentAdapter.getPageTitle(0)), true);
        content.tab.addTab(content.tab.newTab().setText(fragmentAdapter.getPageTitle(1)));
        content.tab.addTab(content.tab.newTab().setText(fragmentAdapter.getPageTitle(2)));

        content.tab.getTabAt(0).setIcon(R.drawable.ic_home);
        content.tab.getTabAt(1).setIcon(R.drawable.ic_performa);
        content.tab.getTabAt(2).setIcon(R.drawable.ic_settings);

        displayView(fragmentAdapter.getItem(0));
        content.tab.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#009de0"), PorterDuff.Mode.SRC_IN);

        content.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                displayView(fragmentAdapter.getItem(tab.getPosition()));
                tab.getIcon().setColorFilter(Color.parseColor("#009de0"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#445066"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void displayView(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(content.container.getId(), fragment).commit();

    }
}
