package candra.bukupengeluaran.Supports.Data;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import candra.bukupengeluaran.Entities.Model.TransaksiModel;
import io.realm.Realm;
import io.realm.RealmResults;
import xyz.truenight.support.realm.RealmHook;
import xyz.truenight.support.realm.RealmSupportGsonFactory;

/**
 * Created by Candra Triyadi on 06/10/2017.
 */

public class DBHelper {

    private static DBHelper instance;
    private final Realm realm;

    private DBHelper(Application application){
        realm = Realm.getDefaultInstance();
    }

    public static DBHelper with(Fragment fragment){
        if (instance == null){
            instance = new DBHelper(fragment.getActivity().getApplication());
        }

        return instance;
    }

    public static DBHelper with(Activity activity){
        if (instance == null){
            instance = new DBHelper(activity.getApplication());
        }
        return instance;
    }

    public static DBHelper with(Application application){
        if (instance == null){
            instance = new DBHelper(application);
        }

        return instance;
    }

    public static DBHelper getInstance(){
        return instance;
    }

    public Realm getRealm(){
        return realm;
    }


    public void clearAll(){
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    public RealmResults<TransaksiModel> getPayment(long millisTanggal){
        return realm.where(TransaksiModel.class).equalTo("timeMillis", millisTanggal).equalTo("stats", 0).findAll();
    }

    public RealmResults<TransaksiModel> getIncome(long millisTanggal){
        return realm.where(TransaksiModel.class).equalTo("timeMillis", millisTanggal).equalTo("stats", 1).findAll();
    }

    public RealmResults<TransaksiModel> getAll(){
        return realm.where(TransaksiModel.class).findAll();
    }

    public String getAllJson(){
        RealmResults<TransaksiModel> all = realm.where(TransaksiModel.class).findAll();
        Gson gson = RealmSupportGsonFactory.create(new RealmHook() {
            @Override
            public Realm instance() {
                return Realm.getDefaultInstance();
            }
        });

        return "{\"list\"="+gson.toJson(all)+"}";
    }

    public void deleteRecordById(long id){
        TransaksiModel model = realm.where(TransaksiModel.class)
                .equalTo("id", id)
                .findFirst();
        realm.beginTransaction();
        model.deleteFromRealm();
        realm.commitTransaction();
    }

    public Number getTotalByDay(long value, int stats){
        Number sum = realm.where(TransaksiModel.class)
                .equalTo("timeMillis", value)
                .equalTo("stats",stats)
                .sum("jumlah");
        return sum;
    }

    public Number getTotalByDateMonthYear(float date, float month, float year, int stats){
        Number sum = realm.where(TransaksiModel.class)
                .equalTo("date", date)
                .equalTo("month", month)
                .equalTo("year", year)
                .equalTo("stats",stats).sum("jumlah");
        return sum;
    }

    public Number getTotalByMonth(float month, float year, int stats){
        Number sum = realm.where(TransaksiModel.class)
                .equalTo("month", month)
                .equalTo("year", year)
                .equalTo("stats",stats)
                .sum("jumlah");
        return sum;
    }

    public Number getTotalByYear(float value, int stats){
        Number sum = realm.where(TransaksiModel.class)
                .equalTo("year", value)
                .equalTo("stats",stats)
                .sum("jumlah");
        return sum;
    }

    public RealmResults<TransaksiModel> getGroupByMonthInOneYear(float month, float year, int stats){
        RealmResults<TransaksiModel> result = realm.where(TransaksiModel.class)

                .equalTo("month", month)
                .equalTo("year", year)
                .equalTo("stats",stats)
                .findAll();

        return result;
    }

    public RealmResults<TransaksiModel> getGroupByDateInOneMonth(float date, float month, int stats){

        RealmResults<TransaksiModel> result = realm.where(TransaksiModel.class)

                .equalTo("month", month)
                .equalTo("date", date)
                .equalTo("stats",stats)
                .findAll();

        return result;
    }

    public void insertData(TransaksiModel model){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(model.getTimeMillis());
        realm.beginTransaction();
        long nextID = 1;
        if (realm.where(TransaksiModel.class).max("id") != null){
            nextID = (long) realm.where(TransaksiModel.class).max("id")+1;
        }
        model.setId(nextID);
        model.setDate(calendar.get(Calendar.DAY_OF_MONTH));
        model.setMonth(calendar.get(Calendar.MONTH));
        model.setYear(calendar.get(Calendar.YEAR));
        realm.copyToRealmOrUpdate(model);
        realm.commitTransaction();
    }

    public void insertAll(ArrayList<TransaksiModel> list){
        realm.beginTransaction();
//        realm.deleteAll();
//        realm.insert(list);
        realm.insertOrUpdate(list);
        realm.commitTransaction();
    }


}
