package edu.mtu.HIDE.pillowtalkmobile;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSettings {

    private final static String sharedPreferencePath = "pillow_talk_user_settings";
    private final static String ipAddressKey = "ip_address_key";
    private final static String useBluetoothKey = "use_bluetooth_key";
    private final static String pillow1NicknameKey = "pillow_1_nickname_key";
    private final static String pillow2NicknameKey = "pillow_2_nickname_key";

    private final static String pillow1LowPressureKey = "pillow_1_low_pressure_key";
    private final static String pillow2LowPressureKey = "pillow_2_low_pressure_key";
    private final static String pillow1MediumPressureKey = "pillow_1_medium_pressure_key";
    private final static String pillow2MediumPressureKey = "pillow_2_medium_pressure_key";
    private final static String pillow1HighPressureKey = "pillow_1_high_pressure_key";
    private final static String pillow2HighPressureKey = "pillow_2_high_pressure_key";

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

    public int getPillow1LowPressureInterval() {
        return sharedPreferences.getInt(pillow1LowPressureKey, 1);
    }

    public void setPillow1LowPressureInterval(int seconds) {
        editor.putInt(pillow1LowPressureKey, seconds);
        editor.commit();
    }

    public int getPillow1MediumPressureInterval() {
        return sharedPreferences.getInt(pillow1MediumPressureKey, 2);
    }

    public void setPillow1MediumPressureInterval(int seconds) {
        editor.putInt(pillow1MediumPressureKey, seconds);
        editor.commit();
    }

    public int getPillow1HighPressureInterval() {
        return sharedPreferences.getInt(pillow1HighPressureKey, 3);
    }

    public void setPillow1HighPressureInterval(int seconds) {
        editor.putInt(pillow1HighPressureKey, seconds);
        editor.commit();
    }

    public int getPillow2LowPressureInterval() {
        return sharedPreferences.getInt(pillow2LowPressureKey, 1);
    }

    public void setPillow2LowPressureInterval(int seconds) {
        editor.putInt(pillow2LowPressureKey, seconds);
        editor.commit();
    }

    public int getPillow2MediumPressureInterval() {
        return sharedPreferences.getInt(pillow2MediumPressureKey, 2);
    }

    public void setPillow2MediumPressureInterval(int seconds) {
        editor.putInt(pillow2MediumPressureKey, seconds);
        editor.commit();
    }

    public int getPillow2HighPressureInterval() {
        return sharedPreferences.getInt(pillow2HighPressureKey, 3);
    }

    public void setPillow2HighPressureInterval(int seconds) {
        editor.putInt(pillow2HighPressureKey, seconds);
        editor.commit();
    }

}
