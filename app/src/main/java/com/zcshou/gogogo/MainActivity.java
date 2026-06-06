package com.zcshou.gogogo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.zcshou.gogogo.databinding.ActivityMainBinding;
import com.zcshou.gogogo.map.MapEngine;
import com.zcshou.gogogo.map.MapEngineFactory;
import com.zcshou.gogogo.ui.base.BaseActivity;
import com.zcshou.gogogo.ui.dialog.MapOptionsDialogFragment;
import com.zcshou.gogogo.ui.viewmodel.MainViewModel;
import com.zcshou.gogogo.ui.viewmodel.FavoriteViewModel;
import com.zcshou.service.ServiceGo;
import com.zcshou.utils.GoUtils;
import com.zcshou.utils.MapUtils;

import com.elvishew.xlog.XLog;

public class MainActivity extends BaseActivity implements MapOptionsDialogFragment.MapOptionsListener {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private FavoriteViewModel favoriteViewModel;
    
    // 地图引擎
    private MapEngine mapEngine;
    private MapEngine.MapProvider currentProvider = MapEngine.MapProvider.BAIDU;
    private MapEngine.MapType currentMapType = MapEngine.MapType.NORMAL;
    
    // 收藏夹
    private ActivityResultLauncher<Intent> favoriteActivityLauncher;
    
    // 百度地图相关（临时）
    private LocationClient mLocClient = null;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private boolean isFirstLoc = true;
    private static LatLng mMarkLatLngMap = null;
    
