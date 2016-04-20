package com.gmail.kyleyeeyixin.multifunction_clock.model.memory_day;

import java.io.Serializable;

/**
 * 纪念日
 * Created by yunnnn on 2016/4/20.
 */
public class MemoryDay implements Serializable {

    private String content;
    private int year;
    private int mouth;
    private int day;
    private int hour;
    private int minute;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMouth() {
        return mouth;
    }

    public void setMouth(int mouth) {
        this.mouth = mouth;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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
