package com.ar.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import com.ar.R;
import com.ar.dto.Item;
import com.ar.manager.ImageDownloadManager;

public class VIPActivity extends Activity {

    public static final String ITEM = "com.ar.activity.ITEM";
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);

        if(savedInstanceState == null) {
            //Obtengo el item del Intent
            Intent intent = getIntent();
            this.item = (Item)intent.getSerializableExtra(VIPActivity.ITEM);
        } else {
            //Obtengo el Item de la istancia anterior
            this.item = (Item)savedInstanceState.getSerializable(VIPActivity.ITEM);
        }
        loadVIPFragment();
    }

    /**
     * Creo el VIPFragment de ser necesario
     */
    private void loadVIPFragment() {
        if(getFragmentManager().findFragmentById(R.id.vip_fragment) == null){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.vip_fragment, VIPFragment.newInstance(item));
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vi, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(VIPActivity.ITEM, this.item);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ImageDownloadManager.cancelAll();
    }

}
