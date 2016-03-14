package com.gmail.kyleyeeyixin.multifunction_clock.module.wifi;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * WIFI控制类
 * Created by yunnnn on 2016/3/14.
 */
public class MyWifiManager {

    private static MyWifiManager mInstance;
    private Context mContext;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    public MyWifiManager(Context context) {
        this.mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null){
            mWifiInfo = mWifiManager.getConnectionInfo();
        }
    }

    public static MyWifiManager getInstance(Context context){
        if (mInstance == null){
            mInstance = new MyWifiManager(context);
        }

        return mInstance;
    }

    /**
     * 打开wifi
     */
    public void openWifi(){
        if (!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭WiFi
     */
    public void closeWifi(){
        if (mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 获取WiFi当前状态
     * @return
     */
    public int getWifiState(){
        if (mWifiManager != null){
            return mWifiManager.getWifiState();
        }
        return 0;
    }
}
