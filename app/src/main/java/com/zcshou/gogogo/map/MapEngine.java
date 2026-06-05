package com.zcshou.gogogo.map;

import android.content.Context;
import android.location.Location;
import android.view.View;

public interface MapEngine {

    enum MapType {
        NORMAL, // 普通地图
        SATELLITE, // 卫星地图
        TRAFFIC // 交通地图
    }

    enum MapProvider {
        BAIDU, // 百度地图
        AMAP // 高德地图
    }

    void init(Context context);

    View getMapView();

    void onResume();

    void onPause();

    void onDestroy();

    void setMapType(MapType type);

    void moveToLocation(double lat, double lng, float zoom);

    void setMyLocationEnabled(boolean enabled);

    void setOnMapClickListener(OnMapClickListener listener);

    void addMarker(double lat, double lng, String title);

    void clearMarkers();

    void setTrafficEnabled(boolean enabled);

    interface OnMapClickListener {
        void onMapClick(double lat, double lng);
    }

    MapProvider getProvider();

    void setOnMapLoadedListener(OnMapLoadedListener listener);

    interface OnMapLoadedListener {
        void onMapLoaded();
    }
}
