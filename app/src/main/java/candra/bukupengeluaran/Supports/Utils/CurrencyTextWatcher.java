package candra.bukupengeluaran.Supports.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Candra Triyadi on 22/09/2017.
 */

public class CurrencyTextWatcher implements TextWatcher {
    private final EditText mEditText;
    private String prevString;
    private static final int MAX_DECIMAL = 3;
    private String prefix = "";

    public CurrencyTextWatcher(EditText editText) {
        mEditText = editText;
    }

    public synchronized void afterTextChanged(Editable s) {

        String str = s.toString();
        if (str.length() < prefix.length()) {
            mEditText.setText(prefix);
            mEditText.setSelection(prefix.length());
            return;
        }
        if (str.equals(prefix)) {
            return;
        }
        String cleanString = str.replace(prefix, "").replaceAll("[,]", "");
        if (cleanString.equals(prevString) || cleanString.isEmpty()) {
            return;
        }
        prevString = cleanString;

        String formattedString;
        if (cleanString.contains(".")) {
            formattedString = formatDecimal(cleanString);
        } else {
            formattedString = formatInteger(cleanString);
        }

        mEditText.setText(formattedString);
        mEditText.setSelection(formattedString.length());
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    private String formatInteger(String str) {
        BigDecimal parsed = new BigDecimal(str);
        DecimalFormat formatter;
        formatter = new DecimalFormat(prefix + "#,###");
        return formatter.format(parsed);
    }

    private String formatDecimal(String str) {
        if (str.equals(".")) {
            return prefix + ".";
        }
        BigDecimal parsed = new BigDecimal(str);
        DecimalFormat formatter;
        formatter = new DecimalFormat(prefix + "#,###." + getDecimalPattern(str));
        return formatter.format(parsed);
    }

    private String getDecimalPattern(String str) {
        int decimalCount = str.length() - 1 - str.indexOf(".");
        String decimalPattern = "";
        for (int i = 0; i < decimalCount && i < MAX_DECIMAL; i++) {
            decimalPattern += "0";
        }
        return decimalPattern;
    }

}
