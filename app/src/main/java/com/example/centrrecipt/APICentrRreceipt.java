package com.example.centrrecipt;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APICentrRreceipt {


    public static final String LOG_TAG = "com";

    String  jsonsss;//

        public String getJSONString (String UrlSpec) throws IOException {

            OkHttpClient client = new OkHttpClient.Builder()
                    .followSslRedirects(false)
                    .followRedirects(false)
                    //   .cache(cache)
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();

            String result = null;

            try {

                Request request = new Request.Builder()
                        .url(UrlSpec)
                        //   .cacheControl(CacheControl.FORCE_CACHE)
                        //  .cacheControl(new CacheControl.Builder().onlyIfCached().build())
                    //       .addHeader("Authorization", "Bearer " + "49936d14fd59419310dfa27028e242dfdf80e5f0")
               //         .addHeader("Authorization", "Token " + "api_token")
                        .build();

                Response response = client.newCall(request).execute();
                result = response.body().string();



            } catch (IOException ioe){
                Log.e(LOG_TAG, "Ошибка загрузки данных  туточки");

            }

            return result;


    }



    public List<ItemCentrRecipt> itemCentrRecipts(){

        List<ItemCentrRecipt> itemCentrRecipts = new ArrayList<>();


        try {
            String url = Uri.parse("https://avto.infosaver.ru/api/v0/centr-receipt/")
                    .buildUpon()
                    .appendQueryParameter("format", "json")
                    .build().toString();


            String jsonString = getJSONString(url);

            parseItems(itemCentrRecipts, jsonString);


        } catch (IOException ioe){
            Log.e(LOG_TAG, "Ошибка загрузки данных", ioe);

        }catch (JSONException joe){
            Log.e(LOG_TAG, "Ошибка парсинга JSON", joe);
        }
        return itemCentrRecipts;


    }

    private void parseItems (List<ItemCentrRecipt> items , String jsonstring) throws IOException, JSONException {

        Log.d(LOG_TAG, "json = " + jsonstring);

        JSONArray array = null;

//
        array= new JSONArray(jsonstring);

        int a = array.length();

        for (int i = 0; i < array.length(); i++) {
            JSONObject nameObject = array.getJSONObject(i);
            ItemCentrRecipt item = new ItemCentrRecipt();
            item.setName(nameObject.getString("name"));
            item.setCoast((float) nameObject.getDouble("coast"));
            item.setType(nameObject.getInt("pay_type"));
            item.setQr_code(nameObject.getString("qr_code"));
            items.add(item);

        }

    }


}
