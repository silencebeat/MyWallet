package candra.bukupengeluaran.Views.ViewHolders;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import candra.bukupengeluaran.Entities.Model.TransaksiModel;
import candra.bukupengeluaran.Modules.OnDeleteRecordListener;
import candra.bukupengeluaran.databinding.ItemTransaksiBinding;

/**
 * Created by Candra Triyadi on 06/10/2017.
 */

public class TransaksiViewHolder extends RecyclerView.ViewHolder {

    ItemTransaksiBinding content;

    public TransaksiViewHolder(View itemView) {
        super(itemView);
        content = DataBindingUtil.bind(itemView);
    }

    public void onBind(final TransaksiModel model, final OnDeleteRecordListener listener, String currencySymbol){
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol(currencySymbol+" ");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        String nominal =  formatter.format(new BigDecimal(model.getJumlah())).trim();
        content.txtJumlah.setText(nominal);
        content.txtNama.setText(model.getNama());

        content.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(model.getId());
            }
        });
    }
}
