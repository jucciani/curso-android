package com.ar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.ar.R;
import com.ar.dto.Item;
import com.ar.manager.ImageDownloadManager;
import com.ar.task.ItemDetailsTask;
import com.ar.view.ItemImageView;

public class VIPActivity extends Activity {

    public static final String ITEM = "com.ar.activity.ITEM";
    private static final String TASK_COMPLETED = "com.ar.activity.TASK_COMPLETED";

    private Item item;
    private ItemDetailsTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        boolean shouldFillDetails = true;

        if(savedInstanceState == null) {
            //Obtengo el item del Intent
            Intent intent = getIntent();
            this.item = (Item)intent.getSerializableExtra(VIPActivity.ITEM);
        } else {
            //Obtenog el Item de la istancia anterior
            this.item = (Item)savedInstanceState.getSerializable(VIPActivity.ITEM);
            shouldFillDetails = !savedInstanceState.getBoolean(VIPActivity.TASK_COMPLETED);
        }
        if(shouldFillDetails){
            fillItemDetails();
        }
        updateItemDetails(this.item);
    }

    public void fillItemDetails(){
        //Verifico la conectividad
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //Si hay conexión, ejecuto la búsqueda
        if (networkInfo != null && networkInfo.isConnected()) {
            this.task = new ItemDetailsTask(this);
            task.execute(this.item.getId());
        }
    }

    public void updateItemDetails(Item item){
        this.item = item; //Guardo el nuevo item
        if(item != null){
            ((TextView)this.findViewById(R.id.vipItemTitle)).setText(item.getTitle());
            ((TextView)this.findViewById(R.id.vipItemPrice)).setText(getResources().getString(R.string.currency_symbol) + item.getPrice());
            ((TextView)this.findViewById(R.id.vipItemDescription)).setText(item.getSubtitle());
            ((ItemImageView)this.findViewById(R.id.vipItemImage)).setImageURL(item.getPictureURL(), true, getResources().getDrawable(R.drawable.ic_action_picture));
            ((TextView)this.findViewById(R.id.vipItemAvailableQuantity)).setText(item.getAvailableQuantity() + getResources().getString(R.string.in_stock));
            ((TextView)this.findViewById(R.id.vipItemCondition)).setText((item.getCondition() == Item.CONDITION_NEW ? getString(R.string.new_item) : getString(R.string.used_item)));
            ((TextView)this.findViewById(R.id.vipItemLocation)).setText(item.getCity() + " (" + item.getState() +")");
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

        //Verifico si se ejecuto el task
        if(this.task != null && this.task.getStatus() == AsyncTask.Status.FINISHED) {
            outState.putBoolean(VIPActivity.TASK_COMPLETED, true);
        } else if(task != null && !AsyncTask.Status.FINISHED.equals(task.getStatus())){
            //Si se creó el asyncTask y se está ejecutando, cancelo el task.
            task.detach();
            task.cancel(true);
            outState.putBoolean(VIPActivity.TASK_COMPLETED,false);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onStop() {
        super.onStop();
        ImageDownloadManager.cancelAll();
    }
}
