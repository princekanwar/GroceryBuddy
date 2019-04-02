package com.darshil.grocerybuddy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

public class NotificationHelper extends ContextWrapper {
    private static final String CHANNEL_ID = "com.darshil.grocerybuddy";
    private static final String CHANNEL_NAME = "Grocery Channel";
    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super( base );
        createChannels();
    }

    private void createChannels() {
        NotificationChannel groceryChannel = new NotificationChannel( CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT );
        groceryChannel.enableLights( true );
        groceryChannel.enableVibration( true );
        groceryChannel.setLightColor( Color.GREEN );
        groceryChannel.setLockscreenVisibility( Notification.VISIBILITY_PRIVATE );
        getManager().createNotificationChannel( groceryChannel );
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        return manager;
    }
    public Notification.Builder getGroceryNotification(String title,String body)
    {
        return new Notification.Builder( getApplicationContext(),CHANNEL_ID )
                .setContentText(body)
                .setContentTitle( title)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel( true );
    }
}
