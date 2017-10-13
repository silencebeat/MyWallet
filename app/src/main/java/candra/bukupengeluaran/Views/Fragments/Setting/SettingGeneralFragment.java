package candra.bukupengeluaran.Views.Fragments.Setting;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.kobakei.ratethisapp.RateThisApp;
import com.onesignal.OneSignal;

import candra.bukupengeluaran.Modules.Wireframe.Wireframe;
import candra.bukupengeluaran.R;
import candra.bukupengeluaran.Supports.Data.DBHelper;
import candra.bukupengeluaran.Supports.Data.SimpleCache;
import candra.bukupengeluaran.Supports.Utils.StaticVariable;
import candra.bukupengeluaran.databinding.FragmentSettingGeneralBinding;

/**
 * Created by Candra Triyadi on 08/10/2017.
 */

public class SettingGeneralFragment extends Fragment implements View.OnClickListener{

    FragmentSettingGeneralBinding content;
    SimpleCache simpleCache;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        content = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_general, container, false);
        simpleCache = new SimpleCache(getContext());
        setView();
        return content.getRoot();
    }

    void setView(){
        content.btnDeleteCache.setOnClickListener(this);
        content.btnPrivacyPolicy.setOnClickListener(this);
        content.btnRatingApp.setOnClickListener(this);
        content.btnDeleteDatabase.setOnClickListener(this);

        boolean a = simpleCache.getBoolean(StaticVariable.IS_SUBSCRIBE_PUSH);
        content.switchPush.setChecked(a);

        content.switchPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OneSignal.setSubscription(isChecked);
                simpleCache.putBoolean(StaticVariable.IS_SUBSCRIBE_PUSH, isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == content.btnRatingApp.getId()){
            RateThisApp.showRateDialog(getContext());

        }else if (v.getId() == content.btnPrivacyPolicy.getId()){
            Wireframe.getInstance().toPrivacyPolicyView(getContext());

        }else if (v.getId() == content.btnDeleteCache.getId()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(getActivity()).clearDiskCache();
                    Snackbar.make(getView(), "Cache cleared", Snackbar.LENGTH_LONG).show();
                }
            }).start();
        }else if (v.getId() == content.btnDeleteDatabase.getId()){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getContext());
            }
            builder.setTitle("Delete database")
                    .setMessage("Are you sure you want to delete all database?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DBHelper.with(getActivity()).clearAll();
                            Snackbar.make(getView(), "Database deleted", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
