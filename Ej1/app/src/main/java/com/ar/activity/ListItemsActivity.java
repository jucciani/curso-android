package com.ar.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentTransaction;
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
        SearchFragment searchFrag = (SearchFragment)
                getFragmentManager().findFragmentById(R.id.search_fragment);

        if(searchFrag == null){
            SearchFragment newFragment = new SearchFragment();
            Bundle args = new Bundle();
            args.putString(SearchFragment.QUERY, this.query);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.search_fragment, newFragment);
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
