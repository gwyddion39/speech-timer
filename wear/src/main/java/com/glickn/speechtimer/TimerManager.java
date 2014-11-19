package com.glickn.speechtimer;

import java.util.HashMap;
import java.util.Map;

public class TimerManager
{
    private Map<Integer, SpeechCountdownTimer> _timerMap;

    private static TimerManager _instance;

    private TimerManager()
    {
        _timerMap = new HashMap<Integer, SpeechCountdownTimer>();
    }

    public static TimerManager getInstance()
    {
        if ( null == _instance )
        {
            _instance = new TimerManager();
        }
        return _instance;
    }

    public void addTimer(Integer id, SpeechCountdownTimer timer)
    {
        _timerMap.put(id, timer);
    }

    public SpeechCountdownTimer getTimer(Integer id)
    {
        return _timerMap.get(id);
    }
}
