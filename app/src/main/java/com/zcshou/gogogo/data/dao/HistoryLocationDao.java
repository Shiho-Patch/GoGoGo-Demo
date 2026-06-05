package com.zcshou.gogogo.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zcshou.gogogo.data.entity.HistoryLocation;

import java.util.List;

/**
 * 历史位置 DAO
 */
@Dao
public interface HistoryLocationDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(HistoryLocation location);
    
    @Update
    int update(HistoryLocation location);
    
    @Query("DELETE FROM HistoryLocation WHERE id = :id")
    int deleteById(int id);
    
    @Query("DELETE FROM HistoryLocation")
    void deleteAll();
    
    @Query("SELECT * FROM HistoryLocation ORDER BY timestamp DESC")
    LiveData<List<HistoryLocation>> getAllLocations();
    
    @Query("SELECT * FROM HistoryLocation ORDER BY timestamp DESC")
    List<HistoryLocation> getAllLocationsSync();
    
    @Query("SELECT * FROM HistoryLocation WHERE id = :id")
    HistoryLocation getLocationById(int id);
    
    @Query("SELECT * FROM HistoryLocation WHERE longitudeWgs84 = :lng AND latitudeWgs84 = :lat")
    HistoryLocation getLocationByCoordinates(String lng, String lat);
}
