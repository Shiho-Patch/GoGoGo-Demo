package com.zcshou.gogogo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.zcshou.gogogo.data.dao.HistorySearchDao;
import com.zcshou.gogogo.data.database.AppDatabase;
import com.zcshou.gogogo.data.entity.HistorySearch;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 搜索数据仓库
 */
public class SearchRepository extends BaseRepository {
    
    private HistorySearchDao historySearchDao;
    private LiveData<List<HistorySearch>> allSearches;
    private ExecutorService executorService;
    
    public SearchRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        historySearchDao = db.historySearchDao();
        allSearches = historySearchDao.getAllSearches();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * 获取所有搜索历史（LiveData）
     */
    public LiveData<List<HistorySearch>> getAllSearches() {
        return allSearches;
    }
    
    /**
     * 获取所有搜索历史（同步）
     */
    public List<HistorySearch> getAllSearchesSync() {
        return historySearchDao.getAllSearchesSync();
    }
    
    /**
     * 保存搜索记录
     */
    public void saveSearch(HistorySearch search) {
        executorService.execute(() -> {
            try {
                historySearchDao.insert(search);
                logDebug("Saved search: " + search.searchKey);
            } catch (Exception e) {
                logError("Failed to save search", e);
            }
        });
    }
    
    /**
     * 搜索关键字
     */
    public List<HistorySearch> searchByKeyword(String query, int limit) {
        try {
            return historySearchDao.searchByKeyword(query, limit);
        } catch (Exception e) {
            logError("Failed to search by keyword", e);
            return null;
        }
    }
    
    /**
     * 删除搜索记录
     */
    public void deleteSearch(int id) {
        executorService.execute(() -> {
            try {
                historySearchDao.deleteById(id);
                logDebug("Deleted search with id: " + id);
            } catch (Exception e) {
                logError("Failed to delete search", e);
            }
        });
    }
    
    /**
     * 清空所有搜索记录
     */
    public void clearAllSearches() {
        executorService.execute(() -> {
            try {
                historySearchDao.deleteAll();
                logDebug("Cleared all search history");
            } catch (Exception e) {
                logError("Failed to clear searches", e);
            }
        });
    }
}
