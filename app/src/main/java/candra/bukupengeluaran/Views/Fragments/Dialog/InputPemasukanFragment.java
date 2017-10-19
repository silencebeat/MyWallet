package candra.bukupengeluaran.Views.Fragments.Dialog;

import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.math.BigDecimal;

import candra.bukupengeluaran.Modules.OnInputPemasukanListener;
import candra.bukupengeluaran.Modules.OnInputPengeluaranListener;
import candra.bukupengeluaran.R;
import candra.bukupengeluaran.Supports.Utils.CurrencyTextWatcher;
import candra.bukupengeluaran.databinding.FragmentInputPemasukanBinding;

/**
 * Created by Candra Triyadi on 07/10/2017.
 */

public class InputPemasukanFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    FragmentInputPemasukanBinding content;
    OnInputPemasukanListener listener;

    private void onAttachToParentFragment(Fragment fragment){
        try{
            listener = (OnInputPemasukanListener) fragment;
        }catch (ClassCastException e){
            throw new ClassCastException(
                    fragment.toString() + " must implement OnInputPemasukanListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAttachToParentFragment(getParentFragment());
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;

        window.setLayout((int) (width * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        content = DataBindingUtil.inflate(inflater, R.layout.fragment_input_pemasukan, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        content.btnAdd.setOnClickListener(this);
        content.editJumlah.addTextChangedListener(new CurrencyTextWatcher(content.editJumlah));

        return content.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == content.btnAdd.getId()){
            if (content.editJumlah.getText().toString().trim().isEmpty() || content.editNama.getText().toString().trim().isEmpty()){
                Snackbar.make(getView(), getResources().getString(R.string.cannot_empty), Snackbar.LENGTH_LONG).show();
            }else{
                dismiss();
                listener.onInputPemasukan(content.editNama.getText().toString(),
                        Double.parseDouble(content.editJumlah.getText().toString().replace(",","")));
            }
        }
    }
}
