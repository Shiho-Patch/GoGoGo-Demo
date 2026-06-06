package com.zcshou.gogogo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.elvishew.xlog.XLog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zcshou.gogogo.databinding.ActivityRouteCreateBinding;
import com.zcshou.gogogo.ui.base.BaseActivity;
import com.zcshou.gogogo.ui.viewmodel.RouteCreateViewModel;
import com.zcshou.utils.GoUtils;

import java.util.ArrayList;
import java.util.List;

public class RouteCreateActivity extends BaseActivity {
    private ActivityRouteCreateBinding binding;
    private RouteCreateViewModel viewModel;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private List<LatLng> mPoints = new ArrayList<>();
    private LocationClient mLocClient;

    // 从 MainActivity 传过来的初始坐标
    private LatLng mInitLoc;

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(RouteCreateViewModel.class);
    }

    @Override
    protected void initViews() {
        binding = ActivityRouteCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initMap();
        initButtons();
    }

    @Override
    protected void initData() {
        parseIntent();
        initLocationClient();

        if (mInitLoc != null) {
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(mInitLoc, 18.0f));
            updateBlueDot(mInitLoc.latitude, mInitLoc.longitude, 0, 0);
            viewModel.setCurrentLocation(mInitLoc.latitude, mInitLoc.longitude);
        } else {
            checkPermissionsAndLocate();
        }
    }

    @Override
    protected void initObservers() {
        viewModel.getSuccessMessage().observe(this, message -> {
            if (!TextUtils.isEmpty(message)) {
                showToast(message);
                viewModel.clearSuccess();
                if (message.equals(getString(R.string.save_success))) {
                    finish();
                }
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (!TextUtils.isEmpty(message)) {
                showToast(message);
                viewModel.clearError();
            }
        });
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            double lat = intent.getDoubleExtra("lat", 0.0);
            double lng = intent.getDoubleExtra("lng", 0.0);
            if (lat != 0.0 && lng != 0.0) {
                mInitLoc = new LatLng(lat, lng);
            }
        }
    }

    private void initMap() {
        mMapView = binding.mapCreate;
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18.0f));

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addPoint(latLng);
            }
            @Override
            public void onMapPoiClick(com.baidu.mapapi.map.MapPoi mapPoi) {
                addPoint(mapPoi.getPosition());
            }
        });
    }

    private void initButtons() {
        binding.btnCreateClear.setOnClickListener(v -> clearPoints());
        binding.btnCreateSave.setOnClickListener(v -> showSaveDialog());
        binding.btnCreateLocation.setOnClickListener(v -> {
            showToast(getString(R.string.refreshing_location));
            checkPermissionsAndLocate();
        });
    }

    private void initLocationClient() {
        try {
            mLocClient = new LocationClient(getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll");
            option.setScanSpan(1000);
            option.setOpenGnss(true);
            option.setIgnoreKillProcess(true);
            mLocClient.setLocOption(option);

            mLocClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation location) {
                    if (location == null || mMapView == null) return;

                    if (Math.abs(location.getLatitude()) < 0.1 && Math.abs(location.getLongitude()) < 0.1) return;
                    if (location.getLocType() == BDLocation.TypeServerError) return;

                    updateBlueDot(location.getLatitude(), location.getLongitude(), location.getRadius(), location.getDirection());
                    viewModel.setCurrentLocation(location.getLatitude(), location.getLongitude());

                    if (mInitLoc == null) {
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(
                                new LatLng(location.getLatitude(), location.getLongitude())));
                        mInitLoc = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                }
            });
        } catch (Exception e) {
            XLog.e("Error initializing location client", e);
        }
    }

    private void checkPermissionsAndLocate() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            } else {
                startLocation();
            }
        } else {
            startLocation();
        }
    }

    private void startLocation() {
        if (mLocClient != null) {
            if (mLocClient.isStarted()) {
                mLocClient.requestLocation();
            } else {
                mLocClient.start();
            }
            mInitLoc = null;
        }
    }

    private void updateBlueDot(double lat, double lng, float radius, float direction) {
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(radius)
                .direction(direction)
                .latitude(lat)
                .longitude(lng)
                .build();
        mBaiduMap.setMyLocationData(locData);
    }

    private void addPoint(LatLng latLng) {
        mPoints.add(latLng);
        viewModel.addPoint(latLng.latitude, latLng.longitude);

        BitmapDescriptor bitmap = getBitmapFromDrawable(R.drawable.icon_gcoding);
        if (bitmap != null) {
            mBaiduMap.addOverlay(new MarkerOptions().position(latLng).icon(bitmap));
        }

        if (mPoints.size() >= 2) {
            OverlayOptions polyline = new PolylineOptions()
                    .points(mPoints)
                    .width(10)
                    .color(0xAAFF0000);
            mBaiduMap.addOverlay(polyline);
        }
    }

    private void clearPoints() {
        mPoints.clear();
        viewModel.clearPoints();
        mBaiduMap.clear();

        if (mLocClient != null && mLocClient.getLastKnownLocation() != null) {
            BDLocation loc = mLocClient.getLastKnownLocation();
            updateBlueDot(loc.getLatitude(), loc.getLongitude(), loc.getRadius(), loc.getDirection());
        } else if (mInitLoc != null) {
            updateBlueDot(mInitLoc.latitude, mInitLoc.longitude, 0, 0);
        }

        showToast(getString(R.string.clear_redraw));
    }

    private void showSaveDialog() {
        if (viewModel.getPointCount() < 2) {
            showToast(getString(R.string.please_draw_at_least_two_points));
            return;
        }

        final EditText input = new EditText(this);
        input.setHint(R.string.enter_route_name);
        input.setPadding(
                getResources().getDimensionPixelSize(R.dimen.spacing_md),
                getResources().getDimensionPixelSize(R.dimen.spacing_md),
                getResources().getDimensionPixelSize(R.dimen.spacing_md),
                getResources().getDimensionPixelSize(R.dimen.spacing_md)
        );

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.save_route_title)
                .setView(input)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(name)) {
                        viewModel.saveRoute(name);
                    } else {
                        showToast(getString(R.string.name_cannot_be_empty));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private BitmapDescriptor getBitmapFromDrawable(int resId) {
        android.graphics.drawable.Drawable drawable = ContextCompat.getDrawable(this, resId);
        if (drawable == null) return null;
        int width = (int) (30 * getResources().getDisplayMetrics().density);
        int height = (int) (30 * getResources().getDisplayMetrics().density);
        drawable.setBounds(0, 0, width, height);
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocation();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMapView != null) mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocClient != null) mLocClient.stop();
        if (mMapView != null) mMapView.onDestroy();
    }
}