    // Service连接
    private boolean isMockServStart = false;
    private ServiceGo.ServiceGoBinder mServiceBinder;
    private ServiceConnection mConnection;
    private BottomSheetBehavior<ViewGroup> locationSheetBehavior;

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
    }

    @Override
    protected void initViews() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 初始化收藏夹 ActivityResultLauncher
        initFavoriteActivityLauncher();
        
        // 初始化地图引擎
        initMapEngine();
        
        // 初始化Toolbar
        setSupportActionBar(binding.toolbar);
        
        // 初始化DrawerLayout
        initNavigationView();
        
        // 初始化底部抽屉
        initBottomSheet();
        
        // 初始化FAB按钮
        initFabButtons();
        
        // 初始化Service连接
        initServiceConnection();
    }
    
    private void initFavoriteActivityLauncher() {
        favoriteActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    double lat = data.getDoubleExtra(FavoriteActivity.EXTRA_LATITUDE, 0.0);
                    double lng = data.getDoubleExtra(FavoriteActivity.EXTRA_LONGITUDE, 0.0);
                    String name = data.getStringExtra(FavoriteActivity.EXTRA_NAME);
                    String address = data.getStringExtra(FavoriteActivity.EXTRA_ADDRESS);
                    
                    // 使用选择的位置
                    onFavoriteLocationSelected(lat, lng, name, address);
                }
            }
        );
    }

    @Override
    protected void initData() {
        checkAndRequestPermissions();
        initMapLocation();
    }

    @Override
    protected void initObservers() {
        // 观察地图提供商标化
        viewModel.getMapProvider().observe(this, provider -> {
            currentProvider = provider;
            switchMapProvider(provider);
        });
        
        // 观察地图类型变化
        viewModel.getMapDisplayType().observe(this, type -> {
            currentMapType = type;
            if (mapEngine != null) {
                mapEngine.setMapType(type);
            }
        });
        
        // 观察模拟状态
        viewModel.getIsMocking().observe(this, isMocking -> {
            updateFabIcon(isMocking);
        });
    }

    private void initMapEngine() {
        // 使用百度地图引擎（默认）
        mapEngine = MapEngineFactory.createEngine(this, currentProvider);
        mapEngine.init(this);
        
        // 添加地图视图
        View mapView = mapEngine.getMapView();
        if (binding.mapContainer.getChildCount() > 0) {
            binding.mapContainer.removeAllViews();
        }
        binding.mapContainer.addView(mapView, 0);
        
        // 设置地图点击监听
        mapEngine.setOnMapClickListener(this::onMapClicked);
        
        // 移动到默认位置
        double defaultLat = 36.547743718042415;
        double defaultLng = 117.07018449827267;
        mapEngine.moveToLocation(defaultLat, defaultLng, 16.0f);
    }

    private void switchMapProvider(MapEngine.MapProvider provider) {
        if (currentProvider == provider && mapEngine != null) {
            return;
        }
        
        XLog.i("Switching map provider to: " + provider);
        
        // 保存当前地图状态
        double currentLat = viewModel.getCurrentLatitude().getValue() != null ? 
            viewModel.getCurrentLatitude().getValue() : 36.547743718042415;
        double currentLng = viewModel.getCurrentLongitude().getValue() != null ? 
            viewModel.getCurrentLongitude().getValue() : 117.07018449827267;
        
        // 销毁旧引擎
        if (mapEngine != null) {
            mapEngine.onDestroy();
        }
        
        // 创建新引擎
        currentProvider = provider;
        mapEngine = MapEngineFactory.createEngine(this, provider);
        mapEngine.init(this);
        
        // 替换地图视图
        binding.mapContainer.removeAllViews();
        View mapView = mapEngine.getMapView();
        binding.mapContainer.addView(mapView, 0);
        
        // 设置点击监听
        mapEngine.setOnMapClickListener(this::onMapClicked);
        
        // 恢复地图位置
        mapEngine.setMapType(currentMapType);
        mapEngine.moveToLocation(currentLat, currentLng, 16.0f);
    }

    private void initBottomSheet() {
        locationSheetBehavior = BottomSheetBehavior.from(binding.locationBottomSheet.getRoot());
        locationSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        locationSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_sheet_peek_height));
        
        // 展开/收起按钮
        binding.locationBottomSheet.collapsedInfo.setOnClickListener(v -> {
            if (locationSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                locationSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (locationSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                locationSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        
        // 分享按钮
        binding.locationBottomSheet.btnShare.setOnClickListener(v -> {
            shareCurrentLocation();
        });
        
        // 保存收藏按钮
        binding.locationBottomSheet.btnSaveFavorite.setOnClickListener(v -> {
            saveCurrentLocationAsFavorite();
        });
    }

    private void initFabButtons() {
        // 主FAB - 开始/停止模拟
        binding.fabMain.setOnClickListener(v -> toggleMocking());
        
        // 我的位置
        binding.fabMyLocation.setOnClickListener(v -> moveToMyLocation());
        
        // 放大地图
        binding.fabZoomIn.setOnClickListener(v -> {
            if (mapEngine != null) {
                // 这里可以实现地图放大
            }
        });
        
        // 缩小地图
        binding.fabZoomOut.setOnClickListener(v -> {
            if (mapEngine != null) {
                // 这里可以实现地图缩小
            }
        });
    }

    private void initServiceConnection() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mServiceBinder = (ServiceGo.ServiceGoBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
    }

    private void initNavigationView() {
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_route_create) {
                Intent intent = new Intent(MainActivity.this, RouteCreateActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_route) {
                Intent intent = new Intent(MainActivity.this, RouteActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_route_run) {
                Intent intent = new Intent(MainActivity.this, RouteRunActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_favorites) {
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                favoriteActivityLauncher.launch(intent);
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_dev) {
                if (!GoUtils.isDeveloperOptionsEnabled(this)) {
                    GoUtils.DisplayToast(this, getResources().getString(R.string.app_error_dev));
                } else {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                        startActivity(intent);
                    } catch (Exception e) {
                        GoUtils.DisplayToast(this, getResources().getString(R.string.app_error_dev));
                    }
                }
            } else if (id == R.id.nav_feedback) {
                // 反馈功能
            } else if (id == R.id.nav_contact) {
                // 联系功能
            }
            
            DrawerLayout drawer = binding.drawerLayout;
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void initMapLocation() {
        try {
            // 初始化百度定位（临时兼容）
            mLocClient = new LocationClient(this);
            mLocClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation == null) return;
                    
                    if (Math.abs(bdLocation.getLatitude()) < 0.0001 || 
                        Math.abs(bdLocation.getLongitude()) < 0.0001 ||
                        bdLocation.getLatitude() == 4.9E-324 || 
                        bdLocation.getLongitude() == 4.9E-324) {
                        return;
                    }

                    mCurrentLat = bdLocation.getLatitude();
                    mCurrentLon = bdLocation.getLongitude();
                    
                    if (isFirstLoc) {
                        isFirstLoc = false;
                        mMarkLatLngMap = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                        mapEngine.moveToLocation(bdLocation.getLatitude(), bdLocation.getLongitude(), 16.0f);
                    }
                }
            });
            
            LocationClientOption locationOption = new LocationClientOption();
            locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            locationOption.setCoorType("bd09ll");
            locationOption.setScanSpan(1000);
            locationOption.setIsNeedAddress(true);
            mLocClient.setLocOption(locationOption);
            mLocClient.start();
        } catch (Exception e) {
            XLog.e("ERROR: initMapLocation");
        }
    }

    private void onMapClicked(double lat, double lng) {
        XLog.i("Map clicked at: " + lat + ", " + lng);
        mMarkLatLngMap = new LatLng(lat, lng);
        
        // 更新ViewModel
        viewModel.setCurrentLatitude(lat);
        viewModel.setCurrentLongitude(lng);
        viewModel.setCurrentLocationName(getString(R.string.unknown_location));
        
        // 刷新底部抽屉
        updateLocationInfo(lat, lng);
        
        // 添加地图标记
        addMarkerToMap(lat, lng);
    }

    private void addMarkerToMap(double lat, double lng) {
        // 使用地图引擎添加标记（未来实现）
        // 临时使用百度地图方式
        if (mapEngine != null) {
            mapEngine.addMarker(lat, lng, "当前位置");
        }
    }

    private void updateLocationInfo(double lat, double lng) {
        binding.locationBottomSheet.tvCoordinates.setText(String.format("%.6f, %.6f", lat, lng));
        binding.locationBottomSheet.tvLatitude.setText(String.format("%.6f", lat));
        binding.locationBottomSheet.tvLongitude.setText(String.format("%.6f", lng));
    }

    private void toggleMocking() {
        if (!checkPermissions()) {
            return;
        }
        
        if (isMockServStart) {
            if (mMarkLatLngMap != null) {
                updateMockLocation();
            } else {
                stopMocking();
            }
        } else {
            if (mMarkLatLngMap == null) {
                GoUtils.DisplayToast(this, getString(R.string.please_select_location));
                return;
            }
            
            if (!GoUtils.isAllowMockLocation(this)) {
                GoUtils.showEnableMockLocationDialog(this);
                return;
            }
            
            startMocking();
        }
    }

    private void startMocking() {
        Intent serviceGoIntent = new Intent(MainActivity.this, ServiceGo.class);
        bindService(serviceGoIntent, mConnection, BIND_AUTO_CREATE);
        
        double[] latLng = MapUtils.bd2wgs(mMarkLatLngMap.longitude, mMarkLatLngMap.latitude);
        serviceGoIntent.putExtra(LNG_MSG_ID, latLng[0]);
        serviceGoIntent.putExtra(LAT_MSG_ID, latLng[1]);
        
        startForegroundService(serviceGoIntent);
        
        isMockServStart = true;
        viewModel.setMocking(true);
        
        GoUtils.DisplayToast(this, getString(R.string.mocking_active));
        
        // 清理标记
        if (mapEngine != null) {
            mapEngine.clearMarkers();
        }
        mMarkLatLngMap = null;
    }

    private void stopMocking() {
        unbindService(mConnection);
        Intent serviceGoIntent = new Intent(MainActivity.this, ServiceGo.class);
        stopService(serviceGoIntent);
        
        isMockServStart = false;
        viewModel.setMocking(false);
        
        GoUtils.DisplayToast(this, getString(R.string.mocking_stopped));
    }

    private void updateMockLocation() {
        double[] latLng = MapUtils.bd2wgs(mMarkLatLngMap.longitude, mMarkLatLngMap.latitude);
        double alt = 55.0;
        mServiceBinder.setPosition(latLng[0], latLng[1], alt);
        
        GoUtils.DisplayToast(this, getString(R.string.location_updated));
        
        // 保存历史
        viewModel.saveHistoryLocation(null); // 这里需要实现
        
        // 清理标记
        if (mapEngine != null) {
            mapEngine.clearMarkers();
        }
        mMarkLatLngMap = null;
    }

    private void updateFabIcon(boolean isMocking) {
        FloatingActionButton fab = binding.fabMain;
        if (isMocking) {
            fab.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            fab.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void moveToMyLocation() {
        if (Math.abs(mCurrentLat) > 0.1 && Math.abs(mCurrentLon) > 0.1) {
            mapEngine.moveToLocation(mCurrentLat, mCurrentLon, 16.0f);
        } else {
            mapEngine.moveToLocation(36.547743718042415, 117.07018449827267, 16.0f);
        }
    }

    private void shareCurrentLocation() {
        Double lat = viewModel.getCurrentLatitude().getValue();
        Double lng = viewModel.getCurrentLongitude().getValue();
        if (lat != null && lng != null) {
            String locationText = String.format("位置: %s\n经度: %.6f\n纬度: %.6f", 
                viewModel.getCurrentLocationName().getValue(), lng, lat);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, locationText);
            startActivity(Intent.createChooser(intent, getString(R.string.share)));
        }
    }

    private void saveCurrentLocationAsFavorite() {
        Double lat = viewModel.getCurrentLatitude().getValue();
        Double lng = viewModel.getCurrentLongitude().getValue();
        if (lat != null && lng != null) {
            showAddFavoriteDialog(lat, lng);
        }
    }
    
    private void showAddFavoriteDialog(double lat, double lng) {
        // 显示对话框让用户输入收藏名称
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.add_favorite);
        
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint(R.string.favorite_name);
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        input.setLayoutParams(lp);
        builder.setView(input);
        
        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                // 转换为 WGS84 坐标
                double[] wgsLatLng = MapUtils.bd2wgs(lng, lat);
                
                // 保存到收藏
                favoriteViewModel.addFavorite(
                    name,
                    getString(R.string.unknown_address),
                    wgsLatLng[1], // latitude WGS84
                    wgsLatLng[0], // longitude WGS84
                    lng, // longitude Custom (BD09)
                    lat // latitude Custom (BD09)
                );
                
                GoUtils.DisplayToast(this, getString(R.string.favorite_saved));
            }
        });
        
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }
    
    private void onFavoriteLocationSelected(double lat, double lng, String name, String address) {
        // 使用从收藏夹选择的位置
        mMarkLatLngMap = new LatLng(lat, lng);
        
        // 更新ViewModel
        viewModel.setCurrentLatitude(lat);
        viewModel.setCurrentLongitude(lng);
        viewModel.setCurrentLocationName(name != null ? name : getString(R.string.unknown_location));
        
        // 刷新底部抽屉
        updateLocationInfo(lat, lng);
        binding.locationBottomSheet.tvLocationName.setText(name != null ? name : getString(R.string.unknown_location));
        
        // 添加地图标记
        addMarkerToMap(lat, lng);
        
        // 移动到该位置
        if (mapEngine != null) {
            mapEngine.moveToLocation(lat, lng, 16.0f);
        }
        
        GoUtils.DisplayToast(this, getString(R.string.location_updated));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            // 搜索功能
            showSearchMenu();
        } else if (id == R.id.action_routes) {
            Intent intent = new Intent(this, RouteActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_map_type) {
            // 显示地图选项对话框
            showMapOptionsDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMapOptionsDialog() {
        MapOptionsDialogFragment dialog = MapOptionsDialogFragment.newInstance(
            currentProvider, 
            currentMapType, 
            this
        );
        dialog.show(getSupportFragmentManager(), "MapOptions");
    }

    private void showSearchMenu() {
        // 搜索菜单逻辑
    }

    @Override
    public void onProviderChanged(MapEngine.MapProvider provider) {
        viewModel.setMapProvider(provider);
    }

    @Override
    public void onMapTypeChanged(MapEngine.MapType type) {
        viewModel.setMapDisplayType(type);
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void checkAndRequestPermissions() {
        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this, 
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION, 
                               Manifest.permission.ACCESS_COARSE_LOCATION },
                PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                GoUtils.DisplayToast(this, getString(R.string.permission_required));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapEngine != null) {
            mapEngine.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapEngine != null) {
            mapEngine.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isMockServStart) {
            unbindService(mConnection);
            Intent serviceGoIntent = new Intent(MainActivity.this, ServiceGo.class);
            stopService(serviceGoIntent);
        }
        if (mapEngine != null) {
            mapEngine.onDestroy();
        }
    }

    public static final String LAT_MSG_ID = "LAT_VALUE";
    public static final String LNG_MSG_ID = "LNG_VALUE";
    public static final String ALT_MSG_ID = "ALT_VALUE";
    public static final int PERMISSION_REQUEST_CODE = 100;
}
