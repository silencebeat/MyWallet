package candra.bukupengeluaran.Views.Fragments.Home;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import candra.bukupengeluaran.Entities.List.ListTheme;
import candra.bukupengeluaran.Entities.Model.Currency;
import candra.bukupengeluaran.Entities.Model.QuoteModel;
import candra.bukupengeluaran.Entities.Model.Quotes;
import candra.bukupengeluaran.Entities.Model.TransaksiModel;
import candra.bukupengeluaran.Modules.OnCalendarSelectedListener;
import candra.bukupengeluaran.Modules.OnDeleteRecordListener;
import candra.bukupengeluaran.Modules.OnInputPemasukanListener;
import candra.bukupengeluaran.Modules.OnInputPengeluaranListener;
import candra.bukupengeluaran.R;
import candra.bukupengeluaran.Modules.Presenter.ParseImagePresenter;
import candra.bukupengeluaran.Supports.Data.DBHelper;
import candra.bukupengeluaran.Supports.Data.SimpleCache;
import candra.bukupengeluaran.Supports.Utils.Adapter;
import candra.bukupengeluaran.Supports.Utils.FragmentAdapter;
import candra.bukupengeluaran.Supports.Utils.SimpleViewAnimator;
import candra.bukupengeluaran.Supports.Utils.StaticVariable;
import candra.bukupengeluaran.Views.Fragments.CalendarFragment;
import candra.bukupengeluaran.Views.Fragments.Dialog.InputPemasukanFragment;
import candra.bukupengeluaran.Views.Fragments.Dialog.InputPengeluaranFragment;
import candra.bukupengeluaran.Views.ViewHolders.TransaksiViewHolder;
import candra.bukupengeluaran.databinding.FragmentHomeBinding;
import io.realm.Realm;

/**
 * Created by Candra Triyadi on 06/10/2017.
 */

