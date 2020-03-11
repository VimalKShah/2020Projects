package com.borqs.samplesoftkeydemo;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;

public class AlertActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_alert);
    }

    @Override
    public void onResume()
    {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.cancel(RSKOptions.NOTIF_ID);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
