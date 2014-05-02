package com.ar.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ar.adapter.ItemArrayAdapter;
import com.ar.dto.Item;
import com.ar.R;
import com.ar.listener.EndlessScrollListener;
import com.ar.manager.ImageDownloadManager;
import com.ar.task.SearchItemTask;

public class ListItemsActivity extends Activity {

	private SearchItemTask task;
	private String query;
	private ArrayList<Item> itemsList;
    private ArrayAdapter<Item> arrayAdapter;
    private int totalResults = -1;
    private EndlessScrollListener scrollListener;

    private final static int ITEMS_QUERY_LIMIT = 15;
    private final static String ITEMS_TOTAL_RESULTS = "com.ar.activity.ITEMS_TOTAL_RESULTS";
    private final static String ITEM_LIST = "com.ar.activity.ITEM_LIST";
    public final static String QUERY = "com.ar.activity.QUERY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_list_items);
		//Me fijo si se esta recuperando o creando por primera vez.
		if(savedInstanceState != null){
			if(savedInstanceState.containsKey(ListItemsActivity.QUERY)){
				//Obtengo el query del estado anterior
				this.query = savedInstanceState.getString(ListItemsActivity.QUERY);
			}
			if(savedInstanceState.containsKey(ListItemsActivity.ITEM_LIST)){
				//Obtengo la lista de items del estado anterior.
				this.itemsList = (ArrayList<Item>)savedInstanceState.getSerializable(ListItemsActivity.ITEM_LIST);
			}
            if(savedInstanceState.containsKey(ListItemsActivity.ITEMS_TOTAL_RESULTS)){
                //Obtengo la lista de items del estado anterior.
                this.totalResults = savedInstanceState.getInt(ListItemsActivity.ITEMS_TOTAL_RESULTS);
            }
		} else {
			//Obtengo el query del Intent
			Intent intent = getIntent();
			this.query = intent.getStringExtra(ListItemsActivity.QUERY);
		}
		//Dibujo la vista
		if(this.itemsList != null) {
			//Si tengo los items cargados, los dibujo.
			drawItemList(this.itemsList);
            scrollListener.setLoading(false);
		} else if(this.query != null) {
			//Si tengo query, ejecuto la búsqueda.
			searchItems();
		} else {
			//Sino muestro un error.
            showMessage(getResources().getString(R.string.error));
		}
	}

    /**
     * Ejecuta la búsqueda en un asynktask basandose en la query ingresada.
     */
	public void searchItems(){
        //Si ya cargué todos los items disponibles, no ejecuto la consulta.
        if(this.totalResults != -1 && this.itemsList != null && this.itemsList.size() == this.totalResults) {
            return;
        }
	    //Verifico la conectividad
        ConnectivityManager connMgr = (ConnectivityManager)
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //Si hay conexión, ejecuto la búsqueda
        if (networkInfo != null && networkInfo.isConnected()) {
           this.task = new SearchItemTask(this);
           int offset = 0;
            if(this.itemsList != null){
                offset = this.itemsList.size();
            }
           task.execute(query,String.valueOf(ITEMS_QUERY_LIMIT),String.valueOf(offset));
        } else {
            showMessage(getResources().getString(R.string.no_network));
        }
	}

    /**
     * Obtiene un array con los resultados y los dibuja en pantalla.
     * @param results
     */
	public void showResults(ArrayList<Item> results, int totalResults){
        if(this.itemsList == null){
            this.itemsList = results;
            this.totalResults = totalResults;
            drawItemList(results);
        } else if(results != null){
            this.itemsList.addAll(results);
            arrayAdapter.notifyDataSetChanged();
        }
        scrollListener.setLoading(false);

    }

    /**
     * Dibuja la lista de items en un ListView.
     */
	private void drawItemList(ArrayList<Item> itemsList){
		//Si tengo items en la lista, dibujo la lista.
		if (itemsList != null){
			arrayAdapter = getItemsArrayAdapter(itemsList);

            LinearLayout ll = new LinearLayout(this);
            TextView textView = new TextView(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            textView.setText(this.query + getResources().getString(R.string.quantity_separator) + totalResults + getResources().getString(R.string.found_items));
            ll.addView(textView);
			ListView lv = new ListView(this);
            scrollListener = new EndlessScrollListener(this);
            lv.setOnScrollListener(scrollListener);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    Intent intent = new Intent(ListItemsActivity.this, VIPActivity.class);
                    String itemId = ListItemsActivity.this.itemsList.get(pos).getId();
                    intent.putExtra(VIPActivity.ITEM,ListItemsActivity.this.itemsList.get(pos));
                    ImageDownloadManager.cancelAll();
                    ListItemsActivity.this.startActivity(intent);
                }
            });
            lv.setAdapter(arrayAdapter);
            ll.addView(lv);
			setContentView(ll);
		} else {
			//Si no hay items, muestro un mensaje.
			showMessage(getResources().getString(R.string.no_items_found));
		}
	}

    /**
     * Devuelve un ArrayAdapter para la lista de items obtenidos.
     * @return
     */
	private ArrayAdapter<Item> getItemsArrayAdapter(ArrayList<Item> itemsList) {
		ArrayAdapter<Item> arrayAdapter = new ItemArrayAdapter(this,itemsList);
        return arrayAdapter;
	}

    /**
     * Muestra un mensaje por pantalla
     * @param message
     */
    private void showMessage(String message){
        TextView textView = new TextView(this);
        textView.setText(message);
        setContentView(textView);
    }

    /**
     * Guardo el estado actual del Activity (query e itemList), cancelo el AsyncTask si se está ejecutando.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ListItemsActivity.QUERY, this.query);
        outState.putInt(ListItemsActivity.ITEMS_TOTAL_RESULTS, this.totalResults);

        //Verifico si ya tengo resultados
        if(itemsList != null) {
            //Guardo los resultados
            outState.putSerializable(ListItemsActivity.ITEM_LIST, this.itemsList);
        } else if(task != null && !AsyncTask.Status.FINISHED.equals(task.getStatus())){
            //Si se creó el asyncTask y se está ejecutando, cancelo el task y guardo la query.
            task.detach();
            task.cancel(true);
        }
        super.onSaveInstanceState(outState);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_items, menu);
		return true;
	}

    @Override
    protected void onStop() {
        super.onStop();
        ImageDownloadManager.cancelAll();
    }
}
