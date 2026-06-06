package com.zcshou.gogogo.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zcshou.gogogo.data.entity.Route;
import com.zcshou.gogogo.data.repository.RouteRepository;
import com.zcshou.gogogo.data.repository.RepositoryManager;
import com.zcshou.gogogo.ui.base.BaseViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 路线创建 ViewModel
 */
public class RouteCreateViewModel extends BaseViewModel {

    private final RouteRepository routeRepository;
    private final MutableLiveData<List<LatLngPoint>> points = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> currentLat = new MutableLiveData<>();
    private final MutableLiveData<Double> currentLng = new MutableLiveData<>();

    public RouteCreateViewModel(@NonNull Application application) {
        super();
        routeRepository = RepositoryManager.getInstance(application).getRouteRepository();
    }

    public LiveData<List<LatLngPoint>> getPoints() {
        return points;
    }

    public LiveData<Double> getCurrentLat() {
        return currentLat;
    }

    public LiveData<Double> getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLocation(double lat, double lng) {
        currentLat.setValue(lat);
        currentLng.setValue(lng);
    }

    public void addPoint(double lat, double lng) {
        List<LatLngPoint> currentPoints = new ArrayList<>(points.getValue());
        currentPoints.add(new LatLngPoint(lat, lng));
        points.setValue(currentPoints);
    }

    public void clearPoints() {
        points.setValue(new ArrayList<>());
    }

    public int getPointCount() {
        List<LatLngPoint> currentPoints = points.getValue();
        return currentPoints != null ? currentPoints.size() : 0;
    }

    public void saveRoute(String routeName) {
        try {
            List<LatLngPoint> currentPoints = points.getValue();
            if (currentPoints == null || currentPoints.size() < 2) {
                setError("请至少绘制两个点");
                return;
            }

            JSONArray jsonArray = new JSONArray();
            for (LatLngPoint p : currentPoints) {
                JSONObject j = new JSONObject();
                j.put("lat", p.lat);
                j.put("lng", p.lng);
                jsonArray.put(j);
            }

            Route route = new Route(routeName, jsonArray.toString(), System.currentTimeMillis());
            routeRepository.saveRoute(route);
            setSuccess("保存成功");
        } catch (Exception e) {
            setError("保存失败: " + e.getMessage());
        }
    }

    public static class LatLngPoint {
        public final double lat;
        public final double lng;

        public LatLngPoint(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }
}
