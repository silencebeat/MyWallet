package candra.bukupengeluaran.Views.Fragments.Home;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import candra.bukupengeluaran.Entities.Model.Currency;
import candra.bukupengeluaran.R;
import candra.bukupengeluaran.Supports.Data.DBHelper;
import candra.bukupengeluaran.Supports.Data.SimpleCache;
import candra.bukupengeluaran.Supports.Utils.StaticVariable;
import candra.bukupengeluaran.databinding.FragmentReportBinding;

/**
 * Created by Candra Triyadi on 06/10/2017.
 */

public class ReportFragment extends Fragment implements View.OnClickListener{

    FragmentReportBinding content;
    String arrMonth[];
    int positionCalendarRange = 0;
    setChart setChart;
    SimpleCache simpleCache;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        content = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false);
        setView();
        return content.getRoot();
    }

    void setView(){
        simpleCache = new SimpleCache(getContext());
        content.btnNext.setOnClickListener(this);
        content.btnPrev.setOnClickListener(this);
        arrMonth = getActivity().getResources().getStringArray(R.array.arr_month);
        setRangeCalendar(positionCalendarRange);
    }

    void setRangeCalendar(int position){
        String prefixMonthandYear;
        String prefixDate;
        Calendar anotherMonth = getAnotherMonth(position);
        String month = arrMonth[anotherMonth.get(Calendar.MONTH)];
        String year = ""+anotherMonth.get(Calendar.YEAR);

        prefixMonthandYear = month+" "+year;

        int tanggal = anotherMonth.get(Calendar.DAY_OF_MONTH);
        prefixDate = "1 - "+tanggal;

        content.txtTitle.setText(prefixMonthandYear);
        content.txtTanggal.setText(prefixDate+" "+prefixMonthandYear);

        if (setChart != null){
            setChart.cancel(true);
        }

        ArrayList<Data> datasIncome = new ArrayList<>();
        ArrayList<Data> datasPayment = new ArrayList<>();

        for (int a=1; a <= tanggal ; a++){
            Data data = new Data();
            Number jm = DBHelper.with(ReportFragment.this).getTotalByDateMonthYear(a,anotherMonth.get(Calendar.MONTH),anotherMonth.get(Calendar.YEAR),1);
            data.setDate(a);
            data.setSum(jm.floatValue()/1000);
            datasIncome.add(data);
        }

        for (int a=1; a <= tanggal ; a++){
            Data data = new Data();
            Number jm = DBHelper.with(ReportFragment.this).getTotalByDateMonthYear(a,anotherMonth.get(Calendar.MONTH),anotherMonth.get(Calendar.YEAR),0);
            data.setDate(a);
            data.setSum(jm.floatValue()/1000);
            datasPayment.add(data);
        }


        setChart = new setChart(1,tanggal,anotherMonth.get(Calendar.MONTH), anotherMonth.get(Calendar.YEAR), datasIncome, datasPayment);
        setChart.execute();
    }

    Calendar getAnotherMonth(int num) {
        // TODO Auto-generated method stub
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MONTH, num);
        return c;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == content.btnNext.getId()){
            positionCalendarRange++;
            setRangeCalendar(positionCalendarRange);

        }else if (v.getId() == content.btnPrev.getId()){
            positionCalendarRange--;
            setRangeCalendar(positionCalendarRange);
        }
    }

    class setChart extends AsyncTask<Void, Void, Entries>{

        ArrayList<Data> arrayListIncome;
        ArrayList<Data> arrayListPayment;
        List<Entry> entriesIncome;
        List<Entry> entriesPayment;
        Entries models;
        int start, end;
        int month, year;

        public setChart(int start, int end, int month, int year, ArrayList<Data> arrayListIncome, ArrayList<Data> arrayListPayment){
            this.arrayListIncome = arrayListIncome;
            this.arrayListPayment = arrayListPayment;
            models = new Entries();
            entriesIncome = new ArrayList<>();
            entriesPayment = new ArrayList<>();
            this.start = start;
            this.end = end;
            this.month = month;
            this.year = year;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Entries doInBackground(Void... params) {

            for (Data data: arrayListIncome){
                entriesIncome.add(new Entry(data.getDate(),data.getSum()));
            }
            models.setEntriesIncome(entriesIncome);

            for (Data data: arrayListPayment){
                entriesPayment.add(new Entry(data.getDate(), data.getSum()));
            }
            models.setEntriesPayment(entriesPayment);

            return models;
        }

        @Override
        protected void onPostExecute(Entries data) {
            super.onPostExecute(data);

            List<ILineDataSet> dataSets = new ArrayList<>();

            if (data.getEntriesIncome().size()>0){
                LineDataSet dataSet = new LineDataSet(data.getEntriesIncome(), "Income (.000)");
                dataSet.setDrawValues(false);
                dataSet.setColor(Color.parseColor("#4caf50"));
                dataSet.setCircleColor(Color.parseColor("#4caf50"));
                dataSets.add(dataSet);
            }

            if (data.getEntriesPayment().size()>0){
                LineDataSet dataSet2 = new LineDataSet(data.getEntriesPayment(), "Payment (.000)");
                dataSet2.setDrawValues(false);
                dataSet2.setColor(Color.parseColor("#f44336"));
                dataSet2.setCircleColor(Color.parseColor("#f44336"));
                dataSets.add(dataSet2);
            }

            if (data.getEntriesIncome().size()<0 && data.getEntriesPayment().size()<0){
                return;
            }

            LineData lineData = new LineData(dataSets);
            content.chart.setData(lineData);
            content.chart.setPinchZoom(false);
            content.chart.setDragEnabled(false);
            content.chart.invalidate();

            Number income = DBHelper.with(ReportFragment.this).getTotalByMonth(month, year, 1);
            Number payment = DBHelper.with(ReportFragment.this).getTotalByMonth(month, year, 0);

            content.txtTotalIncome.setText(numberFormat(income));
            content.txtTotalPayment.setText(numberFormat(payment));

            if (income.floatValue() > payment.floatValue()){
                content.txtInfo.setText(getResources().getString(R.string.good_condition));
            }else if (payment.floatValue() > income.floatValue()){
                content.txtInfo.setText(getResources().getString(R.string.pay_attention));
            }else if (payment.floatValue() == income.floatValue()){
                content.txtInfo.setText(getResources().getString(R.string.no_data));
            }
        }
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

    class Entries{
        List<Entry> entriesIncome = new ArrayList<>();
        List<Entry> entriesPayment = new ArrayList<>();

        public List<Entry> getEntriesIncome() {
            return entriesIncome;
        }

        public List<Entry> getEntriesPayment() {
            return entriesPayment;
        }

        public void setEntriesIncome(List<Entry> entriesIncome) {
            this.entriesIncome = entriesIncome;
        }

        public void setEntriesPayment(List<Entry> entriesPayment) {
            this.entriesPayment = entriesPayment;
        }
    }

    class Data{
        float sum;
        int date;

        public void setDate(int date) {
            this.date = date;
        }

        public void setSum(float sum) {
            this.sum = sum;
        }

        public float getSum() {
            return sum;
        }

        public int getDate() {
            return date;
        }
    }

}
