package candra.bukupengeluaran.Modules.Wireframe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import candra.bukupengeluaran.Views.Activities.CurrencyActivity;
import candra.bukupengeluaran.Views.Activities.HomeActivity;
import candra.bukupengeluaran.Views.Activities.PrivacyPolicyActivity;

/**
 * Created by Candra Triyadi on 06/10/2017.
 */

public class Wireframe {

    private Wireframe(){
    }

    private static class SingleTonHelper{
        private static final Wireframe INSTANCE = new Wireframe();
    }

    public static Wireframe getInstance() {
        return SingleTonHelper.INSTANCE;
    }

    public void toHomeView(Context context){
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    public void toPrivacyPolicyView(Context context){
        Intent intent = new Intent(context, PrivacyPolicyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void toCurrencyView(Context context){
        Intent intent = new Intent(context, CurrencyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
