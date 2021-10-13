package com.example.flake.myapplication.push;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.flake.myapplication.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {
	public static final int notifyID = 9001;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				sendNotification(""	+ extras.get(ApplicationConstants.MSG_KEY));
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String greetMsg) {
		if (!greetMsg.equals("null")) {
			Intent resultIntent = new Intent(this, Main2Activity.class);
			resultIntent.putExtra("greetjson", greetMsg);
			resultIntent.setAction(Intent.ACTION_MAIN);
			resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
					resultIntent, PendingIntent.FLAG_ONE_SHOT);

			NotificationCompat.Builder mNotifyBuilder;
			NotificationManager mNotificationManager;

			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			mNotifyBuilder = new NotificationCompat.Builder(this)
					.setContentTitle("Секреты Москвы")
					.setContentText("Вы выиграли приз. Поздравляем!")
					.setSmallIcon(R.drawable.ic_launcher);
			mNotifyBuilder.setContentIntent(resultPendingIntent);

			int defaults = 0;
			defaults = defaults | Notification.DEFAULT_LIGHTS;
			defaults = defaults | Notification.DEFAULT_VIBRATE;
			defaults = defaults | Notification.DEFAULT_SOUND;

			mNotifyBuilder.setDefaults(defaults);
			mNotifyBuilder.setContentText("Вы выиграли приз. Поздравляем!");
			mNotifyBuilder.setAutoCancel(true);
			mNotificationManager.notify(notifyID, mNotifyBuilder.build());
		}
	}
}