public class HomeFragment extends Fragment implements OnCalendarSelectedListener, View.OnClickListener,
        ViewPager.OnPageChangeListener, Observer, Toolbar.OnMenuItemClickListener, OnInputPengeluaranListener,
        OnInputPemasukanListener, OnDeleteRecordListener{

    FragmentHomeBinding content;
    ParseImagePresenter parseImagePresenter;
    Calendar calendarSelected;
    FragmentAdapter adapter;
    SimpleCache simpleCache;
    String arrBulan[];
    String arrHari[];
    ArrayList<String> collectionImage = new ArrayList<>();
    int indexBackgroundPosition = 0;
    String KEYWORD;
    Realm realm;
    long millisSelected;
    ListTheme listTheme;
    Handler handler;

    @Override
    public void onDestroy() {
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        content = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        simpleCache = new SimpleCache(getContext());
        parseImagePresenter = new ParseImagePresenter(getActivity());
        parseImagePresenter.addObserver(this);
        realm = DBHelper.with(this).getRealm();
        handler = new Handler();
        listTheme = new ListTheme();
        KEYWORD = listTheme.getArrayList().get(0).getName();

        if (simpleCache.getString(StaticVariable.THEME_SELECTED_NAME)!= null){
            KEYWORD = simpleCache.getString(StaticVariable.THEME_SELECTED_NAME);
        }

        startQuotesText();

        if (simpleCache.getList(KEYWORD).size() > 0){
            collectionImage = simpleCache.getList(KEYWORD);
            //Collections.shuffle(collectionImage);
            refreshBackground();
        }else{
            parseImagePresenter.getData(KEYWORD);
        }

        setView();
        return content.getRoot();
    }

    void setView(){
        setHasOptionsMenu(true);
        content.toolbar.inflateMenu(R.menu.menu);
        content.toolbar.setOnMenuItemClickListener(this);
        content.toolbar.getMenu().getItem(0).setVisible(false);

        arrBulan = getActivity().getResources().getStringArray(R.array.arr_month);
        arrHari = getActivity().getResources().getStringArray(R.array.arr_day);

        content.fabTambah.setOnClickListener(this);
        content.fabIncome.setOnClickListener(this);
        content.fabPayment.setOnClickListener(this);

        ViewCompat.setNestedScrollingEnabled(content.listIncome, false);
        ViewCompat.setNestedScrollingEnabled(content.list, false);

//        content.containerList.setAnimationRes(R.anim.slide_down, R.anim.slide_up);
//        content.containerListIncome.setAnimationRes(R.anim.slide_down, R.anim.slide_up);

        setExpandContainer(content.containerList, content.btnExpand, content.imgArrow);
        setExpandContainer(content.containerListIncome, content.btnExpandIncome, content.imgArrowIncome);

        calendarSelected = Calendar.getInstance();
        calendarSelected.set(Calendar.HOUR_OF_DAY, 0);
        calendarSelected.set(Calendar.MINUTE, 0);
        calendarSelected.set(Calendar.SECOND, 0);
        calendarSelected.set(Calendar.MILLISECOND, 0);
        simpleCache.putLong(CalendarFragment.SELECTED_DATE, calendarSelected.getTimeInMillis());
        millisSelected = calendarSelected.getTimeInMillis();
        onCalendarSelected(calendarSelected.getTimeInMillis());

        indexBackgroundPosition = 0;

        adapter = new FragmentAdapter(getChildFragmentManager(), false);
        for (int i = -100 ; i < 1; i++){
            adapter.addFragment(CalendarFragment.Instance(getAnotherWeek(i).getTimeInMillis(),calendarSelected.getTimeInMillis()), "");
        }

        content.pagerCalendar.setAdapter(adapter);
        content.pagerCalendar.setCurrentItem(adapter.getCount()-1);
        content.pagerCalendar.setOffscreenPageLimit(1);
        content.pagerCalendar.addOnPageChangeListener(this);
    }

    private void setExpandContainer(final SimpleViewAnimator view, View btnExpand,final ImageView imgArrow){

        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.isShown()){
                    imgArrow.animate().rotation(0);
                    view.setVisibility(View.GONE);
                }else{
                    imgArrow.animate().rotation(-180);
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onCalendarSelected(long millis) {
        Calendar calendar = getCalFromMillis(millis);
        String prefix;
        if (millis == getNow().getTimeInMillis()){
            prefix = getResources().getString(R.string.prefix_today)+", ";
        }else if (millis == getAnotherDay(-1).getTimeInMillis()){
            prefix = getResources().getString(R.string.prefix_yesterday)+", ";
        }else{
            prefix = arrHari[calendar.get(Calendar.DAY_OF_WEEK)-1]+", ";
        }

        String bulan = arrBulan[calendar.get(Calendar.MONTH)];
        String tahun = ""+calendar.get(Calendar.YEAR);
        content.txtTitle.setText(bulan+" "+tahun);
        content.txtDay.setText(prefix);

        loadDataPengeluaran(millis);
        loadDataPemasukan(millis);
        millisSelected = millis;
    }

    private void loadDataPengeluaran(long millis){

        ArrayList<TransaksiModel> arrayList = new ArrayList(DBHelper.with(this).getPayment(millis));

        Number number = DBHelper.with(this).getTotalByDay(millis, 0);
        content.txtTitleParent.setText(numberFormat(number));

        Adapter<TransaksiModel, TransaksiViewHolder> adapter = new Adapter<TransaksiModel, TransaksiViewHolder>(R.layout.item_transaksi,
                TransaksiViewHolder.class, TransaksiModel.class, arrayList) {
            @Override
            protected void bindView(TransaksiViewHolder holder, TransaksiModel model, int position) {
                holder.onBind(model, HomeFragment.this, getCurrencySymbol());
            }
        };

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        content.list.setLayoutManager(layoutManager);
        content.list.setAdapter(adapter);
    }

    private void loadDataPemasukan(long millis){
        ArrayList<TransaksiModel> arrayList = new ArrayList(DBHelper.with(this).getIncome(millis));

        Number number = DBHelper.with(this).getTotalByDay(millis, 1);
        content.txtTitleIncome.setText(numberFormat(number));

        Adapter<TransaksiModel, TransaksiViewHolder> adapter = new Adapter<TransaksiModel, TransaksiViewHolder>(R.layout.item_transaksi,
                TransaksiViewHolder.class, TransaksiModel.class, arrayList) {
            @Override
            protected void bindView(TransaksiViewHolder holder, TransaksiModel model, int position) {
                holder.onBind(model, HomeFragment.this, getCurrencySymbol());
            }
        };

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        content.listIncome.setLayoutManager(layoutManager);
        content.listIncome.setAdapter(adapter);
    }

    private String getCurrencySymbol(){
        String currency = "$";

        if (simpleCache.getObject(StaticVariable.CURRENCY_SELECTED, Currency.class) != null){
            Currency curr = simpleCache.getObject(StaticVariable.CURRENCY_SELECTED, Currency.class);
            currency = curr.getSymbol();
        }

        return currency;
    }

    private String numberFormat(Number number){
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol(getCurrencySymbol()+" ");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        String nominal =  formatter.format(number).trim();
        return nominal;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position != adapter.getCount()-1){
            content.toolbar.getMenu().getItem(0).setVisible(true);
        }else{
            content.toolbar.getMenu().getItem(0).setVisible(false);
        }
        try {
            ((CalendarFragment) adapter.getmFragments().get(position)).setView();
            if (position > collectionImage.size() - 1){
                indexBackgroundPosition = position % (collectionImage.size() - 1);
            }else{
                indexBackgroundPosition = position;
            }
            refreshBackground();
        }catch (NullPointerException e){

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == content.fabTambah.getId()){
            toogleSubMenuFab();
        }else if (v.getId() == content.fabPayment.getId()){
            InputPengeluaranFragment fragment = new InputPengeluaranFragment();
            fragment.show(getChildFragmentManager(), "payment");
        }else if (v.getId() == content.fabIncome.getId()){
            InputPemasukanFragment fragment = new InputPemasukanFragment();
            fragment.show(getChildFragmentManager(), "income");
        }
    }

    Calendar getAnotherWeek(int num) {
        // TODO Auto-generated method stub
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.WEEK_OF_MONTH, num);
        return c;
    }

    Calendar getAnotherDay(int num) {
        // TODO Auto-generated method stub
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, num);
        return c;
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

    Calendar getCalFromMillis(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    void refreshBackground(){

        if (collectionImage.size() > 0){
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(getActivity())
                    .setDefaultRequestOptions(requestOptions)
                    .asBitmap()
                    .load(collectionImage.get(indexBackgroundPosition))
                    .into(content.imgBackground);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        collectionImage = (ArrayList<String>) arg;
        refreshBackground();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_back:
                simpleCache.putLong(CalendarFragment.SELECTED_DATE, calendarSelected.getTimeInMillis());
                onCalendarSelected(calendarSelected.getTimeInMillis());
                content.pagerCalendar.setCurrentItem(adapter.getCount()-1);
                onPageSelected(adapter.getCount()-1);
                return true;
        }
        return false;
    }

    private void toogleSubMenuFab(){

        if (content.containerFabIncome.isShown()){
            content.containerFabIncome.animate().alpha(0.0f).setDuration(300);
            content.containerFabPayment.animate().alpha(0.0f).setDuration(300);
            content.containerFabIncome.setVisibility(View.INVISIBLE);
            content.containerFabPayment.setVisibility(View.INVISIBLE);
//            content.fabTamb.ah.animate().rotation(0);
        }else{
            content.containerFabIncome.setVisibility(View.VISIBLE);
            content.containerFabPayment.setVisibility(View.VISIBLE);
            content.containerFabIncome.animate().alpha(1.0f).setDuration(300);
            content.containerFabPayment.animate().alpha(1.0f).setDuration(300);
//            content.fabTambah.animate().rotation(45);
        }

    }

    @Override
    public void onInputPengeluaran(String nama, double jumlah) {

        TransaksiModel model = new TransaksiModel();
        model.setJumlah(jumlah);
        model.setNama(nama);
        model.setStats(0);
        model.setTimeMillis(millisSelected);
        DBHelper.getInstance().insertData(model);
        loadDataPengeluaran(millisSelected);
    }

    @Override
    public void onInputPemasukan(String nama, double jumlah) {
        TransaksiModel model = new TransaksiModel();
        model.setJumlah(jumlah);
        model.setNama(nama);
        model.setStats(1);
        model.setTimeMillis(millisSelected);
        DBHelper.getInstance().insertData(model);
        loadDataPemasukan(millisSelected);
    }

    @Override
    public void onDelete(long id) {
        DBHelper.getInstance().deleteRecordById(id);
        loadDataPemasukan(millisSelected);
        loadDataPengeluaran(millisSelected);
    }

    int indexQuote = 0;
    private int mInterval = 300000;
    ArrayList<QuoteModel> arrayList;
    Quotes quotes;

    public void startQuotesText() {
        quotes = simpleCache.getObject(StaticVariable.QUOTES, Quotes.class);
        arrayList = quotes.getArrayList();
        Collections.shuffle(arrayList);
        quotesChanger.run();
    }

    Runnable quotesChanger = new Runnable() {
        @Override
        public void run() {
            try {
                String quotes = arrayList.get(indexQuote).getQuotes();
                String author = arrayList.get(indexQuote).getAuthor();
                content.txtQuote.setText("\""+quotes+"\"\n- "+author);
                indexQuote++;
                if (indexQuote > arrayList.size()-1){
                    indexQuote = 0;
                }
            } finally {
                handler.postDelayed(quotesChanger, mInterval);
            }
        }
    };
}
