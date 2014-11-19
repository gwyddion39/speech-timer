package com.glickn.speechtimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Vibrator;

// This class extends CountDownTimer which is a class for executing
// an action at a specified interval for a specified period of time.
// We're using this to create our notification and update the remaining
// time shown once per second.
public class SpeechCountdownTimer extends CountDownTimer
{
    private static int _nextNotificationId = 101;
    private int _notificationId;

    private Notification.Builder _notificationBuilder;
    private  NotificationManager _notificationManager;
    private Context _context;
    private Vibrator _vibrator;

    public SpeechCountdownTimer(Context context, int seconds, int interval)
    {
        super(seconds * 1000, interval);
        _context = context;
        _notificationId = ++_nextNotificationId;
        createNotification(seconds);
        TimerManager.getInstance().addTimer(_notificationId, this);
        _vibrator = (Vibrator)_context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onTick(long msUntilFinished)
    {
        _notificationBuilder.setContentText(formatSecondsAsString((int)(msUntilFinished / 1000)));
        _notificationManager.notify(_notificationId, _notificationBuilder.build());

        // 3 seconds remaining: . . . . .
        if ( (int)(msUntilFinished / 1000) == 3 )
        {
            long[] pattern = {0,100,100,100,100,100,100,100,100,100};
            _vibrator.vibrate(pattern, -1);
            return;
        }

        // 1 minute remaining: - - -
        if ( (int)(msUntilFinished / 1000) == 60 )
        {
            long[] pattern = {0,300,100,300,100,300};
            _vibrator.vibrate(pattern, -1);
            return;
        }

        // 10 minute intervals: - . -
        if ( (int)(msUntilFinished / 1000) % (60*10) == 0)
        {
            long[] pattern = {0,300,100,100,100,300};
            _vibrator.vibrate(pattern, -1);
            return;
        }

        // 5 minute interval: - . .
        if ( (int)(msUntilFinished / 1000) % (60*5) == 0)
        {
            long[] pattern = {0,300,100,100,100,100};
            _vibrator.vibrate(pattern, -1);
        }
    }

    @Override
    public void onFinish()
    {
        // Remove the notification from the context stream
        _notificationManager.cancel(_notificationId);
    }

    // Called once to create the notification. We save the NotificationBuilder
    // as a member variable use that to update the notification later.
    private void createNotification(int seconds)
    {
        Intent notificationIntent =
            new Intent(_context, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
            _context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the stop action
        Intent stopIntent = new Intent(NotificationIntentService.ACTION_STOP_TIMER, null, _context,
            NotificationIntentService.class);
        stopIntent.putExtra(NotificationIntentService.TIMER_ID_KEY, _notificationId);
        PendingIntent pendingIntentStop = PendingIntent
            .getService(_context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action.Builder stopActionBuilder = new Notification.Action.Builder(R.drawable.ic_launcher, "Stop", pendingIntentStop);

        // Create the ongoing notification
        _notificationBuilder =
            new Notification.Builder(_context)
                .setContentTitle("Speech Timer")
                .setContentText(formatSecondsAsString(seconds))
                .setSmallIcon(R.drawable.empty)
                .setOngoing(true)
                .extend(new Notification.WearableExtender()
                    .setBackground(BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_launcher))
                    .setContentIcon(R.drawable.ic_launcher)
                    .addAction(stopActionBuilder.build()));

        // Build the notification and show it
        _notificationManager =
            (NotificationManager)_context.getSystemService(Context.NOTIFICATION_SERVICE);
        _notificationManager.notify(_notificationId, _notificationBuilder.build());
    }

    // Utility to format the remaining time
    private String formatSecondsAsString(int duration)
    {
        int minutes = (int)(duration / 60);
        int seconds = (int)(duration) % 60;

        StringBuilder timerString = new StringBuilder();
        timerString.append(minutes);
        timerString.append(":");
        if ( seconds > 10 )
        {
            timerString.append(seconds);
        }
        else
        {
            timerString.append(0);
            timerString.append(seconds);
        }

        return timerString.toString();
    }
}
