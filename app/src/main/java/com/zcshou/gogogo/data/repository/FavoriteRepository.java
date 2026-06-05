package com.zcshou.gogogo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.zcshou.gogogo.data.dao.FavoriteLocationDao;
import com.zcshou.gogogo.data.database.AppDatabase;
import com.zcshou.gogogo.data.entity.FavoriteLocation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 收藏数据仓库
 */
public class FavoriteRepository extends BaseRepository {
    
    private FavoriteLocationDao favoriteLocationDao;
    private LiveData<List<FavoriteLocation>> allFavorites;
    private ExecutorService executorService;
    
    public FavoriteRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        favoriteLocationDao = db.favoriteLocationDao();
        allFavorites = favoriteLocationDao.getAllFavorites();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * 获取所有收藏（LiveData）
     */
    public LiveData<List<FavoriteLocation>> getAllFavorites() {
        return allFavorites;
    }
    
    /**
     * 获取所有收藏（同步）
     */
    public List<FavoriteLocation> getAllFavoritesSync() {
        return favoriteLocationDao.getAllFavoritesSync();
    }
    
    /**
     * 保存收藏
     */
    public void saveFavorite(FavoriteLocation favorite) {
        executorService.execute(() -> {
            try {
                if (favorite.id > 0) {
                    // 更新现有收藏
                    favoriteLocationDao.update(favorite);
                    logDebug("Updated favorite: " + favorite.name);
                } else {
                    // 插入新收藏
                    favoriteLocationDao.insert(favorite);
                    logDebug("Inserted favorite: " + favorite.name);
                }
            } catch (Exception e) {
                logError("Failed to save favorite", e);
            }
        });
    }
    
    /**
     * 根据ID获取收藏
     */
    public FavoriteLocation getFavoriteById(int id) {
        try {
            return favoriteLocationDao.getFavoriteById(id);
        } catch (Exception e) {
            logError("Failed to get favorite by id", e);
            return null;
        }
    }
    
    /**
     * 删除收藏
     */
    public void deleteFavorite(int id) {
        executorService.execute(() -> {
            try {
                favoriteLocationDao.deleteById(id);
                logDebug("Deleted favorite with id: " + id);
            } catch (Exception e) {
                logError("Failed to delete favorite", e);
            }
        });
    }
    
    /**
     * 删除收藏（对象）
     */
    public void deleteFavorite(FavoriteLocation favorite) {
        executorService.execute(() -> {
            try {
                favoriteLocationDao.delete(favorite);
                logDebug("Deleted favorite: " + favorite.name);
            } catch (Exception e) {
                logError("Failed to delete favorite", e);
            }
        });
    }
    
    /**
     * 清空所有收藏
     */
    public void clearAllFavorites() {
        executorService.execute(() -> {
            try {
                favoriteLocationDao.deleteAll();
                logDebug("Cleared all favorites");
            } catch (Exception e) {
                logError("Failed to clear favorites", e);
            }
        });
    }
}
