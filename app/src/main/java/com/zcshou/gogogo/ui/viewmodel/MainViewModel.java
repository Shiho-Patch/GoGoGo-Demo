package com.zcshou.gogogo.ui.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zcshou.gogogo.data.entity.FavoriteLocation;
import com.zcshou.gogogo.data.entity.HistoryLocation;
import com.zcshou.gogogo.data.entity.Route;
import com.zcshou.gogogo.data.repository.FavoriteRepository;
import com.zcshou.gogogo.data.repository.LocationRepository;
import com.zcshou.gogogo.data.repository.RouteRepository;
import com.zcshou.gogogo.data.repository.SettingsRepository;
import com.zcshou.gogogo.map.MapEngine;
import com.zcshou.gogogo.ui.base.BaseViewModel;

import java.util.List;

/**
 * Main ViewModel - 主界面的业务逻辑
 */
public class MainViewModel extends BaseViewModel {
    
    private final LocationRepository locationRepository;
    private final RouteRepository routeRepository;
    private final FavoriteRepository favoriteRepository;
    private final SettingsRepository settingsRepository;
    
    // UI State
    private final MutableLiveData<Boolean> isMocking = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRouteRunning = new MutableLiveData<>(false);
    private final MutableLiveData<String> currentLocationName = new MutableLiveData<>("");
    private final MutableLiveData<Double> currentLatitude = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> currentLongitude = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> currentAddress = new MutableLiveData<>("");
    
    // Map State
    private final MutableLiveData<Integer> mapType = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> showTraffic = new MutableLiveData<>(false);
    private final MutableLiveData<Float> mapZoom = new MutableLiveData<>(16.0f);
    private final MutableLiveData<MapEngine.MapProvider> mapProvider = new MutableLiveData<>(MapEngine.MapProvider.BAIDU);
    private final MutableLiveData<MapEngine.MapType> mapDisplayType = new MutableLiveData<>(MapEngine.MapType.NORMAL);
    
    public MainViewModel(@NonNull Application application) {
        super();
        locationRepository = new LocationRepository(application);
        routeRepository = new RouteRepository(application);
        favoriteRepository = new FavoriteRepository(application);
        settingsRepository = new SettingsRepository(application);
        
        // Initialize from settings
        mapType.setValue(settingsRepository.getMapType());
        showTraffic.setValue(settingsRepository.isShowTraffic());
    }
    
    // =============== Mock Location State ===============
    
    public LiveData<Boolean> getIsMocking() {
        return isMocking;
    }
    
    public void setMocking(boolean mocking) {
        isMocking.setValue(mocking);
    }
    
    public LiveData<Boolean> getIsRouteRunning() {
        return isRouteRunning;
    }
    
    public void setRouteRunning(boolean running) {
        isRouteRunning.setValue(running);
    }
    
    // =============== Location State ===============
    
    public LiveData<String> getCurrentLocationName() {
        return currentLocationName;
    }
    
    public void setCurrentLocationName(String name) {
        currentLocationName.setValue(name);
    }
    
    public LiveData<Double> getCurrentLatitude() {
        return currentLatitude;
    }
    
    public void setCurrentLatitude(double latitude) {
        currentLatitude.setValue(latitude);
    }
    
    public LiveData<Double> getCurrentLongitude() {
        return currentLongitude;
    }
    
    public void setCurrentLongitude(double longitude) {
        currentLongitude.setValue(longitude);
    }
    
    public LiveData<String> getCurrentAddress() {
        return currentAddress;
    }
    
    public void setCurrentAddress(String address) {
        currentAddress.setValue(address);
    }
    
    // =============== Map State ===============
    
    public LiveData<Integer> getMapType() {
        return mapType;
    }
    
    public void setMapType(int type) {
        mapType.setValue(type);
        settingsRepository.setMapType(type);
    }
    
    public LiveData<Boolean> getShowTraffic() {
        return showTraffic;
    }
    
    public void setShowTraffic(boolean show) {
        showTraffic.setValue(show);
        settingsRepository.setShowTraffic(show);
    }
    
    public LiveData<Float> getMapZoom() {
        return mapZoom;
    }
    
    public void setMapZoom(float zoom) {
        mapZoom.setValue(zoom);
    }
    
    // =============== Repository Access ===============
    
    public LiveData<List<HistoryLocation>> getAllHistoryLocations() {
        return locationRepository.getAllLocations();
    }
    
    public LiveData<List<Route>> getAllRoutes() {
        return routeRepository.getAllRoutes();
    }
    
    public LiveData<List<FavoriteLocation>> getAllFavorites() {
        return favoriteRepository.getAllFavorites();
    }
    
    public void saveHistoryLocation(HistoryLocation location) {
        locationRepository.insertOrUpdateLocation(location);
    }
    
    public void addFavorite(FavoriteLocation favorite) {
        favoriteRepository.saveFavorite(favorite);
    }
    
    // =============== Settings ===============
    
    public SettingsRepository getSettingsRepository() {
        return settingsRepository;
    }
    
    public void setKeepScreenOn(boolean keepOn) {
        settingsRepository.setKeepScreenOn(keepOn);
    }
    
    public boolean isKeepScreenOn() {
        return settingsRepository.isKeepScreenOn();
    }
    
    public void setMockInterval(int interval) {
        settingsRepository.setMockInterval(interval);
    }
    
    public int getMockInterval() {
        return settingsRepository.getMockInterval();
    }

    // =============== Map Engine Support ===============

    public LiveData<MapEngine.MapProvider> getMapProvider() {
        return mapProvider;
    }

    public void setMapProvider(MapEngine.MapProvider provider) {
        mapProvider.setValue(provider);
    }

    public LiveData<MapEngine.MapType> getMapDisplayType() {
        return mapDisplayType;
    }

    public void setMapDisplayType(MapEngine.MapType type) {
        mapDisplayType.setValue(type);
    }
}
