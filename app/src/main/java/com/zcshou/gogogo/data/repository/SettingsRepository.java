package com.zcshou.gogogo.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * 设置数据仓库
 */
public class SettingsRepository extends BaseRepository {
    
    private static final String PREFS_NAME = "gogogo_settings";
    private static final String KEY_MAP_TYPE = "map_type";
    private static final String KEY_LOCATION_MODE = "location_mode";
    private static final String KEY_MOCK_INTERVAL = "mock_interval";
    private static final String KEY_SHOW_TRAFFIC = "show_traffic";
    private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_DB_MIGRATED = "db_migrated";
    
    private SharedPreferences sharedPreferences;
    private MutableLiveData<Boolean> trafficEnabled;
    private MutableLiveData<Boolean> screenOnEnabled;
    private MutableLiveData<Integer> mockInterval;
    
    public SettingsRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        trafficEnabled = new MutableLiveData<>(isShowTraffic());
        screenOnEnabled = new MutableLiveData<>(isKeepScreenOn());
        mockInterval = new MutableLiveData<>(getMockInterval());
    }
    
    /**
     * 获取地图类型
     */
    public int getMapType() {
        return sharedPreferences.getInt(KEY_MAP_TYPE, 0);
    }
    
    /**
     * 设置地图类型
     */
    public void setMapType(int mapType) {
        sharedPreferences.edit().putInt(KEY_MAP_TYPE, mapType).apply();
        logDebug("Set map type: " + mapType);
    }
    
    /**
     * 获取定位模式
     */
    public int getLocationMode() {
        return sharedPreferences.getInt(KEY_LOCATION_MODE, 0);
    }
    
    /**
     * 设置定位模式
     */
    public void setLocationMode(int mode) {
        sharedPreferences.edit().putInt(KEY_LOCATION_MODE, mode).apply();
        logDebug("Set location mode: " + mode);
    }
    
    /**
     * 获取模拟间隔（毫秒）
     */
    public int getMockInterval() {
        return sharedPreferences.getInt(KEY_MOCK_INTERVAL, 1000);
    }
    
    /**
     * 设置模拟间隔
     */
    public void setMockInterval(int interval) {
        sharedPreferences.edit().putInt(KEY_MOCK_INTERVAL, interval).apply();
        mockInterval.setValue(interval);
        logDebug("Set mock interval: " + interval + "ms");
    }
    
    /**
     * 是否显示交通状况
     */
    public boolean isShowTraffic() {
        return sharedPreferences.getBoolean(KEY_SHOW_TRAFFIC, false);
    }
    
    /**
     * 设置是否显示交通状况
     */
    public void setShowTraffic(boolean show) {
        sharedPreferences.edit().putBoolean(KEY_SHOW_TRAFFIC, show).apply();
        trafficEnabled.setValue(show);
        logDebug("Set show traffic: " + show);
    }
    
    /**
     * 是否保持屏幕常亮
     */
    public boolean isKeepScreenOn() {
        return sharedPreferences.getBoolean(KEY_KEEP_SCREEN_ON, false);
    }
    
    /**
     * 设置是否保持屏幕常亮
     */
    public void setKeepScreenOn(boolean keepOn) {
        sharedPreferences.edit().putBoolean(KEY_KEEP_SCREEN_ON, keepOn).apply();
        screenOnEnabled.setValue(keepOn);
        logDebug("Set keep screen on: " + keepOn);
    }
    
    /**
     * 是否首次启动
     */
    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true);
    }
    
    /**
     * 设置首次启动标志
     */
    public void setFirstLaunch(boolean isFirst) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, isFirst).apply();
        logDebug("Set first launch: " + isFirst);
    }
    
    /**
     * 数据库是否已迁移
     */
    public boolean isDbMigrated() {
        return sharedPreferences.getBoolean(KEY_DB_MIGRATED, false);
    }
    
    /**
     * 设置数据库迁移标志
     */
    public void setDbMigrated(boolean migrated) {
        sharedPreferences.edit().putBoolean(KEY_DB_MIGRATED, migrated).apply();
        logDebug("Set DB migrated: " + migrated);
    }
    
    /**
     * LiveData：交通状况显示状态
     */
    public LiveData<Boolean> getTrafficEnabled() {
        return trafficEnabled;
    }
    
    /**
     * LiveData：屏幕常亮状态
     */
    public LiveData<Boolean> getScreenOnEnabled() {
        return screenOnEnabled;
    }
    
    /**
     * LiveData：模拟间隔
     */
    public LiveData<Integer> getMockIntervalLiveData() {
        return mockInterval;
    }
}
