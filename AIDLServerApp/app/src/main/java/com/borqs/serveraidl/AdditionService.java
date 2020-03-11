package com.borqs.serveraidl;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.List;

import androidx.core.app.ActivityCompat;

public class AdditionService extends Service {

    public AdditionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    private final IAdd.Stub mBinder = new IAdd.Stub() {

        @Override
        public int add(int number1, int number2) throws RemoteException {
            return number1 + number2;
        }

        @Override
        public List<String> getStringList() throws RemoteException {
            return MainActivity.getList();
        }

        @Override
        public List<Person> getPersonList() throws RemoteException {
            return MainActivity.getPersons();
        }

        @Override
        public void placeCall(String number) throws RemoteException {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
            if (ActivityCompat
                    .checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                // use System.currentTimeMillis() to have a unique ID for the pending intent
                PendingIntent pIntent = PendingIntent
                        .getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent1, 0);

                // build notification
                // the addAction re-use the same intent to keep the example short
                Notification n = new Notification.Builder(getApplicationContext())
                        .setContentTitle("AIDL Server App")
                        .setContentText("Please grant call permission from settings")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true).build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, n);
                return;
            }
            startActivity(intent);
        }
    };



}
