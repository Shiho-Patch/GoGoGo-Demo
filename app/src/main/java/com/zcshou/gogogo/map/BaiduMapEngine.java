package com.zcshou.gogogo.map;

import android.content.Context;
import android.view.View;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class BaiduMapEngine implements MapEngine {

    private MapView mapView;
    private BaiduMap baiduMap;
    private Context context;
    private List<MarkerOptions> markers = new ArrayList<>();
    private OnMapClickListener mapClickListener;
    private OnMapLoadedListener mapLoadedListener;

    @Override
    public void init(Context context) {
        this.context = context;

        // 百度地图 SDK 初始化
        SDKInitializer.setAgreePrivacy(context, true);
        LocationClient.setAgreePrivacy(true);
        SDKInitializer.setApiKey("Ej7yXb88zSEeJBmyUoDw0b40lrQBrU7h");
        SDKInitializer.initialize(context);
        SDKInitializer.setCoordType(CoordType.BD09LL);

        mapView = new MapView(context);
        baiduMap = mapView.getMap();

        // 配置地图
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMyLocationConfiguration(
                new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL,
                        true,
                        null
                )
        );

        // 设置地图点击监听
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mapClickListener != null) {
                    mapClickListener.onMapClick(latLng.latitude, latLng.longitude);
                }
            }

            @Override
            public void onMapPoiClick(com.baidu.mapapi.map.MapPoi mapPoi) {
                // POI 点击
            }
        });

        // 地图加载完成回调
        baiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
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
        if (baiduMap == null) return;
        
        switch (type) {
            case NORMAL:
                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case SATELLITE:
                baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case TRAFFIC:
                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                baiduMap.setTrafficEnabled(true);
                break;
        }
    }

    @Override
    public void moveToLocation(double lat, double lng, float zoom) {
        if (baiduMap == null) return;
        
        LatLng latLng = new LatLng(lat, lng);
        MapStatus.Builder builder = new MapStatus.Builder()
                .target(latLng)
                .zoom(zoom);
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(builder.build());
        baiduMap.animateMapStatus(update);
    }

    @Override
    public void setMyLocationEnabled(boolean enabled) {
        if (baiduMap != null) {
            baiduMap.setMyLocationEnabled(enabled);
        }
    }

    @Override
    public void setOnMapClickListener(OnMapClickListener listener) {
        this.mapClickListener = listener;
    }

    @Override
    public void addMarker(double lat, double lng, String title) {
        if (baiduMap == null) return;
        
        LatLng point = new LatLng(lat, lng);
        
        // 构建 MarkerOptions
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(android.R.drawable.ic_menu_mylocation);
        
        MarkerOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)
                .title(title);
        
        markers.add(option);
        baiduMap.addOverlay(option);
    }

    @Override
    public void clearMarkers() {
        if (baiduMap != null) {
            baiduMap.clear();
            markers.clear();
        }
    }

    @Override
    public void setTrafficEnabled(boolean enabled) {
        if (baiduMap != null) {
            baiduMap.setTrafficEnabled(enabled);
        }
    }

    @Override
    public MapProvider getProvider() {
        return MapProvider.BAIDU;
    }

    @Override
    public void setOnMapLoadedListener(OnMapLoadedListener listener) {
        this.mapLoadedListener = listener;
    }
}
