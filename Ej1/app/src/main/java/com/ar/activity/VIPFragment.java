package com.ar.activity;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ar.R;
import com.ar.dto.Item;
import com.ar.task.ItemDetailsTask;
import com.ar.view.ItemImageView;

/**
 * Created by jucciani on 03/05/14.
 */
public class VIPFragment extends Fragment implements ItemDetailsTask.ItemDetailsListener{
    public static final String ITEM = "com.ar.activity.ITEM";
    private static final String TASK_COMPLETED = "com.ar.activity.TASK_COMPLETED";

    private Item item;
    private ItemDetailsTask task;

    /**
     * Crea una nueva instancia del VIPFragment inicializandola con el item.
     */
    public static VIPFragment newInstance(Item item) {
        VIPFragment newFragment = new VIPFragment();
        Bundle args = new Bundle();
        args.putSerializable(VIPFragment.ITEM, item);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean shouldFillDetails = true;
        View view = inflater.inflate(R.layout.vip,container,false);
        if(savedInstanceState == null) {
            //Obtengo el item del Intent
            Bundle args = getArguments();
            this.item = (Item)args.getSerializable(VIPActivity.ITEM);
        } else {
            //Obtengo el Item de la instancia anterior
            this.item = (Item)savedInstanceState.getSerializable(VIPFragment.ITEM);
            shouldFillDetails = !savedInstanceState.getBoolean(VIPFragment.TASK_COMPLETED);
        }
        if(shouldFillDetails){
            fillItemDetails();
        }
        updateItemDetails(this.item, view);
        //return super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    public void fillItemDetails(){
        //Verifico la conectividad
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //Si hay conexión, ejecuto la búsqueda
        if (networkInfo != null && networkInfo.isConnected()) {
            this.task = new ItemDetailsTask(this);
            task.execute(this.item.getId());
        }
    }

    @Override
    public void updateItemDetails(Item item){
        updateItemDetails(item,getView());
    }

    public void updateItemDetails(Item item, View view){
        this.item = item; //Guardo el nuevo item
        if(item != null){
            ((TextView)view.findViewById(R.id.vipItemTitle)).setText(item.getTitle());
            ((TextView)view.findViewById(R.id.vipItemPrice)).setText(getResources().getString(R.string.currency_symbol) + item.getPrice());
            ((TextView)view.findViewById(R.id.vipItemDescription)).setText(item.getSubtitle());
            ((ItemImageView)view.findViewById(R.id.vipItemImage)).setImageURL(item.getPictureURL(), true, getResources().getDrawable(R.drawable.ic_action_picture));
            ((TextView)view.findViewById(R.id.vipItemAvailableQuantity)).setText(item.getAvailableQuantity() + getResources().getString(R.string.in_stock));
            ((TextView)view.findViewById(R.id.vipItemCondition)).setText((item.getCondition() == Item.CONDITION_NEW ? getString(R.string.new_item) : getString(R.string.used_item)));
            ((TextView)view.findViewById(R.id.vipItemLocation)).setText(item.getCity() + " (" + item.getState() +")");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(VIPActivity.ITEM, this.item);

        //Verifico si se ejecuto el task
        if(this.task != null && this.task.getStatus() == AsyncTask.Status.FINISHED) {
            outState.putBoolean(VIPFragment.TASK_COMPLETED, true);
        } else if(task != null && !AsyncTask.Status.FINISHED.equals(task.getStatus())){
            //Si se creó el asyncTask y se está ejecutando, cancelo el task.
            task.detach();
            task.cancel(true);
            outState.putBoolean(VIPFragment.TASK_COMPLETED,false);
        }
        super.onSaveInstanceState(outState);
    }

}
