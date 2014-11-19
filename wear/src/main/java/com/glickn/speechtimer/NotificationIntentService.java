package com.glickn.speechtimer;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotificationIntentService extends IntentService
{
    private static final String TAG = "NotificationIntent";

    // Actions
    public static final String ACTION_STOP_TIMER = "com.glickn.speechtimer.ACTION_STOP_TIMER";

    // Keys
    public static final String TIMER_ID_KEY = "timer_id";

    public NotificationIntentService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String action = intent.getAction();
        if ( ACTION_STOP_TIMER.equals(action) )
        {
            Bundle bundle = intent.getExtras();
            if ( bundle != null && bundle.containsKey(TIMER_ID_KEY) )
            {
                TimerManager.getInstance().getTimer(bundle.getInt(TIMER_ID_KEY)).onFinish();
                TimerManager.getInstance().getTimer(bundle.getInt(TIMER_ID_KEY)).cancel();
            }
        }
    }
}
