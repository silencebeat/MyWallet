package candra.bukupengeluaran.Views.ViewHolders;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Calendar;

import candra.bukupengeluaran.Entities.Model.CalendarModel;
import candra.bukupengeluaran.Modules.OnCalendarSelectedListener;
import candra.bukupengeluaran.R;
import candra.bukupengeluaran.databinding.ItemCalendarBinding;

/**
 * Created by Candra Triyadi on 06/10/2017.
 */

public class CalendarViewHolder extends RecyclerView.ViewHolder {

    ItemCalendarBinding content;

    public CalendarViewHolder(View itemView) {
        super(itemView);
        content = DataBindingUtil.bind(itemView);
    }

    public void onBind(final CalendarModel model, long millisSelectedDate, final long millisNowDate, final OnCalendarSelectedListener listener, final OnCalendarSelectedListener originListener){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(model.getMillisDate());
        content.txtHari.setText(model.getName());
        content.txtTanggal.setText(""+calendar.get(Calendar.DAY_OF_MONTH));

        if (model.getMillisDate() <= millisNowDate){
            if (calendar.getTimeInMillis() == millisSelectedDate){
                content.txtTanggal.setTextColor(Color.parseColor("#ffffff"));
                content.txtTanggal.setBackgroundResource(R.drawable.view_circle_calendar_selected);
            }else{
                content.txtTanggal.setTextColor(Color.parseColor("#d9000000"));
                content.txtTanggal.setBackgroundResource(R.drawable.view_circle_calendar_non_selected);
            }
        }else{
            content.txtTanggal.setTextColor(Color.parseColor("#40000000"));
        }


        content.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (model.getMillisDate() <= millisNowDate){
                    listener.onCalendarSelected(model.getMillisDate());
                    originListener.onCalendarSelected(model.getMillisDate());
                }
            }
        });

    }
}
