package com.zcshou.gogogo.map;

import android.content.Context;
import android.location.Location;
import android.view.View;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.ArrayList;
import java.util.List;

public class AMapEngine implements MapEngine {

    private MapView mapView;
    private AMap aMap;
    private Context context;
    private List<Marker> markers = new ArrayList<>();
    private OnMapClickListener mapClickListener;
    private OnMapLoadedListener mapLoadedListener;

    @Override
    public void init(Context context) {
        this.context = context;

        mapView = new MapView(context);
        aMap = mapView.getMap();

        // 配置地图
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);

        // 设置地图点击监听
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mapClickListener != null) {
                    mapClickListener.onMapClick(latLng.latitude, latLng.longitude);
                }
            }
        });

        // POI 点击监听
        aMap.setOnPOIClickListener(new AMap.OnPOIClickListener() {
            @Override
            public void onPOIClick(com.amap.api.maps.model.Poi poi) {
                // POI 点击
            }
        });

        // 地图加载完成回调
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                if (mapLoadedListener != null) {
                    mapLoadedListener.onMapLoaded();
                }
            }
        });
    }

    @Override
    public View getMapView() {
        return mapView;
    }

    @Override
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void setMapType(MapType type) {
        if (aMap == null) return;
        
        switch (type) {
            case NORMAL:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                break;
            case SATELLITE:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                break;
            case TRAFFIC:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                aMap.setTrafficEnabled(true);
                break;
        }
    }

    @Override
    public void moveToLocation(double lat, double lng, float zoom) {
        if (aMap == null) return;
        
        LatLng latLng = new LatLng(lat, lng);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(zoom)
                .build();
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void setMyLocationEnabled(boolean enabled) {
        if (aMap != null) {
            aMap.setMyLocationEnabled(enabled);
        }
    }

    @Override
    public void setOnMapClickListener(OnMapClickListener listener) {
        this.mapClickListener = listener;
    }

    @Override
    public void addMarker(double lat, double lng, String title) {
        if (aMap == null) return;
        
        LatLng point = new LatLng(lat, lng);
        
        // 构建 MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions()
                .position(point)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        
        Marker marker = aMap.addMarker(markerOptions);
        markers.add(marker);
    }

    @Override
    public void clearMarkers() {
        if (aMap != null) {
            aMap.clear();
            markers.clear();
        }
    }

    @Override
    public void setTrafficEnabled(boolean enabled) {
        if (aMap != null) {
            aMap.setTrafficEnabled(enabled);
        }
    }

    @Override
    public MapProvider getProvider() {
        return MapProvider.AMAP;
    }

    @Override
    public void setOnMapLoadedListener(OnMapLoadedListener listener) {
        this.mapLoadedListener = listener;
    }
}
