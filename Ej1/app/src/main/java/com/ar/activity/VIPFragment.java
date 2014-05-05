package com.ar.activity;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ar.R;
import com.ar.dto.Item;
import com.ar.service.TrackItemService;
import com.ar.task.ItemDetailsTask;
import com.ar.view.ItemImageView;

/**
 * Created by jucciani on 03/05/14.
 */
public class VIPFragment extends Fragment implements ItemDetailsTask.ItemDetailsListener{
    public static final String ITEM = "com.ar.activity.ITEM";
    private static final String TASK_COMPLETED = "com.ar.activity.TASK_COMPLETED";
    private static final String TRACKED_ITEM = "com.ar.activity.TRACKED_ITEM";

    private Item item;
    private ItemDetailsTask task;
    private Button trackButton;
    private ProgressBar trackItemProgress;
    private boolean trackedItem = false;

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
            this.trackedItem = savedInstanceState.getBoolean(VIPFragment.TRACKED_ITEM);
        }
        if(shouldFillDetails){
            //Si todavía no cargué los detalles, los busco.
            fillItemDetails();
        }
        //Cargo la vista con los detalles del Item
        updateItemDetails(this.item, view);

        initTrackingSettings(view);

        return view;
    }

    public void initTrackingSettings(View view){
        //Obtengo los componentes que se utilizaran para evitar haver findViewById de mas y seteo el
        //listener del botón.
        this.trackButton = (Button)view.findViewById(R.id.track_item);
        this.trackItemProgress = (ProgressBar)view.findViewById(R.id.track_item_progress);
        if(this.trackedItem){
            notifyTrackedItem();
        }
        this.trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTrackButtonClick();
            }
        });

        //Escucha si el item estaba trackeado.
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_ITEM);
        intentFilter.addAction(DELETED_ITEM);
        bManager.registerReceiver(bReceiver, intentFilter);
        //Busco el Item
        Intent intent = new Intent(getActivity(), TrackItemService.class);
        intent.putExtra(TrackItemService.ACTION_NAME,TrackItemService.READ_ITEM);
        intent.putExtra(TrackItemService.ITEM,this.item);
        getActivity().startService(intent);
    }
    public void fillItemDetails(){
        //Verifico la conectividad
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //Si hay conexión, ejecuto la búsqueda
        if (networkInfo != null && networkInfo.isConnected()) {
            this.task = new ItemDetailsTask(this);
            this.task.execute(this.item.getId());
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
        outState.putSerializable(VIPFragment.ITEM, this.item);
        outState.putBoolean(VIPFragment.TRACKED_ITEM, this.trackedItem);

        //Verifico si se ejecuto el task
        if(this.task != null && this.task.getStatus() == AsyncTask.Status.FINISHED) {
            outState.putBoolean(VIPFragment.TASK_COMPLETED, true);
        } else if(this.task != null && !AsyncTask.Status.FINISHED.equals(this.task.getStatus())){
            //Si se creó el asyncTask y se está ejecutando, cancelo el task.
            this.task.detach();
            this.task.cancel(true);
            outState.putBoolean(VIPFragment.TASK_COMPLETED,false);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Saco el activity actual de la lista de receivers.
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(getActivity());
        bManager.unregisterReceiver(this.bReceiver);
    }

    public void onTrackButtonClick() {
        //Inicio el servicio para guardar o borrar el Item de la db según corresponda
        Intent intent = new Intent(getActivity(), TrackItemService.class);
        if(!this.trackedItem){
            intent.putExtra(TrackItemService.ACTION_NAME,TrackItemService.SAVE_ITEM);
        } else {
            intent.putExtra(TrackItemService.ACTION_NAME,TrackItemService.DELETE_ITEM);
        }
        intent.putExtra(TrackItemService.ITEM,this.item);
        getActivity().startService(intent);
    }

    //Your activity will respond to this action String
    public static final String RECEIVE_ITEM = "com.ar.activity.RECEIVE_ITEM";
    public static final String DELETED_ITEM = "com.ar.activity.DELETED_ITEM";


    /**
     * Obtengo una notificación del servicio y ejecuto la acción correspondiente.
     */
    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(RECEIVE_ITEM)){
                Item item = (Item)intent.getSerializableExtra(ITEM);
                if(item != null){
                    notifyTrackedItem();
                } else {
                    notifyUntrackedItem();
                }
            } else if(intent.getAction().equals(DELETED_ITEM)){
                notifyUntrackedItem();
            }
        }
    };

    public void notifyTrackedItem(){
        this.trackButton.setText(getString(R.string.untrackItem));
        this.trackedItem = true;
        this.trackButton.setVisibility(View.VISIBLE);
        if(this.trackItemProgress != null){
            this.trackItemProgress.setVisibility(View.GONE);
            this.trackItemProgress = null;
        }
    }
    public void notifyUntrackedItem(){
        this.trackButton.setText(getString(R.string.trackItem));
        this.trackedItem = false;
        this.trackButton.setVisibility(View.VISIBLE);
        if(this.trackItemProgress != null){
            this.trackItemProgress.setVisibility(View.GONE);
            this.trackItemProgress = null;
        }
    }
}
