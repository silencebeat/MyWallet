package candra.bukupengeluaran.Modules.Presenter;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

import candra.bukupengeluaran.Supports.Data.SimpleCache;
import candra.bukupengeluaran.Supports.Utils.StaticVariable;


/**
 * Created by Candra Triyadi on 14/09/2017.
 */

public class ParseImagePresenter extends Observable{

    Activity activity;
    String userAgent;
    ParseImage parseImage;
    SimpleCache simpleCache;
    String query = null;
    ProgressBar progressBar;

    public ParseImagePresenter(Activity activity, ProgressBar progressBar){
        simpleCache = new SimpleCache(activity);
        this.activity = activity;
        userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
        this.progressBar = progressBar;
    }

    public ParseImagePresenter(Activity activity){
        simpleCache = new SimpleCache(activity);
        this.activity = activity;
        userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
    }

    public void getData(String query){
        if (!StaticVariable.isConnectingToInternet(activity)){

            return;
        }
        if (parseImage != null)
            parseImage.cancel(true);
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        this.query = query;
        parseImage = new ParseImage();
        parseImage.execute(query);
    }

    public void cancelRequest(){
        if (parseImage != null){
            parseImage.cancel(true);
        }
    }

    class ParseImage extends AsyncTask<String, Integer, Document> {
        ArrayList<String> resultUrls = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect("https://www.google.com/search?site=imghp&tbm=isch&source=hp&q="+query+"+site:pixabay.com"+"&gws_rd=cr&tbm=isch&tbs=isz:m")
                        .userAgent(userAgent).referrer("https://www.google.com/")
                        .get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            try{
                Elements elements = doc.select("div.rg_meta");
                JSONObject jsonObject;
                for (Element element : elements) {
                    if (element.childNodeSize() > 0) {
                        jsonObject = (JSONObject) new JSONParser().parse(element.childNode(0).toString());
                        String url = (String) jsonObject.get("ou");
                        resultUrls.add(url);
                    }
                }

                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                Collections.shuffle(resultUrls);
                simpleCache.putList(query, resultUrls);
                notifyToObserver(resultUrls);

            }catch ( ParseException e) {
                e.printStackTrace();
            } catch (Exception ex){
                ex.printStackTrace();
            }

        }
    }

    private void notifyToObserver(ArrayList<String> arrayList){
        setChanged();
        notifyObservers(arrayList);
    }
}
