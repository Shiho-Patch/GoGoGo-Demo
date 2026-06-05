package com.zcshou.gogogo.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 管理类
 * 统一管理应用的配置项
 */
public class AppPreferences {
    private static final String PREF_NAME = "gogogo_prefs";
    private static final String KEY_ALTITUDE = "setting_altitude";
    private static final String KEY_MAP_KEY = "setting_map_key";
    private static final String KEY_IS_FIRST_LAUNCH = "is_first_launch";
    
    private static AppPreferences instance;
    private final SharedPreferences sharedPreferences;

    private AppPreferences(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AppPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreferences(context);
        }
        return instance;
    }

    /**
     * 获取海拔设置
     */
    public double getAltitude() {
        return sharedPreferences.getFloat(KEY_ALTITUDE, 55.0f);
    }

    /**
     * 保存海拔设置
     */
    public void setAltitude(double altitude) {
        sharedPreferences.edit().putFloat(KEY_ALTITUDE, (float) altitude).apply();
    }

    /**
     * 获取地图 Key
     */
    public String getMapKey() {
        return sharedPreferences.getString(KEY_MAP_KEY, "");
    }

    /**
     * 保存地图 Key
     */
    public void setMapKey(String mapKey) {
        sharedPreferences.edit().putString(KEY_MAP_KEY, mapKey).apply();
    }

    /**
     * 是否首次启动
     */
    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_LAUNCH, true);
    }

    /**
     * 设置是否首次启动
     */
    public void setFirstLaunch(boolean isFirstLaunch) {
        sharedPreferences.edit().putBoolean(KEY_IS_FIRST_LAUNCH, isFirstLaunch).apply();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
