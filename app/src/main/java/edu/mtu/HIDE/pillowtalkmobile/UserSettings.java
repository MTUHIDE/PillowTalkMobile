package edu.mtu.HIDE.pillowtalkmobile;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSettings {

    private final static String sharedPreferencePath = "pillow_talk_user_settings";
    private final static String ipAddressKey = "ip_address_key";
    private final static String useBluetoothKey = "use_bluetooth_key";
    private final static String pillow1NicknameKey = "pillow_1_nickname_key";
    private final static String pillow2NicknameKey = "pillow_2_nickname_key";
    private final static String pillow1PressureKey = "pillow_1_inflate_key";
    private final static String pillow2PressureKey = "pillow_2_inflate_key";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public UserSettings(Context context) {
        this.sharedPreferences = context.getSharedPreferences(sharedPreferencePath, Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
        this.editor.apply();
    }

    public Boolean getUseBluetooth() {
        return sharedPreferences.getBoolean(useBluetoothKey, false);
    }

    public void setUseBluetooth(Boolean useBluetooth) {
        editor.putBoolean(useBluetoothKey, useBluetooth);
        editor.commit();
    }

    public String getIPAddress() {
        return sharedPreferences.getString(ipAddressKey, "");
    }

    public void setIPAddress(String ipAddress) {
        editor.putString(ipAddressKey, ipAddress);
        editor.commit();
    }

    public String getPillow1Nickname() {
        return sharedPreferences.getString(pillow1NicknameKey, "Pillow 1");
    }

    public void setPillow1Nickname(String nickname) {
        editor.putString(pillow1NicknameKey, nickname);
        editor.commit();
    }

    public String getPillow2Nickname() {
        return sharedPreferences.getString(pillow2NicknameKey, "Pillow 2");
    }

    public void setPillow2Nickname(String nickname) {
        editor.putString(pillow2NicknameKey, nickname);
        editor.commit();
    }

    public int getPillow1PressureInterval() {
        return sharedPreferences.getInt(pillow1PressureKey, 1);
    }

    public void setPillow1PressureInterval(int seconds) {
        editor.putInt(pillow1PressureKey, seconds);
        editor.commit();
    }

    public int getPillow2PressureInterval() {
        return sharedPreferences.getInt(pillow2PressureKey, 1);
    }

    public void setPillow2PressureInterval(int seconds) {
        editor.putInt(pillow2PressureKey, seconds);
        editor.commit();
    }

}
