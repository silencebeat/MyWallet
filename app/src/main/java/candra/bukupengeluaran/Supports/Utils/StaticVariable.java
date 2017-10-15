package candra.bukupengeluaran.Supports.Utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.Display;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Candra Triyadi on 15/09/2017.
 */

public class StaticVariable {

    public final static String JSON = "JSON";
    public static String WATCH_COUNT = "WATCH_COUNT";
    public final static String THEME = "THEME";
    public final static String QUOTES = "QUOTES";
    public final static String ISLOGIN = "ISLOGIN";
    public final static String THEME_SELECTED_NAME = "THEME_SELECTED_NAME";
    public final static String IS_SUBSCRIBE_PUSH = "IS_SUBSCRIBE_PUSH";
    public final static String CURRENCY_SELECTED = "CURRENCY_SELECTED";
    public final static String EMAIL = "EMAIL";

    public static boolean isConnectingToInternet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static void showDialog(Activity activity, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Hi mate!!");
        builder.setMessage(message);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static <T> T stringToObject(String s, Class<T> a){
        return new Gson().fromJson(s, a);
    }

    public static String objectToString(Object T){
        return new Gson().toJson(T);
    }

    public static Point getSizeScreen(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point screen = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(screen);
        } else {
            screen.x = display.getWidth();
            screen.y = display.getHeight();
        }
        return screen;
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static long[] millisToTime(long millis){
        long time[] = new long[3];
        time[0] = TimeUnit.MILLISECONDS.toHours(millis);
        time[1] = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1);
        time[2] = TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1);
        return time;
    }

    public static long timeToMillis(long hour, long minute, long second){
        return TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute) + TimeUnit.SECONDS.toMillis(second);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int w, int h) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static void showTimePickerDialog(Activity activity, TimePickerDialog.OnTimeSetListener listener){
        Calendar cal = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, listener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    public static void showDatePickerDialog(Activity activity, DatePickerDialog.OnDateSetListener listener, Calendar minDate){
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        if (minDate != null)
            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    public static String getTimeNow(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE - HH:mm", Locale.ENGLISH);
        String tanggalSekarang = simpleDateFormat.format(calendar.getTime());
        return tanggalSekarang;
    }
}
