package com.ar.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.ar.dto.Item;
import com.ar.activity.ListItemsActivity;

public class SearchItemTask extends AsyncTask<String, Void, ArrayList<Item>> {
    private static int MAX_ITEMS_TO_SHOW = 100;
    private static final String SEARCH_ITEM_URI = "https://api.mercadolibre.com/sites/MLA/search";
    private static final String SEARCH_ITEM_QUERY_PARAM = "q";
    private static final String LIMIT_QUERY_PARAM = "limit";
    private static final String OFFSET_QUERY_PARAM = "offset";

    private int totalResults = 0;

    private ResultsHandler resultHandler;

    public interface ResultsHandler {
        public void showResults(ArrayList<Item> results, int totalResults);
       }
	
	public SearchItemTask(ResultsHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

	@Override
	protected ArrayList<Item> doInBackground(String... args) {
		InputStream is = null;
		JSONObject result = null;
        AndroidHttpClient client = null;
	    try {
            String query =  URLEncoder.encode(args[0], "utf-8");
            Uri.Builder uriBuilder = Uri.parse(SearchItemTask.SEARCH_ITEM_URI)
                    .buildUpon()
                    .appendQueryParameter(SearchItemTask.SEARCH_ITEM_QUERY_PARAM, query);
            //Si recibo mas parametros es porque recibo el limit y el offset
            if(args.length > 1){
                uriBuilder.appendQueryParameter(SearchItemTask.LIMIT_QUERY_PARAM,args[1]);
                uriBuilder.appendQueryParameter(SearchItemTask.OFFSET_QUERY_PARAM,args[2]);
            }
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
	    return getItemsArray(result);
	}

	@Override
	protected void onPostExecute(ArrayList<Item> results) {
		super.onPostExecute(results);
        //Si tengo activity, muestro el resultado.
		if(this.resultHandler != null) this.resultHandler.showResults(results, totalResults);
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
        ArrayList<Item> newList = null;
        if(result != null) {
            try {
                this.totalResults = result.getJSONObject("paging").getInt("total");
                JSONArray resultsArray = result.getJSONArray("results");
                //Convierto el JSONArray a una lista de Items
                int maxItems = Math.max(resultsArray.length(), SearchItemTask.MAX_ITEMS_TO_SHOW);
                newList = new ArrayList<Item>();
                for (int i = 0; i < maxItems; i++) {
                    try {
                        newList.add(new Item(resultsArray.getJSONObject(i)));
                    } catch (JSONException e) {
                        //Hubo un error parseando el item, no lo agrego a la lista.
                    }
                }
                if(newList.isEmpty()) newList = null;
            } catch (JSONException e1) {
                //Si hubo un error al parsear el JSONArray, devuelvo la lista vacía.
            }
        }
        return newList;
    }


    public void detach() {
		this.resultHandler = null;
	}

}
