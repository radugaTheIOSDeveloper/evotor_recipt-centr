package com.example.centrrecipt;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIUserKKT {


    public static final String LOG_TAG = "apic";

    String inn;
    String rn_kkt;
    String fn_num;


    public String getJSONString (String UrlSpec) throws IOException {



        MediaType JSON = MediaType.parse("application/json");



        FormBody body = new FormBody.Builder()
                .add("inn", inn)
                .add("rn_kkt", rn_kkt)
                .add("fn_num", fn_num)

                .build();

//        OkHttpClient client = new OkHttpClient.Builder()
//                .followSslRedirects(false)
//                .followRedirects(false)
//                //   .cache(cache)
//                .hostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                })
//                .build();

         OkHttpClient client = new OkHttpClient();

        String result = null;


        try {

            Request request = new Request.Builder()
                    .url(UrlSpec)
                    //   .cacheControl(CacheControl.FORCE_CACHE)
                    //  .cacheControl(new CacheControl.Builder().onlyIfCached().build())
                           .addHeader("Authorization", "Bearer " + "49936d14fd59419310dfa27028e242dfdf80e5f0")
                    //         .addHeader("Authorization", "Token " + "api_token")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            result = response.body().string();

            Log.d("log tag ", "status " + response.code());


        } catch (IOException ioe){

            Log.e(LOG_TAG, "Ошибка загрузки данных  туточки");

        }

        return result;

    }



    public List<ItemKKT> itemKKTS(String _inn,String _rn_kkt, String _fn_num){

        List<ItemKKT> itemKKTS = new ArrayList<>();

        inn = _inn;
        rn_kkt = _rn_kkt;
        fn_num = _fn_num;

        try {
            String url = Uri.parse("https://avto.infosaver.ru/api/v0/user-kkt-info/")
                    .buildUpon()
                    .appendQueryParameter("format", "json")
                    .build().toString();



            String jsonString = getJSONString(url);
            parseItems(itemKKTS, jsonString);



        } catch (IOException ioe){
            Log.e(LOG_TAG, "Ошибка загрузки данных", ioe);

        }catch (JSONException joe){
            Log.e(LOG_TAG, "Ошибка парсинга JSON", joe);
        }
        return itemKKTS;


    }

    //TODO api and adapter

    private void parseItems (List<ItemKKT> itemKKTS , String jsonstring) throws IOException, JSONException {

        Log.d("LOG_TAG", "jsonString Start Api = " + jsonstring);


    }

}
