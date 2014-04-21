package com.ar.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import com.ar.R;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	public void searchItem(View view) {
		//Obtengo el texto a buscar
	    String query = ((EditText) findViewById(R.id.query)).getText().toString();
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
