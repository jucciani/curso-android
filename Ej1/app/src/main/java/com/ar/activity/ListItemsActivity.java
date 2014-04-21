package com.ar.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ar.dto.Item;
import com.ar.R;
import com.ar.task.SearchItemTask;

public class ListItemsActivity extends Activity {
	
	private SearchItemTask task;
	private String query;
	private ArrayList<Item> itemsList;
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
				this.itemsList = savedInstanceState.getParcelableArrayList(ListItemsActivity.ITEM_LIST);
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
		} else if(this.query != null) {
			//Si tengo query, ejecuto la búsqueda.
			searchItems(this.query);
		} else {
			//Sino muestro un error.
        	TextView textView = (TextView) findViewById(R.id.list_item_desc);
            textView.setText(getResources().getString(R.string.error));
		}
	}

    /**
     * Ejecuta la búsqueda en un asynktask basandose en la query ingresada.
     */
	private void searchItems(String query){
	    //Verifico la conectividad
        ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //Si hay conexión, ejecuto la búsqueda
        if (networkInfo != null && networkInfo.isConnected()) {
           this.task = new SearchItemTask(this);
           task.execute(query);
        } else {
        	TextView textView = (TextView) findViewById(R.id.list_item_desc);
            textView.setText(getResources().getString(R.string.no_network));
        }
	}

    /**
     * Obtiene un array con los resultados y los dibuja en pantalla.
     * @param results
     */
	public void showResults(ArrayList<Item> results){
			this.itemsList = results;
			drawItemList(this.itemsList);
	}

    /**
     * Dibuja la lista de items en un ListView.
     */
	private void drawItemList(ArrayList<Item> itemsList){
		//Si tengo items en la lista, dibujo la lista.
		if (!itemsList.isEmpty()){
			ArrayAdapter<Item> arrayAdapter = getItemsArrayAdapter(itemsList);
			ListView lv = new ListView(this);
			
			lv.setAdapter(arrayAdapter); 
			setContentView(lv);			
		} else {
			//Si no hay items, muestro un mensaje.
			TextView textView = (TextView) findViewById(R.id.list_item_desc);
            textView.setText(getResources().getString(R.string.no_items_found));
		}
	}

    /**
     * Devuelve un ArrayAdapter para la lista de items obtenidos.
     * @return
     */
	private ArrayAdapter<Item> getItemsArrayAdapter(ArrayList<Item> itemsList) {
		ArrayAdapter<Item> arrayAdapter = new ArrayAdapter<Item>(this, R.layout.list_item, itemsList){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout itemView;
				//Obtengo el Item actual
				Item item = getItem(position);
				if(convertView==null) {
					itemView = new LinearLayout(getContext());
		            String inflater = Context.LAYOUT_INFLATER_SERVICE;
		            LayoutInflater vi;
		            vi = (LayoutInflater)getContext().getSystemService(inflater);
		            vi.inflate(R.layout.list_item, itemView, true);
		        } else {
		        	itemView = (LinearLayout) convertView;
		        }
				//Obtengo los TextView de la vista
		        TextView itemTitle = (TextView)itemView.findViewById(R.id.itemTitle);
		        TextView itemPrice = (TextView)itemView.findViewById(R.id.itemPrice);
		        TextView itemDescription = (TextView)itemView.findViewById(R.id.itemDescription);
		        TextView itemQuantity = (TextView)itemView.findViewById(R.id.itemQuantity);
		        
		        //Leno los textView con al info correspondiente del item.
	        	itemTitle.setText(item.getTitle());
	        	itemPrice.setText("$" +item.getPrice());
	        	//Si existen los textViews -> estoy en landscape, cargo la info adicional.
	        	if(itemDescription != null && item.getSubtitle() != null) {
	        		itemDescription.setText(item.getSubtitle());
	        	}
	        	if(itemQuantity != null) {
	        		itemQuantity.setText(item.getAvailableQuantity() + " disponibles");
	        	}
	        	//Devuelvo la vista cargada.
				return itemView;
			}
		};
		
        return arrayAdapter;
	}

    /**
     * Guardo el estado actual del Activity (query e itemList), cancelo el AsyncTask si se está ejecutando.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ListItemsActivity.QUERY, this.query);
        //Verifico si ya tengo resultados
        if(itemsList != null) {
            //Guardo los resultados
            outState.putParcelableArrayList(ListItemsActivity.ITEM_LIST, this.itemsList);
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

}
