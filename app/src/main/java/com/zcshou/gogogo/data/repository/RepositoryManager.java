package com.zcshou.gogogo.data.repository;

import android.content.Context;

/**
 * 仓库管理器
 * 统一管理所有 Repository 实例
 */
public class RepositoryManager {
    
    private static RepositoryManager instance;
    
    private LocationRepository locationRepository;
    private SearchRepository searchRepository;
    private RouteRepository routeRepository;
    private FavoriteRepository favoriteRepository;
    private SettingsRepository settingsRepository;
    
    private RepositoryManager(Context context) {
        Context appContext = context.getApplicationContext();
        locationRepository = new LocationRepository(appContext);
        searchRepository = new SearchRepository(appContext);
        routeRepository = new RouteRepository(appContext);
        favoriteRepository = new FavoriteRepository(appContext);
        settingsRepository = new SettingsRepository(appContext);
    }
    
    public static synchronized RepositoryManager getInstance(Context context) {
        if (instance == null) {
            instance = new RepositoryManager(context);
        }
        return instance;
    }
    
    public LocationRepository getLocationRepository() {
        return locationRepository;
    }
    
    public SearchRepository getSearchRepository() {
        return searchRepository;
    }
    
    public RouteRepository getRouteRepository() {
        return routeRepository;
    }
    
    public FavoriteRepository getFavoriteRepository() {
        return favoriteRepository;
    }
    
    public SettingsRepository getSettingsRepository() {
        return settingsRepository;
    }
}
