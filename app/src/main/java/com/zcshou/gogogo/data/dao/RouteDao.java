package com.zcshou.gogogo.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zcshou.gogogo.data.entity.Route;

import java.util.List;

/**
 * 路线 DAO
 */
@Dao
public interface RouteDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Route route);
    
    @Update
    int update(Route route);
    
    @Delete
    int delete(Route route);
    
    @Query("DELETE FROM RouteHistory WHERE id = :id")
    int deleteById(int id);
    
    @Query("DELETE FROM RouteHistory")
    void deleteAll();
    
    @Query("SELECT * FROM RouteHistory ORDER BY timestamp DESC")
    LiveData<List<Route>> getAllRoutes();
    
    @Query("SELECT * FROM RouteHistory ORDER BY timestamp DESC")
    List<Route> getAllRoutesSync();
    
    @Query("SELECT * FROM RouteHistory WHERE id = :id")
    Route getRouteById(int id);
}
