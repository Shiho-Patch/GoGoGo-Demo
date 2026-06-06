package com.zcshou.gogogo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.elvishew.xlog.XLog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zcshou.gogogo.data.entity.Route;
import com.zcshou.gogogo.databinding.ActivityRouteRunBinding;
import com.zcshou.gogogo.ui.base.BaseActivity;
import com.zcshou.gogogo.ui.viewmodel.RouteRunViewModel;
import com.zcshou.service.ServiceGo;
import com.zcshou.utils.GoUtils;
import com.zcshou.utils.MapUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RouteRunActivity extends BaseActivity {
    private ActivityRouteRunBinding binding;
    private RouteRunViewModel viewModel;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private List<LatLng> mCurrentRoutePoints = new ArrayList<>();

    private ServiceGo.ServiceGoBinder mServiceBinder;
    private boolean isBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceBinder = (ServiceGo.ServiceGoBinder) service;
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBinder = null;
            isBound = false;
        }
    };

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(RouteRunViewModel.class);
    }

    @Override
    protected void initViews() {
        binding = ActivityRouteRunBinding.inflate(getLayoutInflater());
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
        bindGoService();
        
        // 检查是否有从 RouteActivity 传递过来的路线 ID
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("routeId")) {
            int routeId = intent.getIntExtra("routeId", -1);
            if (routeId != -1) {
                // 从 Repository 加载路线
                loadRouteById(routeId);
            }
        }
    }

    @Override
    protected void initObservers() {
        viewModel.getIsSimulating().observe(this, simulating -> {
            binding.btnRunToggle.setText(simulating ? R.string.stop_simulation : R.string.start_simulation);
            binding.btnRunToggle.setIconResource(simulating ? R.drawable.ic_close : R.drawable.ic_play_arrow_24);
        });

        viewModel.getCurrentRoute().observe(this, route -> {
            if (route != null) {
                binding.tvCurrentRoute.setText(getString(R.string.current_route_prefix) + route.routeName);
            } else {
                binding.tvCurrentRoute.setText(R.string.no_route_selected);
            }
        });
    }
    
    /**
     * 根据 ID 加载路线
     */
    private void loadRouteById(int routeId) {
        // 注意：这里我们需要从 Repository 获取路线
        // 由于 Repository 的 getAllRoutes() 返回 LiveData，我们可以通过观察它来查找
        viewModel.getAllRoutes().observe(this, routes -> {
            if (routes != null) {
                for (Route route : routes) {
                    if (route.id == routeId) {
                        loadRoute(route);
                        break;
                    }
                }
            }
        });
    }

    private void initMap() {
        mMapView = binding.mapRun;
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
    }

    private void initButtons() {
        binding.btnRunLoad.setOnClickListener(v -> showRouteList());
        binding.btnRunToggle.setOnClickListener(v -> toggleSimulation());
        
        binding.cbSpeedFloat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setSpeedFloat(isChecked);
        });
    }

    private void showRouteList() {
        if (Boolean.TRUE.equals(viewModel.getIsSimulating().getValue())) {
            showToast(R.string.please_stop_simulation_first);
            return;
        }

        List<Route> routes = viewModel.getAllRoutes().getValue();
        if (routes == null || routes.isEmpty()) {
            showToast(R.string.no_saved_routes);
            return;
        }

        String[] names = new String[routes.size()];
        for (int i = 0; i < routes.size(); i++) {
            names[i] = routes.get(i).routeName;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.select_route_title)
                .setItems(names, (dialog, which) -> {
                    loadRoute(routes.get(which));
                })
                .show();
    }

    private void loadRoute(Route route) {
        try {
            mCurrentRoutePoints.clear();
            mBaiduMap.clear();

            JSONArray ja = new JSONArray(route.pointsJson);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                mCurrentRoutePoints.add(new LatLng(jo.getDouble("lat"), jo.getDouble("lng")));
            }

            drawRouteOnMap();
            viewModel.setCurrentRoute(route);

            if (!mCurrentRoutePoints.isEmpty()) {
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(mCurrentRoutePoints.get(0), 16.0f));
            }

        } catch (Exception e) {
            XLog.e("Failed to load route", e);
            showToast(R.string.route_parse_failed);
        }
    }

    private void drawRouteOnMap() {
        if (mCurrentRoutePoints.size() < 2) return;

        BitmapDescriptor bitmap = getBitmapFromDrawable(R.drawable.icon_gcoding);
        if (bitmap != null) {
            mBaiduMap.addOverlay(new MarkerOptions().position(mCurrentRoutePoints.get(0)).icon(bitmap));
            mBaiduMap.addOverlay(new MarkerOptions().position(mCurrentRoutePoints.get(mCurrentRoutePoints.size() - 1)).icon(bitmap));
        }

        OverlayOptions polyline = new PolylineOptions()
                .points(mCurrentRoutePoints)
                .width(10)
                .color(0xAA0000FF);
        mBaiduMap.addOverlay(polyline);
    }

    private void toggleSimulation() {
        if (mCurrentRoutePoints.size() < 2) {
            showToast(R.string.please_select_valid_route);
            return;
        }
        if (!isBound || mServiceBinder == null) {
            showToast(R.string.service_not_connected);
            bindGoService();
            return;
        }

        boolean isSimulating = Boolean.TRUE.equals(viewModel.getIsSimulating().getValue());

        if (isSimulating) {
            mServiceBinder.stopRoute();
            viewModel.setIsSimulating(false);
        } else {
            if (!GoUtils.isDeveloperOptionsEnabled(this)) {
                GoUtils.DisplayToast(this, getResources().getString(R.string.app_error_dev));
                return;
            }

            // Get values from UI
            String speedStr = binding.etRunSpeed.getText().toString();
            double speed = TextUtils.isEmpty(speedStr) ? 3.8 : Double.parseDouble(speedStr);
            viewModel.setSpeed(speed);

            String loopStr = binding.etLoopCount.getText().toString();
            int loopCount = TextUtils.isEmpty(loopStr) ? 10 : Integer.parseInt(loopStr);
            if (loopCount <= 0) loopCount = 1;
            viewModel.setLoopCount(loopCount);

            boolean isFloat = binding.cbSpeedFloat.isChecked();

            List<double[]> wgsPoints = new ArrayList<>();
            for (LatLng p : mCurrentRoutePoints) {
                wgsPoints.add(MapUtils.bd2wgs(p.longitude, p.latitude));
            }

            mServiceBinder.startRoute(wgsPoints, speed, loopCount, isFloat);
            viewModel.setIsSimulating(true);

            String tips = String.format(getString(R.string.start_simulation_toast), loopCount);
            if (isFloat) {
                tips = String.format(getString(R.string.start_simulation_toast_with_float), loopCount);
            }
            showToast(tips);
        }
    }

    private BitmapDescriptor getBitmapFromDrawable(int resId) {
        android.graphics.drawable.Drawable drawable = androidx.core.content.ContextCompat.getDrawable(this, resId);
        if (drawable == null) return null;
        int width = (int) (30 * getResources().getDisplayMetrics().density);
        int height = (int) (30 * getResources().getDisplayMetrics().density);
        drawable.setBounds(0, 0, width, height);
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void bindGoService() {
        Intent intent = new Intent(this, ServiceGo.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
        if (mMapView != null) mMapView.onDestroy();
        if (isBound) unbindService(mConnection);
    }
}
