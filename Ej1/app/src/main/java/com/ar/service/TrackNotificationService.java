package com.ar.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ar.R;
import com.ar.dbhelper.ItemDbHelper;
import com.ar.dto.Item;
import com.ar.task.ItemDetailsTask;

import java.util.List;

/**
 * Created by jucciani on 04/05/14.
 */
public class TrackNotificationService extends IntentService {

    NotificationManager notificationManager;
    private static int MY_NOTIFICATION_ID=1;
    private static final int SLEEP_TIME = 20000;


    public TrackNotificationService(){
        super("TrackNotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Permite saber si existe una instancia del servicio ejecutandose.
     * @param activity
     * @return
     */
    public static boolean isMyServiceRunning(Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackNotificationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ItemDbHelper mDbHelper = new ItemDbHelper(this);


        //Obtengo todos los Items de la DB
        List<Item> itemList = mDbHelper.getAllItems();
        while(itemList != null && !itemList.isEmpty()){
            for(Item item:itemList){
                //Item item = itemList.get(0);
                try {
                    //TDOO Negrada, cambiar esto
                    //Obtengo los detalles actualizados del Item
                    Item newItem = new ItemDetailsTask(null).execute(item.getId()).get();
                    if(item.getPrice() == newItem.getPrice()){
                        newItem.setPrice(item.getPrice());
                        sendNotification(newItem);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            //Espero un tiempo determinado antes de ejecutar la prómixa búsqueda
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (Exception e ){
                //e.printStackTrace();
            }
            itemList = mDbHelper.getAllItems();
        }
        Log.d("TrackNotificationService", "FINISHED");
        stopSelf();
    }

    private void sendNotification(Item item){
        String notificationText = getString(R.string.newPrceTxt) + item.getPrice();
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(item.getTitle())
                .setContentText(notificationText)
                .setTicker(getString(R.string.newPrice))
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .build();

        notificationManager.notify(MY_NOTIFICATION_ID++, notification);
    }

}
