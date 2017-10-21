package candra.bukupengeluaran.Views.Fragments.Setting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.kobakei.ratethisapp.RateThisApp;
import com.onesignal.OneSignal;

import java.util.ArrayList;

import candra.bukupengeluaran.Entities.Model.Currency;
import candra.bukupengeluaran.Entities.Model.Firestore.Users;
import candra.bukupengeluaran.Entities.Model.TransaksiModel;
import candra.bukupengeluaran.Entities.Model.TransaksiModels;
import candra.bukupengeluaran.Modules.Wireframe.Wireframe;
import candra.bukupengeluaran.R;
import candra.bukupengeluaran.Supports.Data.DBHelper;
import candra.bukupengeluaran.Supports.Data.SimpleCache;
import candra.bukupengeluaran.Supports.Utils.StaticVariable;
import candra.bukupengeluaran.databinding.FragmentSettingGeneralBinding;

import static android.content.ContentValues.TAG;

/**
 * Created by Candra Triyadi on 08/10/2017.
 */

public class SettingGeneralFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    enum StatusData {
        restore,backup
    }

    FragmentSettingGeneralBinding content;
    SimpleCache simpleCache;
    GoogleApiClient mGoogleApiClient;
    static final int RC_SIGN_IN = 100;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    StatusData statusData = StatusData.backup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        content = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_general, container, false);
        simpleCache = new SimpleCache(getContext());
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        setView();
        return content.getRoot();
    }

    void setView(){
        content.btnDeleteCache.setOnClickListener(this);
        content.btnPrivacyPolicy.setOnClickListener(this);
        content.btnRatingApp.setOnClickListener(this);
        content.btnDeleteDatabase.setOnClickListener(this);
        content.btnCurrency.setOnClickListener(this);
        content.btnGetFromCloud.setOnClickListener(this);
        content.btnSaveToCloud.setOnClickListener(this);
        content.btnLogout.setOnClickListener(this);

        boolean a = simpleCache.getBoolean(StaticVariable.IS_SUBSCRIBE_PUSH);
        content.switchPush.setChecked(a);

        content.switchPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OneSignal.setSubscription(isChecked);
                simpleCache.putBoolean(StaticVariable.IS_SUBSCRIBE_PUSH, isChecked);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        String email = simpleCache.getString(StaticVariable.EMAIL);
        if ( email != null){
            content.btnLogout.setVisibility(View.VISIBLE);
            content.txtEmail.setText(email);
        }else{
            clear();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setCurrency();
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    void setCurrency(){
        if (simpleCache.getObject(StaticVariable.CURRENCY_SELECTED, Currency.class) != null){
            Currency currency = simpleCache.getObject(StaticVariable.CURRENCY_SELECTED, Currency.class);
            content.btnCurrency.setText(currency.getSymbol());
        }
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        }else if (v.getId() == content.btnCurrency.getId()){
            Wireframe.getInstance().toCurrencyView(getContext());
        }else if (v.getId() == content.btnGetFromCloud.getId()){
            statusData = StatusData.restore;
            if (isLogin()){
                downloadFromCloud();
            }else{
                loginGoogle();
            }
        }else if (v.getId() == content.btnSaveToCloud.getId()){
            statusData = StatusData.backup;
            if (isLogin()){
                uploadToCloud();
            }else{
                loginGoogle();
            }
        }else if (v.getId() == content.btnLogout.getId()){
            logoutGoogle();
        }
    }

    void uploadToCloud(){
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        Users users = new Users(simpleCache.getString(StaticVariable.EMAIL), DBHelper.with(this).getAllJson());

        db.collection("users").document(simpleCache.getString(StaticVariable.EMAIL))
                .set(users)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        progressDialog.dismiss();
                        Snackbar.make(getView(), "Data saved", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        progressDialog.dismiss();
                        Snackbar.make(getView(), "Backup error", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    void downloadFromCloud(){
        progressDialog.setMessage("Restoring data...");
        progressDialog.show();
        DocumentReference docRef = db.collection("users").document(simpleCache.getString(StaticVariable.EMAIL));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                progressDialog.dismiss();
                if (documentSnapshot != null){
                    Users users = documentSnapshot.toObject(Users.class);
                    TransaksiModels transaksiModels =  new Gson().fromJson(users.getData(), TransaksiModels.class);
                    ArrayList<TransaksiModel> list = transaksiModels.getList();
                    DBHelper.with(SettingGeneralFragment.this).insertAll(list);
                    Snackbar.make(getView(), "Data restored", Snackbar.LENGTH_LONG).show();
                }else{
                    Snackbar.make(getView(), "Restoring failed", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    boolean isLogin(){
        return simpleCache.getBoolean(StaticVariable.ISLOGIN, false);
    }

    void loginGoogle(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    void logoutGoogle(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()){
                            clear();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null){
                simpleCache.putString(StaticVariable.EMAIL,acct.getEmail());
                simpleCache.putBoolean(StaticVariable.ISLOGIN, true);
                content.btnLogout.setVisibility(View.VISIBLE);
                content.txtEmail.setText(acct.getEmail());
                if (statusData == StatusData.backup){
                    uploadToCloud();
                }else if (statusData == StatusData.restore){
                    downloadFromCloud();
                }
            }

        } else {
            clear();
        }
    }

    private void clear(){
        content.txtEmail.setText("*Google Account Required");
        content.btnLogout.setVisibility(View.INVISIBLE);
        simpleCache.putString(StaticVariable.EMAIL,null);
        simpleCache.putBoolean(StaticVariable.ISLOGIN, false);
    }
}
