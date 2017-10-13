package candra.bukupengeluaran.Views.Activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import candra.bukupengeluaran.R;
import candra.bukupengeluaran.databinding.ActivityPrivacyPolicyBinding;

/**
 * Created by Candra Triyadi on 13/10/2017.
 */

public class PrivacyPolicyActivity extends AppCompatActivity {

    ActivityPrivacyPolicyBinding content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy);
        setToolbar();

        content.webview.loadUrl("file:///android_asset/privacy_policy.html");
    }

    void setToolbar(){
        setSupportActionBar(content.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
