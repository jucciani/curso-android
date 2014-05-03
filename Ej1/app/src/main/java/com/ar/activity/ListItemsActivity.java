package com.ar.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.ar.dto.Item;
import com.ar.R;
import com.ar.manager.ImageDownloadManager;

public class ListItemsActivity extends Activity implements SearchFragment.OnItemSelectedListener {

	private String query;
    public final static String QUERY = "com.ar.activity.QUERY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_list_items);
		//Me fijo si se esta recuperando o creando por primera vez.
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(SearchFragment.QUERY)){
                //Obtengo el query del estado anterior
                this.query = savedInstanceState.getString(SearchFragment.QUERY);
            }
        } else {
			//Obtengo el query del Intent
			Intent intent = getIntent();
			this.query = intent.getStringExtra(ListItemsActivity.QUERY);
		}
        ((TextView)findViewById(R.id.query_feedback)).setText(query);
        loadSearchFragment();
	}

    private void loadSearchFragment(){
        //Si estoy recuperando no creo uno nuevo
        if(getFragmentManager().findFragmentById(R.id.search_fragment) == null){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.search_fragment, SearchFragment.newInstance(this.query));
            transaction.commit();
        }
    }

    @Override
    public void onSearchItemSelected(Item item) {
        Intent intent = new Intent(ListItemsActivity.this, VIPActivity.class);
        intent.putExtra(VIPActivity.ITEM,item);
        ImageDownloadManager.cancelAll();
        startActivity(intent);
    }

    /**
     * Guardo el estado actual del Activity (query e itemList), cancelo el AsyncTask si se está ejecutando.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ListItemsActivity.QUERY, this.query); 
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
