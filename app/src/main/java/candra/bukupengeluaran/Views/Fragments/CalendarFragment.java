package candra.bukupengeluaran.Views.Fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

import candra.bukupengeluaran.Entities.Model.CalendarModel;
import candra.bukupengeluaran.Modules.OnCalendarSelectedListener;
import candra.bukupengeluaran.R;
import candra.bukupengeluaran.Supports.Data.SimpleCache;
import candra.bukupengeluaran.Supports.Utils.Adapter;
import candra.bukupengeluaran.Supports.Utils.SpanningLinearLayoutManager;
import candra.bukupengeluaran.Views.ViewHolders.CalendarViewHolder;
import candra.bukupengeluaran.databinding.FragmentCalendarBinding;

/**
 * Created by Candra Triyadi on 06/10/2017.
 */

public class CalendarFragment extends Fragment implements OnCalendarSelectedListener{

    FragmentCalendarBinding content;
    static final String START_DATE = "START_DATE";
    public static final String SELECTED_DATE = "SELECTED_DATE";
    String hari[];
    int day[] = {Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};
    long startDate, nowDate;
    public long selectedDate;
    Calendar calStart;
    OnCalendarSelectedListener listener;
    Adapter<CalendarModel, CalendarViewHolder> adapter;
    ArrayList<CalendarModel> arrayList;
    SimpleCache simpleCache;

    public static CalendarFragment Instance(long startDate, long selectedDate){
        CalendarFragment fragment = new CalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(START_DATE, startDate);
        bundle.putLong(SELECTED_DATE, selectedDate);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void onAttachToParentFragment(Fragment fragment){
        try{
            listener = (OnCalendarSelectedListener) fragment;
        }catch (ClassCastException e){
            throw new ClassCastException(
                    fragment.toString() + " must implement OnCalendarSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAttachToParentFragment(getParentFragment());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        content = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);
        simpleCache = new SimpleCache(getActivity());
        calStart = Calendar.getInstance();
        calStart.setFirstDayOfWeek(Calendar.SUNDAY);
        startDate = getArguments().getLong(START_DATE);
        calStart.setTimeInMillis(startDate);
        nowDate = getNow().getTimeInMillis();
        hari = getActivity().getResources().getStringArray(R.array.arr_daay_short);
        populateList();
        setView();
        setList();
        return content.getRoot();
    }

    public void setView(){

        ViewCompat.setNestedScrollingEnabled(content.list, false);
        if (simpleCache == null)
            simpleCache = new SimpleCache(getActivity());
        selectedDate = simpleCache.getLong(SELECTED_DATE);


        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    private void setList(){
        adapter = new Adapter<CalendarModel, CalendarViewHolder>(R.layout.item_calendar,
                CalendarViewHolder.class, CalendarModel.class, arrayList) {
            @Override
            protected void bindView(CalendarViewHolder holder, CalendarModel model, int position) {
                holder.onBind(model, selectedDate, nowDate, listener, CalendarFragment.this);
            }
        };

        SpanningLinearLayoutManager layoutManager = new SpanningLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        content.list.setLayoutManager(layoutManager);
        content.list.setAdapter(adapter);
        content.list.setHasFixedSize(true);
    }

    void populateList(){
        arrayList = new ArrayList<>();
        for (int i=0; i<7 ; i++){
            CalendarModel model = new CalendarModel();
            model.setMillisDate(getTimeMillisPerDay(day[i]));
            model.setName(hari[i]);
            arrayList.add(model);
        }
    }

    long getTimeMillisPerDay(int day){
        calStart.set(Calendar.DAY_OF_WEEK, day);
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        return calStart.getTimeInMillis();
    }

    @Override
    public void onCalendarSelected(long millis) {
        selectedDate = millis;
        simpleCache.putLong(SELECTED_DATE, millis);
        adapter.notifyDataSetChanged();
    }

    Calendar getNow(){
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }
}
