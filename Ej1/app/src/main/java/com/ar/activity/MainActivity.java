package com.ar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import com.ar.R;


public class MainActivity extends ActionBarActivity implements SearchItemFragment.SearchItemListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

    @Override
    public void onSearchButtonClick(String query) {
        //Creo el Intent para cargar la nueva Activity
        Intent intent = new Intent(this,ListItemsActivity.class);
        //Le asigno el parametro 'query'
        intent.putExtra(ListItemsActivity.QUERY, query);
        //Ejecuto la nueva activity
        startActivity(intent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
