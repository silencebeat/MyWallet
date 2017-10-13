package candra.bukupengeluaran.Supports.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noizar on 10/21/16.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();
    boolean isShowTitle = true;

    public FragmentAdapter(FragmentManager fm, boolean isShowTitle) {
        super(fm);
        this.isShowTitle = isShowTitle;
    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    public void insertFragment(Fragment fragment, String title, int position){
        mFragments.add(position, fragment);
        mFragmentTitles.add(position, title);
        notifyDataSetChanged();
    }

    public void removeFragment(int position){
        mFragments.remove(position);
        notifyDataSetChanged();
    }

    public List<Fragment> getmFragments() {
        return mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(isShowTitle) {
            return mFragmentTitles.get(position);
        }else {
            return "";
        }
    }
}