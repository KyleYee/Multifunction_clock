package com.gmail.kyleyeeyixin.multifunction_clock.model.alarm_clock;

import java.io.Serializable;

/**
 * 闹钟设置实例
 * Created by yunnnn on 2016/4/13.
 */
public class AlarmClock implements Serializable {
    private static final long serialVersionUID = -5502555111184921046L;
    private int hour;
    private int minute;
    private boolean type;//闹钟状态

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
