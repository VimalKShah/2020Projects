package com.borqs.samplesoftkeydemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ActionMenuView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

/**
 * Menu handling Phase#1
 *
 * 1> If there is a Menu, add RSK Options on Nav bar irrespective of number of Menu items.
 * 2> Handle RSK event to open menu
 * 3> On Menu open put Close on LSK
 * 4> On Menu close, restore the RSK Options.
 */
public class RSKOptions extends Activity {
    private static final String LOG_TAG = "RSKOptions";
    private Button mButton0;
    private boolean mIsMenuOpened;
    private static final String CHANNEL_ID = "Sample Channel";
    public static final int NOTIF_ID = 1;

    private MultiAutoCompleteTextView mAtv;

    private final static String[] testData = {"Anchorage", "Barcelona", "Cairo", "Delhi", "Estonia",
              "Finland", "Geneva", "Helsinki", "Iceland", "Jaipur", "Kenya"};


    private boolean mIsMenuButtonHidden = false;

    private void hideActionMenuView() {

        int resourceId = getResources().getIdentifier("action_bar", "id", "android");
        ViewGroup actionbar = (ViewGroup) getWindow().getDecorView().findViewById(resourceId);
        Log.d(LOG_TAG, "in hideActionMenuView");
        if (actionbar != null) {
            for (int i = 0; i < actionbar.getChildCount(); i++) {
                View v = actionbar.getChildAt(i);
                if (v != null && v instanceof ActionMenuView) {
                    v.setVisibility(View.GONE);
                    mIsMenuButtonHidden = true;
                    break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "onCreate called");
        setContentView(R.layout.activity_rskoptions);

        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(
                (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                    if (!mIsMenuButtonHidden) {
                        hideActionMenuView();
                    }
            }
        }
        ));

        mButton0 = findViewById(R.id.button_test0);
        mAtv = findViewById(R.id.fieldPhone);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, testData);
        mAtv.setThreshold(1); //will start working from first character
        mAtv.setAdapter(adapter);
        mAtv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    @Override
    protected void onResume()
    {
        // This is done for NoActionBar theme apps since onCreateOptionsMenu and onPrepareOptionsMenu
        // don't get called each time
        SoftKeyMenuUtils.setRSKMenuLabel(this, SoftKeyMenuUtils.LABEL_RIGHT_OPTIONS);
        super.onResume();
    }

    public void onButtonZeroClicked(View v){
        Log.e(LOG_TAG, "on Click of Button 0 called");
    }

    public void onButtonOneClicked(View v) {
        Log.e(LOG_TAG, "on Click of Button 1 called");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(getLayoutInflater().inflate(R.layout.alert_content, null));

        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.e(LOG_TAG, "on Positive Click in Dialog");
             }
        });

        builder.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.e(LOG_TAG, "on Negative Click in Dialog");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void notifyFullScreen() {
        Intent intent = new Intent(this, AlertActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("My full screen notification")
                .setContentText("Hello Full Screen!")
                .setFullScreenIntent(pendingIntent, true)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIF_ID, builder.build());
    }

    /**
     * Called when a panel's menu is opened by the user.
     *
     * Below description from docs can be ignored as it is applicable for older
     * versions of android
     * "This may also be called when the menu is
     * changing from one type to another
     * (for example, from the icon menu to the expanded menu)."
     *
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        Log.e(LOG_TAG, "onMenuOpened");
        mIsMenuOpened = true;
        // case 3>Set LSK label to "Close" on menu open
        // SoftKeyMenuUtils.setLSKMenuLabels(this, getString(R.string.menu_close), mTextView);
        // Activity return true
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        Log.e(LOG_TAG, "onPanelClosed");
        mIsMenuOpened = false;
        // case 4b> show "Options" on panel close
        // SoftKeyMenuUtils.setRSKMenuLabels(this, getString(R.string.menu_options), mTextView);
        super.onPanelClosed(featureId, menu);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        Log.e(LOG_TAG, "onOptionsMenuClosed");
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(LOG_TAG, "onCreateOptionsMenu");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.e(LOG_TAG, "oPrepareOptionsMenu");
        getMenuInflater().inflate(R.menu.actions, menu);
        SoftKeyMenuUtils.setRSKMenuLabel(this, SoftKeyMenuUtils.LABEL_RIGHT_OPTIONS);

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(LOG_TAG, "onOptionsItemSelected");
        switch(item.getItemId())
        {
            case R.id.lsk:
                // SoftKeyMenuUtils.setLSKMenuLabels(this, "LEFT");
                createNotificationChannel();
                notifyFullScreen();
                return true;
            case R.id.rsk:
                SoftKeyMenuUtils.setRSKMenuLabel(this, SoftKeyMenuUtils.LABEL_RIGHT_OPTIONS );
                return true;
            case R.id.csk:
                SoftKeyMenuUtils.setCSKMenuLabel(this, SoftKeyMenuUtils.LABEL_CENTER_SELECT);
                return true;
            case R.id.all:
                SoftKeyMenuUtils.setSoftKeyMenuLabels(this, SoftKeyMenuUtils.LABEL_LEFT_BACK,
                        SoftKeyMenuUtils.LABEL_CENTER_SELECT, SoftKeyMenuUtils.LABEL_RIGHT_OPTIONS, false);
                return true;
            case R.id.blank:
                SoftKeyMenuUtils.setSoftKeyMenuLabels(this, SoftKeyMenuUtils.LABEL_LEFT_BACK,
                        SoftKeyMenuUtils.LABEL_CENTER_BLANK, SoftKeyMenuUtils.LABEL_RIGHT_BLANK, false);
                return true;
            case R.id.standard:
                SoftKeyMenuUtils.setSoftKeyMenuLabels(this, SoftKeyMenuUtils.LABEL_LEFT_BACK,
                        SoftKeyMenuUtils.LABEL_CENTER_SELECT, SoftKeyMenuUtils.LABEL_RIGHT_OPTIONS, false);
                return true;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e(LOG_TAG, "onKeyUp "+event);
        return super.onKeyUp(keyCode, event);
    }

    // TBD: LSK/RSK keys events are not received when Menu is open.
    // Currently "Back" key closes menu.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(LOG_TAG, "onKeyDown"+ event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_SOFT_RIGHT:
                // case 2> Open options menu when RSK is clicked
                openOptionsMenu();
                return true;
            case KeyEvent.KEYCODE_SOFT_LEFT:
                Log.e(LOG_TAG, "LSK pressed close options menu");
                //case 4a> close menu if Menu is open and left soft key is pressed
                if (mIsMenuOpened) {
                    onBackPressed();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if( (KeyEvent.KEYCODE_DPAD_UP == event.getKeyCode()) && mButton0.hasFocus())
        {
            return true;  // consume the event to prevent focus going to action bar
        }
        return super.dispatchKeyEvent(event);
    }
}