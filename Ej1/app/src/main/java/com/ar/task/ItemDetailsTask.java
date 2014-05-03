package com.ar.task;

import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.ar.activity.VIPActivity;
import com.ar.dto.Item;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ItemDetailsTask extends AsyncTask<String, Void, Item> {

    private static final String ITEM_DETAILS_URI = "https://api.mercadolibre.com/items/";
    private ItemDetailsListener itemDetailsListener;

    public interface ItemDetailsListener {
        public void updateItemDetails(Item item);
    }
    public ItemDetailsTask(ItemDetailsListener itemDetailsListener){
        this.itemDetailsListener = itemDetailsListener;
    }

    @Override
    protected Item doInBackground(String... args) {
        InputStream is = null;
        JSONObject result = null;
        AndroidHttpClient client = null;
        try {
            String itemId =  URLEncoder.encode(args[0], "utf-8");
            Uri.Builder uriBuilder = Uri.parse(ITEM_DETAILS_URI + itemId)
                    .buildUpon();
            String uri = uriBuilder.build().toString();

            client = AndroidHttpClient.newInstance("Android");
            HttpGet request = new HttpGet(uri);
            HttpResponse response = client.execute(request); //here is where the exception is thrown
            is = response.getEntity().getContent();


            // Creo un JSONObject con la respuesta
            result = new JSONObject(readIt(is));

        } catch(Exception e) {

        } finally {// Cerramos la conexion y el InputStream.

            if(client != null) client.close();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        Item item = null;
        try {
            item = new Item(result);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return item;
    }

    @Override
    protected void onPostExecute(Item item) {
        super.onPostExecute(item);
        if(this.itemDetailsListener != null) this.itemDetailsListener.updateItemDetails(item);
    }

    //Transforma el InputStream a String.
    private String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {

        BufferedReader r = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }

    public void detach(){
        this.itemDetailsListener = null;
    }
}
