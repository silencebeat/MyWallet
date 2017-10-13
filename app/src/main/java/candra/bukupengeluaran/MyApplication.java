package candra.bukupengeluaran;

import android.app.Application;

import com.kobakei.ratethisapp.RateThisApp;
import com.onesignal.OneSignal;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Candra Triyadi on 06/10/2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myname.is.bento")
                .schemaVersion(2)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        // Custom condition: 3 days and 5 launches
        RateThisApp.Config configRate = new RateThisApp.Config(3, 5);
        RateThisApp.init(configRate);
    }
}
