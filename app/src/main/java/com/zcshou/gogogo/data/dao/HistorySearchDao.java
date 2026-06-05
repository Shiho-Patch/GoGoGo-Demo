package com.zcshou.gogogo.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zcshou.gogogo.data.entity.HistorySearch;

import java.util.List;

/**
 * 历史搜索 DAO
 */
@Dao
public interface HistorySearchDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(HistorySearch search);
    
    @Query("DELETE FROM HistorySearch WHERE id = :id")
    int deleteById(int id);
    
    @Query("DELETE FROM HistorySearch")
    void deleteAll();
    
    @Query("SELECT * FROM HistorySearch ORDER BY timestamp DESC")
    LiveData<List<HistorySearch>> getAllSearches();
    
    @Query("SELECT * FROM HistorySearch ORDER BY timestamp DESC")
    List<HistorySearch> getAllSearchesSync();
    
    @Query("SELECT * FROM HistorySearch WHERE id = :id")
    HistorySearch getSearchById(int id);
    
    @Query("SELECT * FROM HistorySearch WHERE searchKey = :key")
    HistorySearch getSearchByKey(String key);
    
    @Query("SELECT * FROM HistorySearch WHERE searchKey LIKE '%' || :query || '%' ORDER BY timestamp DESC LIMIT :limit")
    List<HistorySearch> searchByKeyword(String query, int limit);
}
