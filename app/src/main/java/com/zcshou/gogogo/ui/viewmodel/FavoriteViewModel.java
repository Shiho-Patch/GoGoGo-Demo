package com.zcshou.gogogo.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zcshou.gogogo.data.entity.FavoriteLocation;
import com.zcshou.gogogo.data.repository.FavoriteRepository;
import com.zcshou.gogogo.data.repository.RepositoryManager;

import java.util.List;

/**
 * 收藏夹 ViewModel
 */
public class FavoriteViewModel extends AndroidViewModel {
    private final FavoriteRepository favoriteRepository;
    private final LiveData<List<FavoriteLocation>> allFavorites;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        favoriteRepository = RepositoryManager.getInstance(application).getFavoriteRepository();
        allFavorites = favoriteRepository.getAllFavorites();
    }

    /**
     * 获取所有收藏（LiveData）
     */
    public LiveData<List<FavoriteLocation>> getAllFavorites() {
        return allFavorites;
    }

    /**
     * 添加收藏
     */
    public void addFavorite(String name, String address, 
                           double longitudeWgs84, double latitudeWgs84,
                           double longitudeCustom, double latitudeCustom) {
        FavoriteLocation favorite = new FavoriteLocation();
        favorite.name = name;
        favorite.address = address;
        favorite.longitudeWgs84 = String.valueOf(longitudeWgs84);
        favorite.latitudeWgs84 = String.valueOf(latitudeWgs84);
        favorite.longitudeCustom = String.valueOf(longitudeCustom);
        favorite.latitudeCustom = String.valueOf(latitudeCustom);
        favorite.timestamp = System.currentTimeMillis();
        favorite.sortOrder = 0;
        
        favoriteRepository.saveFavorite(favorite);
    }

    /**
     * 更新收藏
     */
    public void updateFavorite(FavoriteLocation favorite) {
        favoriteRepository.saveFavorite(favorite);
    }

    /**
     * 删除收藏
     */
    public void deleteFavorite(FavoriteLocation favorite) {
        favoriteRepository.deleteFavorite(favorite);
    }

    /**
     * 删除收藏（通过ID）
     */
    public void deleteFavorite(int id) {
        favoriteRepository.deleteFavorite(id);
    }

    /**
     * 清空所有收藏
     */
    public void clearAllFavorites() {
        favoriteRepository.clearAllFavorites();
    }
}
