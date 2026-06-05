package com.zcshou.gogogo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.zcshou.gogogo.data.dao.HistoryLocationDao;
import com.zcshou.gogogo.data.database.AppDatabase;
import com.zcshou.gogogo.data.entity.HistoryLocation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 位置数据仓库
 */
public class LocationRepository extends BaseRepository {
    
    private HistoryLocationDao historyLocationDao;
    private LiveData<List<HistoryLocation>> allLocations;
    private ExecutorService executorService;
    
    public LocationRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        historyLocationDao = db.historyLocationDao();
        allLocations = historyLocationDao.getAllLocations();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * 获取所有历史位置（LiveData）
     */
    public LiveData<List<HistoryLocation>> getAllLocations() {
        return allLocations;
    }
    
    /**
     * 获取所有历史位置（同步）
     */
    public List<HistoryLocation> getAllLocationsSync() {
        return historyLocationDao.getAllLocationsSync();
    }
    
    /**
     * 插入或更新位置记录
     */
    public void insertOrUpdateLocation(HistoryLocation location) {
        executorService.execute(() -> {
            try {
                // 检查是否已存在相同坐标的记录
                HistoryLocation existing = historyLocationDao.getLocationByCoordinates(
                    location.longitudeWgs84, location.latitudeWgs84);
                
                if (existing != null) {
                    // 更新现有记录
                    location.id = existing.id;
                    historyLocationDao.update(location);
                    logDebug("Updated location: " + location.location);
                } else {
                    // 插入新记录
                    historyLocationDao.insert(location);
                    logDebug("Inserted location: " + location.location);
                }
            } catch (Exception e) {
                logError("Failed to insert/update location", e);
            }
        });
    }
    
    /**
     * 更新位置名称
     */
    public void updateLocationName(int id, String newName) {
        executorService.execute(() -> {
            try {
                HistoryLocation location = historyLocationDao.getLocationById(id);
                if (location != null) {
                    location.location = newName;
                    historyLocationDao.update(location);
                    logDebug("Updated location name: " + newName);
                }
            } catch (Exception e) {
                logError("Failed to update location name", e);
            }
        });
    }
    
    /**
     * 删除位置记录
     */
    public void deleteLocation(int id) {
        executorService.execute(() -> {
            try {
                historyLocationDao.deleteById(id);
                logDebug("Deleted location with id: " + id);
            } catch (Exception e) {
                logError("Failed to delete location", e);
            }
        });
    }
    
    /**
     * 清空所有位置记录
     */
    public void clearAllLocations() {
        executorService.execute(() -> {
            try {
                historyLocationDao.deleteAll();
                logDebug("Cleared all location history");
            } catch (Exception e) {
                logError("Failed to clear locations", e);
            }
        });
    }
}
