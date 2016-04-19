package com.gmail.kyleyeeyixin.multifunction_clock.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.provider.Settings;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yunnnn on 2016/4/12.
 */
public class Utils {

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static boolean isValidString(String string) {
        return string != null && !string.trim().equals("");
    }


    /**
     * This message is to hide the soft input keyboard.
     *
     * @param context
     */
    public static void HideSoftInput(final Activity context) {
        InputMethodManager manager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean show = manager.isActive();
        if (null != context && context.getCurrentFocus() != null) {
            IBinder iBinder = context.getCurrentFocus().getWindowToken();
            manager.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showSoftInput(final Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    /**
     * check the email address.
     *
     * @return true: The email address is legal. false: The other.
     */
    public static boolean isEmail(String mail) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mail);
        return m.find();
    }

    /**
     * If the system location service is unavailable, we should open the GPS setting page.
     */
    public static void openGpsSettingPage(final Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // The Android SDK doc says that the location settings activity
            // may not be found. In that case show the general settings.
            // General settings activity
            intent.setAction(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }


    /**
     * Check whether the network is available.
     *
     * @param context     The Application's context.
     * @param showMessage If show the toast.
     * @return true: The network connection is available. false: otherwise.
     */
    public static boolean isConnected(Context context, boolean showMessage) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected() == true) {
            return true;
        }

        return false;
    }


    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static int pixToDip(Context context, float pixValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixValue, metrics);
    }

    public static int spToPixels(Context context, float spValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, metrics);
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static byte[] inputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    public static void copyText(Context context, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(text);
    }

    public static BluetoothAdapter judgeConnectBluetooth(Activity activity) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            if (!adapter.enable()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivity(intent);
            }
            return adapter;
        }
        return adapter;
    }


    ;
}
