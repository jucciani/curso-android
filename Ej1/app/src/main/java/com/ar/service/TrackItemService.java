package com.ar.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ar.activity.VIPFragment;
import com.ar.dbhelper.ItemDbHelper;
import com.ar.dto.Item;

/**
 * Created by jucciani on 04/05/14.
 */
public class TrackItemService extends IntentService {

    public static final String ACTION_NAME = "ACTION_NAME";
    public static final String ITEM = "ITEM";
    public static final String SAVE_ITEM = "SAVE_ITEM";
    public static final String READ_ITEM = "READ_ITEM";
    public static final String DELETE_ITEM = "DELETE_ITEM";

    public TrackItemService(){
        super("TrackItemService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getStringExtra(ACTION_NAME);
        Item item = (Item)intent.getSerializableExtra(ITEM);
        ItemDbHelper mDbHelper = new ItemDbHelper(this);
        Intent RTReturn = null;

        if(DELETE_ITEM.equals(action)){
            Log.d("Deleting Item: ", item.getId());
            mDbHelper.deleteItem(item.getId());
            RTReturn = new Intent(VIPFragment.DELETED_ITEM);
        } else {
            if(SAVE_ITEM.equals(action)){
                mDbHelper.saveItem(item);
                Log.d("Saving Item: ", item.getId());

            } else if(READ_ITEM.equals(action)){
                Log.d("Reading Item: ", item.getId());
                item = mDbHelper.readItem(item.getId());
                if(item != null){
                    Log.d("Item Recovered: ", item.getId());
                }
            }
            RTReturn = new Intent(VIPFragment.RECEIVE_ITEM);
            if(item != null){
                RTReturn.putExtra(VIPFragment.ITEM, item);
            }
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn);
    }
}
