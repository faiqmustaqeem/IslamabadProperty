package com.codiansoft.islamabadproperty;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by CodianSoft on 16/02/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    String messageToShow;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Log.d("messageFromServer", remoteMessage.getData().toString());
        }
        if (remoteMessage.getNotification().getBody() != null) {
            Log.e("MessageBody", remoteMessage.getNotification().getBody());
            messageToShow = remoteMessage.getNotification().getBody();
        }


        showNotification(remoteMessage.getData().get("message"));

    }

    private void showNotification(String message) {

        //    Log.e("MessagePrint", message);
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingntent =
                PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Islamabad Property")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentText(messageToShow)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingntent);
        Log.e("UriDeFAULT", String.valueOf(Settings.System.DEFAULT_NOTIFICATION_URI));

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }


}
