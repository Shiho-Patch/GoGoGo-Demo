package com.zcshou.gogogo.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zcshou.gogogo.data.entity.FavoriteLocation;

import java.util.List;

/**
 * 收藏位置 DAO（新功能）
 */
@Dao
public interface FavoriteLocationDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(FavoriteLocation favorite);
    
    @Update
    int update(FavoriteLocation favorite);
    
    @Delete
    int delete(FavoriteLocation favorite);
    
    @Query("DELETE FROM FavoriteLocation WHERE id = :id")
    int deleteById(int id);
    
    @Query("DELETE FROM FavoriteLocation")
    void deleteAll();
    
    @Query("SELECT * FROM FavoriteLocation ORDER BY sortOrder ASC, timestamp DESC")
    LiveData<List<FavoriteLocation>> getAllFavorites();
    
    @Query("SELECT * FROM FavoriteLocation ORDER BY sortOrder ASC, timestamp DESC")
    List<FavoriteLocation> getAllFavoritesSync();
    
    @Query("SELECT * FROM FavoriteLocation WHERE id = :id")
    FavoriteLocation getFavoriteById(int id);
}
