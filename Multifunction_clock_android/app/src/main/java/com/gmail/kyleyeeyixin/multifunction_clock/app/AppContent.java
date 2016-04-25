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
    //时间实体数据
    public static final String EXTRA_TIME = "extra_time";

    /**
     * 发送命令的命令值
     * 1.进入跑表   20
     * 2.开始跑表   21
     * 3.跑表暂停   22
     * 4.跑表复位   23
     * 5.时钟设置   10
     * 6.闹钟设置   30
     * 7.整点报时   60
     * 8.纪念日
     * 9.温度显示
     * 10.电量显示
     */
    public static final String SEND_ENTER_STOPWATCH = "20";
    public static final String SEND_START_STOPWATCH = "21";
    public static final String SEND_PAUSE_STOPWATCH = "22";
    public static final String SEND_RESET_STOPWATCH = "23";
    public static final String SEND_ENTER_TIME = "10";
    public static final String SEND_ENTER_ALARM_CLOCK = "30";
    public static final String SEND_ENTER_CHIME = "60";

}
