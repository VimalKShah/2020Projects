package com.borqs.samplesoftkeydemo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;


public class SoftKeyMenuUtils {
    private static final String LOG_TAG = "DemoSoftKeyMenuUtils";

    public static final int LABEL_RIGHT_OPTIONS = 6;
    public static final int LABEL_RIGHT_BLANK = 5;

    public static final int LABEL_CENTER_BLANK = 15;
    public static final int LABEL_CENTER_SELECT = 18;

    public static final int LABEL_LEFT_BACK=0;

    public static void setSoftKeyMenuLabels(Context context, int leftLabel, int centerLabel,
                                            int rightLabel, boolean rightFocus) {
        Intent intent = new Intent("android.intent.action.CHANGE_NAVBAR");
        intent.putExtra("left", leftLabel);
        intent.putExtra("center", centerLabel);
        intent.putExtra("right", rightLabel);
        intent.putExtra("right_focus", rightFocus);
        context.sendBroadcast(intent);
    }

    // remove TextView from function when used in production code
    public static void setLSKMenuLabel(Context context, int labelId) {
        Intent intent = new Intent("android.intent.action.CHANGE_NAVBAR");
        intent.putExtra("left", labelId);
        context.sendBroadcast(intent);
    }

    public static void setCSKMenuLabel(Context context, int centerLabel) {
        Log.e(LOG_TAG, "RSK intent sent  -->"+centerLabel);
        Intent intent = new Intent("android.intent.action.CHANGE_NAVBAR");
        intent.putExtra("center", centerLabel);
        context.sendBroadcast(intent);
    }

    // remove TextView from function when used in production code
    public static void setRSKMenuLabel(Context context, int labelId) {
        Log.e(LOG_TAG, "RSK intent sent  -->");
        Intent intent = new Intent("android.intent.action.CHANGE_NAVBAR");
        intent.putExtra("right", labelId);
        context.sendBroadcast(intent);
    }
}
