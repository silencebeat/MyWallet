package candra.bukupengeluaran.Supports.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by silencebeat on 2/16/16.
 */
public class SimpleCache {

    private Context context;
    private SharedPreferences sharedPreferences;
    private String LISTKEY = "DELIV_LIST_KEY";
    private String HASHMAPKEY = "DELIV_HASHMAP_KEY";

    public SimpleCache(Context context){
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0l);
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public double getDouble(String key) {
        String number = getString(key);
        try {
            double value = Double.parseDouble(number);
            return value;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void putDouble(String key, double value) {
        putString(key, String.valueOf(value));
    }

    public void putString(String key, String value) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public boolean putBooleanArray(String key, boolean[] value) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key + "_size", value.length);

        for (int i = 0; i < value.length; i++)
            editor.putBoolean(key + "_" + i, value[i]);

        return editor.commit();
    }

    public boolean[] getBooleanArray(String key) {

        int size = sharedPreferences.getInt(key + "_size", 0);
        boolean array[] = new boolean[size];
        for (int i = 0; i < size; i++)
            array[i] = sharedPreferences.getBoolean(key + "_" + i, false);

        return array;
    }

    public void putList(String key, ArrayList<String> marray) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        String[] mystringlist = marray.toArray(new String[marray.size()]);
        editor.putString(key, TextUtils.join("‚‗‚", mystringlist));
        editor.apply();
    }

    public ArrayList<String> getList(String key) {
        String[] mylist = TextUtils
                .split(sharedPreferences.getString(key, ""), "‚‗‚");
        ArrayList<String> gottenlist = new ArrayList<>(
                Arrays.asList(mylist));
        return gottenlist;
    }


    public void addList(String key, String id) {
        ArrayList<String> temp = getList(key);
        if (temp != null) {
            temp.add(id);
            putList(key, temp);
        } else {
            temp = new ArrayList<>();
            temp.add(id);
            putList(key, temp);
        }
    }

    public void removeList(String key, String param) {
        ArrayList<String> list = getList(key);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(param)) {
                    list.remove(i);
                }
            }

            putList(key, list);
        }

    }

    public void putObject(String KEY, Object T) {
        String result = new Gson().toJson(T);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, result);
        editor.apply();
    }

    public <T> T getObject(String KEY, Class<T> a) {
        String gson = sharedPreferences.getString(KEY, null);
        if (gson == null) {
            return null;
        } else {
            try {
                return new Gson().fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object stored with key "
                        + KEY + " is instance of other class");
            }
        }
    }

    public void putHashMap(String KEY, HashMap<String, String> map) {
        String result = new Gson().toJson(map).toString();
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(HASHMAPKEY, 0);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(KEY, result);
        prefEditor.apply();
    }

    public HashMap<String, String> getHashmap(String KEY) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(HASHMAPKEY, 0);
        String storedCollection = pref.getString(KEY, null);
        HashMap<String, String> collection;
        Type mapType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        collection = new Gson().fromJson(storedCollection, mapType);
        return collection;
    }

    public void putListHashMap(String KEY, ArrayList<HashMap<String, String>> marray) {
        String result = new Gson().toJson(marray).toString();
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(LISTKEY, 0);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(KEY, result);
        prefEditor.apply();
    }

    public ArrayList<HashMap<String, String>> getListHashmap(String KEY) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(LISTKEY, 0);
        String storedCollection = pref.getString(KEY, null);
        ArrayList<HashMap<String, String>> collection;
        Type listType = new TypeToken<ArrayList<HashMap<String, String>>>() {
        }.getType();
        collection = new Gson().fromJson(storedCollection, listType);
        return collection;
    }

    public void addListHashMap(String KEY, HashMap<String, String> map) {
        ArrayList<HashMap<String, String>> tempMarray;
        tempMarray = getListHashmap(KEY);
        if (tempMarray != null) {
            tempMarray.add(map);
            putListHashMap(KEY, tempMarray);
        } else {
            tempMarray = new ArrayList<>();
            tempMarray.add(map);
            putListHashMap(KEY, tempMarray);
        }
    }

    public void deleteListHashmap(String KEY, int index) {
        ArrayList<HashMap<String, String>> tempMarray = getListHashmap(KEY);
        tempMarray.remove(index);
        putListHashMap(KEY, tempMarray);
    }

    public void deleteAllListHashMap(String KEY) {
        putListHashMap(KEY, null);
    }

    public void putListInt(String key, ArrayList<Integer> marray) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Integer[] mystringlist = marray.toArray(new Integer[marray.size()]);
        editor.putString(key, TextUtils.join("‚‗‚", mystringlist));
        editor.apply();
    }

    public ArrayList<Integer> getListInt(String key) {
        String[] mylist = TextUtils
                .split(sharedPreferences.getString(key, ""), "‚‗‚");
        ArrayList<String> gottenlist = new ArrayList<>(
                Arrays.asList(mylist));
        ArrayList<Integer> gottenlist2 = new ArrayList<>();
        for (int i = 0; i < gottenlist.size(); i++) {
            gottenlist2.add(Integer.parseInt(gottenlist.get(i)));
        }

        return gottenlist2;
    }

    public void putListBoolean(String key, ArrayList<Boolean> marray) {
        ArrayList<String> origList = new ArrayList<>();
        for (Boolean b : marray) {
            if (b == true) {
                origList.add("true");
            } else {
                origList.add("false");
            }
        }
        putList(key, origList);
    }

    public ArrayList<Boolean> getListBoolean(String key) {
        ArrayList<String> origList = getList(key);
        ArrayList<Boolean> mBools = new ArrayList<>();
        for (String b : origList) {
            if (b.equals("true")) {
                mBools.add(true);
            } else {
                mBools.add(false);
            }
        }
        return mBools;
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, true);
    }

    public boolean getBoolean(String key, boolean def) {
        return sharedPreferences.getBoolean(key, def);
    }

    public void putFloat(String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key) {
        return sharedPreferences.getFloat(key, 0f);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
