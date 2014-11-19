package com.glickn.speechtimer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends Activity implements
    DelayedConfirmationView.DelayedConfirmationListener
{

    private NumberPicker            _numberPicker;
    private Button                  _startButton;
    private Button                  _exitButton;

    private DelayedConfirmationView _delayedView;
    private View                    _confirmationScreen;
    private TextView                _confirmationText;

    private String[] TIMER_DISPLAY_VALUES = {"5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        // This listener is a little different than how you would setup your views in Android.
        // We wait until the layout is inflated when using a WatchViewStub so that it's certain
        // which layout we're using; Round vs Rectangular
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener()
        {
            @Override
            public void onLayoutInflated(WatchViewStub stub)
            {
                _numberPicker = (NumberPicker) stub.findViewById(R.id.number_picker);
                _startButton = (Button) stub.findViewById(R.id.start_button);
                _exitButton = (Button) stub.findViewById(R.id.exit_button);

                // Setup the number picker
                _numberPicker.setMinValue(0);
                _numberPicker.setMaxValue(11);
                _numberPicker.setDisplayedValues(TIMER_DISPLAY_VALUES);

                // Setup the buttons
                _startButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        // Start a delayed confirmation timer to give the user a chance to cancel
                        // DelayedConfirmationView is part of the wearable-ui library specified in
                        // the wear project's gradle file
                        _confirmationText.setText(String.format("Setting timer for %s minutes",
                            TIMER_DISPLAY_VALUES[_numberPicker.getValue()]));
                        _confirmationScreen.setVisibility(View.VISIBLE);
                        _delayedView.start();
                    }
                });

                _exitButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        finish();
                    }
                });

                // Setup delayed confirmation button
                _confirmationScreen = findViewById(R.id.confirmation_screen);
                _confirmationText = (TextView) findViewById(R.id.confirmation_text);
                _delayedView = (DelayedConfirmationView) findViewById(R.id.delayed_confirm);
                _delayedView.setListener(MainActivity.this);
                _delayedView.setTotalTimeMs(4000);
            }
        });
    }

    private void startTimer(int seconds)
    {
        SpeechCountdownTimer speechCountdownTimer = new SpeechCountdownTimer(this, seconds, 1000);
        speechCountdownTimer.start();
    }

    private int getNumberPickerValue()
    {
        String displayedValue = TIMER_DISPLAY_VALUES[_numberPicker.getValue()];
        return Integer.parseInt(displayedValue) * 60;
    }

    /**
     * DelayedConfirmationListener section
     */

    @Override
    public void onTimerFinished(View view)
    {
        // User didn't cancel, perform the action
        startTimer(getNumberPickerValue());
        finish();
    }

    @Override
    public void onTimerSelected(View view)
    {
        // User canceled, abort the action
        _confirmationScreen.setVisibility(View.GONE);
    }
}
