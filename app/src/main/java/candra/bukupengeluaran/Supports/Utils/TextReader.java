package candra.bukupengeluaran.Supports.Utils;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import candra.bukupengeluaran.Entities.Model.QuoteModel;
import candra.bukupengeluaran.Entities.Model.Quotes;
import candra.bukupengeluaran.Supports.Data.SimpleCache;

/**
 * Created by Candra Triyadi on 29/05/2017.
 */

public class TextReader {

    Context context;
    SimpleCache simpleDB;
    ReadTextListener readTextListener;

    public interface ReadTextListener{
        void onFinishReadingText();
        void onErrorReadingText();
    }

    public TextReader(Context context, ReadTextListener readTextListener){
        this.context = context;
        simpleDB = new SimpleCache(context);
        this.readTextListener = readTextListener;
        new loadUrls().execute();
    }

    private class loadUrls extends AsyncTask<Void, Void, Quotes>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Quotes doInBackground(Void... params) {
            BufferedReader reader = null;
            ArrayList<QuoteModel> models = new ArrayList<>();
            Quotes datas = new Quotes();
            try {
                reader = new BufferedReader(
                        new InputStreamReader(context.getAssets().open("quotes.txt"), "UTF-8"));

                String mLine;
                while ((mLine = reader.readLine()) != null) {
                    QuoteModel quoteModel = new QuoteModel();
                    String[] meta = mLine.split(";");
                    quoteModel.setQuotes(meta[0]);
                    quoteModel.setAuthor(meta[1]);
                    models.add(quoteModel);
                }
            } catch (IOException e) {
                //log the exception
                return null;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }

            datas.setArrayList(models);
            return datas;
        }

        @Override
        protected void onPostExecute(Quotes datas) {
            if (datas == null){
                readTextListener.onErrorReadingText();
                return;
            }
            simpleDB.putObject(StaticVariable.QUOTES, datas);
            readTextListener.onFinishReadingText();
        }
    }
}
