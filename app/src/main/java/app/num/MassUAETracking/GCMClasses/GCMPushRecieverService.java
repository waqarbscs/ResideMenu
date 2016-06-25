package app.num.MassUAETracking.GCMClasses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import app.num.MassUAETracking.MainActivity;
import app.num.MassUAETracking.R;

/**
 * Created by Imdad on 5/2/2016.
 */
public class GCMPushRecieverService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        //super.onMessageReceived(from, data);
        sendNotification(data.getString("message"));
    }

    public void sendNotification(String message) {

        Intent intent = null;
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestcode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this,requestcode,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("GCM Message X:X")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,noBuilder.build());
    }
}
