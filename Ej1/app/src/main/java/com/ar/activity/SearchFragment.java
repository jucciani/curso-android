package com.ar.activity;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ar.R;
import com.ar.adapter.ItemArrayAdapter;
import com.ar.dto.Item;
import com.ar.listener.EndlessScrollListener;
import com.ar.task.SearchItemTask;

import java.util.ArrayList;

/**
 * Created by jucciani on 02/05/14.
 */
public class SearchFragment extends ListFragment implements SearchItemTask.ResultsHandler{

    private OnItemSelectedListener activityCallback;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Me fijo si se esta recuperando o creando por primera vez.
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(SearchFragment.QUERY)){
                //Obtengo el query del estado anterior
                this.query = savedInstanceState.getString(SearchFragment.QUERY);
            }
            if(savedInstanceState.containsKey(SearchFragment.ITEM_LIST)){
                //Obtengo la lista de items del estado anterior.
                this.itemsList = (ArrayList<Item>)savedInstanceState.getSerializable(SearchFragment.ITEM_LIST);
            }
            if(savedInstanceState.containsKey(SearchFragment.ITEMS_TOTAL_RESULTS)){
                //Obtengo la lista de items del estado anterior.
                this.totalResults = savedInstanceState.getInt(SearchFragment.ITEMS_TOTAL_RESULTS);
            }
        } else {
            Bundle args = getArguments();
            this.query = args.getString(SearchFragment.QUERY);
        }
        //Dibujo la vista
        if(this.itemsList != null) {
            //Si tengo los items cargados, los dibujo.
            drawItemList(this.itemsList);
        } else if(this.query != null) {
            //Si tengo query, ejecuto la búsqueda.
            searchItems();
        } else {
            //Sino muestro un error.
            showMessage(getResources().getString(R.string.error));
        }
        return super.onCreateView(inflater, container, savedInstanceState);
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
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @Override
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
        if(scrollListener != null){
            scrollListener.setLoading(false);
        }
    }

    /**
     * Dibuja la lista de items en un ListView.
     */
    private void drawItemList(ArrayList<Item> itemsList){

        //Si tengo items en la lista, dibujo la lista.
        if (itemsList != null){
            arrayAdapter = getItemsArrayAdapter(itemsList);
            setListAdapter(arrayAdapter);

            //Actualizo el texto
            TextView textView = (TextView)getActivity().findViewById(R.id.query_feedback);
            textView.setText(this.query + getResources().getString(R.string.quantity_separator) + totalResults + getResources().getString(R.string.found_items));
        } else {
            //Si no hay items, muestro un mensaje.
            showMessage(getResources().getString(R.string.no_items_found));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Asigno el scrollListener a la vista
        scrollListener = new EndlessScrollListener(this);
        getListView().setOnScrollListener(scrollListener);
        if(savedInstanceState != null){
            scrollListener.setLoading(false);
        }
    }

    /**
     * Devuelve un ArrayAdapter para la lista de items obtenidos.
     * @return
     */
    private ArrayAdapter<Item> getItemsArrayAdapter(ArrayList<Item> itemsList) {
        return new ItemArrayAdapter(this.getActivity(),itemsList);
    }


    public interface OnItemSelectedListener {
        public void onSearchItemSelected(Item item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCallback = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SearchItemListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Le informo al listener que se selecciono un item
        activityCallback.onSearchItemSelected(itemsList.get(position));

    }

    /**
     * Muestra un mensaje por pantalla
     * @param message
     */
    private void showMessage(String message){
        TextView textView = new TextView(getActivity());
        textView.setText(message);
        getActivity().setContentView(textView);
    }

    /**
     * Guardo el estado actual del Activity (query e itemList), cancelo el AsyncTask si se está ejecutando.
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SearchFragment.QUERY, this.query);
        outState.putInt(SearchFragment.ITEMS_TOTAL_RESULTS, this.totalResults);

        //Verifico si ya tengo resultados
        if(itemsList != null) {
            //Guardo los resultados
            outState.putSerializable(SearchFragment.ITEM_LIST, this.itemsList);
        } else if(task != null && !AsyncTask.Status.FINISHED.equals(task.getStatus())){
            //Si se creó el asyncTask y se está ejecutando, cancelo el task y guardo la query.
            task.detach();
            task.cancel(true);
        }
        super.onSaveInstanceState(outState);
    }
}
