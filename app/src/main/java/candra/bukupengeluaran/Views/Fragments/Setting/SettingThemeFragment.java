package candra.bukupengeluaran.Views.Fragments.Setting;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import candra.bukupengeluaran.Entities.List.ListTheme;
import candra.bukupengeluaran.Entities.Model.ThemeModel;
import candra.bukupengeluaran.Modules.OnThemeSelectedListener;
import candra.bukupengeluaran.R;
import candra.bukupengeluaran.Supports.Data.SimpleCache;
import candra.bukupengeluaran.Supports.Utils.Adapter;
import candra.bukupengeluaran.Supports.Utils.StaticVariable;
import candra.bukupengeluaran.Views.ViewHolders.SettingThemeViewHolder;
import candra.bukupengeluaran.databinding.FragmentSettingThemeBinding;

/**
 * Created by Candra Triyadi on 08/10/2017.
 */

public class SettingThemeFragment extends Fragment implements OnThemeSelectedListener{

    FragmentSettingThemeBinding content;
    SimpleCache simpleCache;
    String selectedName;
    Adapter<ThemeModel, SettingThemeViewHolder> adapter;
    ListTheme listTheme;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        content = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_theme, container, false);
        simpleCache = new SimpleCache(getActivity());
        listTheme = new ListTheme();
        selectedName = listTheme.getArrayList().get(0).getName();
        setView();
        return content.getRoot();
    }

    private void setView(){

        if (simpleCache.getString(StaticVariable.THEME_SELECTED_NAME) != null){
            selectedName = simpleCache.getString(StaticVariable.THEME_SELECTED_NAME);
        }

        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        adapter = new Adapter<ThemeModel, SettingThemeViewHolder>(R.layout.item_theme,
                SettingThemeViewHolder.class, ThemeModel.class, listTheme.getArrayList()) {
            @Override
            protected void bindView(SettingThemeViewHolder holder, ThemeModel model, int position) {
                holder.onBind(getContext(), model, selectedName, SettingThemeFragment.this);
            }
        };

        content.list.setLayoutManager(manager);
        content.list.setAdapter(adapter);
    }

    @Override
    public void onThemeSelected(String selectedName) {
        simpleCache.putString(StaticVariable.THEME_SELECTED_NAME, selectedName);
        this.selectedName = selectedName;
        adapter.notifyDataSetChanged();
    }
}
