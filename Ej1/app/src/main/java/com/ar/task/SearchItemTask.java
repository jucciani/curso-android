package com.ar.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;

import com.ar.dto.Item;
import com.ar.activity.ListItemsActivity;

public class SearchItemTask extends AsyncTask<String, Void, ArrayList<Item>> {
    private static int MAX_ITEMS_TO_SHOW = 100;
    private static final String SEARCH_ITEM_URI = "https://api.mercadolibre.com/sites/MLA/search";
    private static final String SEARCH_ITEM_QUERY_PARAM = "q";

    private ListItemsActivity activity;
	
	public SearchItemTask(ListItemsActivity activity) {
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected ArrayList<Item> doInBackground(String... args) {
		InputStream is = null;
		JSONObject result = null;
	        
	    try {
            String query =  URLEncoder.encode(args[0], "utf-8");
            String uri = Uri.parse(SearchItemTask.SEARCH_ITEM_URI)
                    .buildUpon()
                    .appendQueryParameter(SearchItemTask.SEARCH_ITEM_QUERY_PARAM, query)
                    .build().toString();
	        URL url = new URL(uri);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        is = conn.getInputStream();

	        // Creo un JSONObject con la respuesta
		    result = new JSONObject(readIt(is));

	    // Cerramos el InputStream.
	    } catch(Exception e) {
	    		
	    } finally {
	        if (is != null) {
	            try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } 
	    }
	    return getItemsArray(result);
	}

	@Override
	protected void onPostExecute(ArrayList<Item> results) {
		super.onPostExecute(results);
        //Si tengo activity, muestro el resultado.
		if(this.activity != null) this.activity.showResults(results);
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

    /**
     * Convierte el JSONObject a una lista de Items
     * @param result
     * @return
     */
    private ArrayList<Item> getItemsArray(JSONObject result) {
        ArrayList<Item> newList = new ArrayList<Item>();
        if(result != null) {
            try {
                JSONArray resultsArray = result.getJSONArray("results");
                //Convierto el JSONArray a una lista de Items
                int maxItems = Math.max(resultsArray.length(), SearchItemTask.MAX_ITEMS_TO_SHOW);
                for (int i = 0; i < maxItems; i++) {
                    try {
                        newList.add(new Item(resultsArray.getJSONObject(i)));
                    } catch (JSONException e) {
                        //Hubo un error parseando el item, no lo agrego a la lista.
                    }
                }
            } catch (JSONException e1) {
                //Si hubo un error al parsear el JSONArray, devuelvo la lista vacía.
            }
        }
        return newList;
    }


    public void detach() {
		this.activity = null;
	}

}
