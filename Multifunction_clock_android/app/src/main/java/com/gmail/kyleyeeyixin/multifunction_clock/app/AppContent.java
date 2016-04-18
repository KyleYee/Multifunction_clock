package com.gmail.kyleyeeyixin.multifunction_clock.app;

/**
 * Created by kyleYee on 2016/4/16.
 */
public class AppContent {
    //发送数据给单片机
    public static final String BLUETOOTH_BROADCAST_SEND = "send";
    //接收由单片机发来的数据
    public static final String BLUETOOTH_BROADCAST_RECEIVE = "receive";
    //时钟设置蓝牙广播
    public static final String BLUETOOTH_BROADCAST_TIME = "service_broadcast_time";
    //链接蓝牙广播
    public static final String BLUETOOTH_BROADCAST_CONNECT = "connect";
    //设置闹钟广播
    public static final String BLUETOOTH_BROADCAST_ALARM_CLOCK = "alarm_clock";
    //秒表广播
    public static final String BLUETOOTH_BROADCAST_STOPWATCH = "stopwatch";
    //整点报时广播
    public static final String BLUETOOTH_BROADCAST_CHIME = "chime";
    //纪恋日广播
    public static final String BLUETOOTH_BROADCAST_MEMORIAL_DAY = "memorial_day";
    //温度显示广播
    public static final String BLUETOOTH_BROADCAST_TMEPERATURE = "temperature";
    //电量显示广播
    public static final String BLUETOOTH_BROADCAST_POWER = "power";

    /**
     * The profile is in disconnected state
     */
    public static final int STATE_DISCONNECTED = 0;
    /**
     * The profile is in connecting state
     */
    public static final int STATE_CONNECTING = 1;
    /**
     * The profile is in connected state
     */
    public static final int STATE_CONNECTED = 2;
    /**
     * The profile is in disconnecting state
     */
    public static final int STATE_DISCONNECTING = 3;
    //连接状态广播
    public static final String BLUETOOTH_CONNECT_STATE = "bluetooth_state";
    //连接是否成功
    public static final String EXTRA_SUCCEED = "extra_succeed";
    //链接状态
    public static final String EXTRA_CONNECT_STATE = "extra_connect_state";
}
