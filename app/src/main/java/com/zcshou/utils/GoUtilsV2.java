package com.zcshou.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.provider.ProviderProperties;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.elvishew.xlog.XLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 通用工具类（重构优化版）
 * <p>
 * 改进点：
 * 1. 完善的日志记录
 * 2. 更好的错误处理
 * 3. 代码结构优化
 * 4. 添加常量定义
 */
public class GoUtilsV2 {
    private static final String TAG = "GoUtilsV2";
    
    // 常量定义
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final int TOAST_Y_OFFSET = 100;

    /**
     * 检查开发者选项是否已启用
     */
    public static boolean isDeveloperOptionsEnabled(Context context) {
        try {
            return Settings.Global.getInt(
                    context.getContentResolver(),
                    Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                    0
            ) == 1;
        } catch (Exception e) {
            XLog.e(TAG, "Error checking developer options", e);
            return false;
        }
    }

    /**
     * 检查 WIFI 是否已连接
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }
        
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    /**
     * 检查 WIFI 是否已启用
     */
    public static boolean isWifiEnabled(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return wifiManager != null && wifiManager.isWifiEnabled();
        } catch (Exception e) {
            XLog.e(TAG, "Error checking WIFI status", e);
            return false;
        }
    }

    /**
     * 检查移动数据是否已连接
     */
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }
        
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
    }

    /**
     * 检查是否有网络连接
     * 注意：这只能检测到网络连接状态，无法确定是否真的能上网
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }
        
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        );
    }

    /**
     * 检查网络是否可用（已连接 WIFI 或移动数据）
     */
    public static boolean isNetworkAvailable(Context context) {
        return (isWifiConnected(context) || isMobileConnected(context)) && isNetworkConnected(context);
    }

    /**
     * 检查 GPS 是否已打开
     */
    public static boolean isGpsOpened(Context context) {
        try {
            LocationManager locationManager = 
                    (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager != null && 
                   locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            XLog.e(TAG, "Error checking GPS status", e);
            return false;
        }
    }

    /**
     * 检查是否已在开发者选项中开启模拟位置权限
     */
    @SuppressLint("wrongconstant")
    public static boolean isAllowMockLocation(Context context) {
        boolean canMockPosition = false;

        try {
            LocationManager locationManager = 
                    (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null) {
                return false;
            }

            // 检查 GPS provider 是否存在
            boolean hasGpsProvider = locationManager.getAllProviders()
                    .contains(LocationManager.GPS_PROVIDER);

            if (hasGpsProvider) {
                // 尝试添加测试 provider 来验证权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    locationManager.addTestProvider(
                            LocationManager.GPS_PROVIDER,
                            false, true, false, false, true, true, true,
                            ProviderProperties.POWER_USAGE_HIGH,
                            ProviderProperties.ACCURACY_FINE
                    );
                } else {
                    locationManager.addTestProvider(
                            LocationManager.GPS_PROVIDER,
                            false, true, false, false, true, true, true,
                            Criteria.POWER_HIGH,
                            Criteria.ACCURACY_FINE
                    );
                }
                canMockPosition = true;

                // 清理测试 provider
                try {
                    locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
                    locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
                } catch (Exception e) {
                    XLog.w(TAG, "Error cleaning up test provider", e);
                }
            }
        } catch (SecurityException e) {
            XLog.w(TAG, "Mock location permission not granted", e);
        } catch (Exception e) {
            XLog.e(TAG, "Error checking mock location permission", e);
        }

        return canMockPosition;
    }

    /**
     * 获取应用程序版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            XLog.e(TAG, "Package not found", e);
            return "";
        }
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            return context.getResources().getString(applicationInfo.labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            XLog.e(TAG, "Package not found", e);
            return "";
        }
    }

    /**
     * 时间戳转换为日期字符串
     */
    public static String timeStamp2Date(String seconds) {
        if (seconds == null || seconds.isEmpty() || "null".equals(seconds)) {
            return "";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
            long timestamp = Long.parseLong(seconds) * 1000;
            return sdf.format(new Date(timestamp));
        } catch (NumberFormatException e) {
            XLog.e(TAG, "Invalid timestamp: " + seconds, e);
            return "";
        }
    }

    /**
     * 显示开启位置模拟权限的对话框
     */
    public static void showEnableMockLocationDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("启用位置模拟")
                .setMessage("请在\"开发者选项→选择模拟位置信息应用\"中进行设置")
                .setPositiveButton("设置", (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        XLog.e(TAG, "Error opening developer settings", e);
                        DisplayToast(context, "打开设置失败");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 显示开启悬浮窗权限的对话框
     */
    public static void showEnableFloatWindowDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("启用悬浮窗")
                .setMessage("为了模拟定位的稳定性，建议开启\"显示悬浮窗\"选项")
                .setPositiveButton("设置", (dialog, which) -> {
                    try {
                        Intent intent = new Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + context.getPackageName())
                        );
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        XLog.e(TAG, "Error opening overlay settings", e);
                        DisplayToast(context, "打开设置失败");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 显示开启 GPS 的对话框
     */
    public static void showEnableGpsDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("启用定位服务")
                .setMessage("是否开启 GPS 定位服务?")
                .setPositiveButton("确定", (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        XLog.e(TAG, "Error opening location settings", e);
                        DisplayToast(context, "打开设置失败");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 显示提醒关闭 WIFI 的对话框
     */
    public static void showDisableWifiDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("警告")
                .setMessage("开启 WIFI 后（即使没有连接热点）将导致定位闪回真实位置。建议关闭 WIFI，使用移动流量！")
                .setPositiveButton("去关闭", (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        XLog.e(TAG, "Error opening WIFI settings", e);
                        DisplayToast(context, "打开设置失败");
                    }
                })
                .setNegativeButton("忽略", null)
                .show();
    }

    /**
     * 显示 Toast 提示
     */
    public static void DisplayToast(Context context, String message) {
        if (context == null || message == null) {
            return;
        }
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, TOAST_Y_OFFSET);
        toast.show();
    }

    /**
     * 计数器类
     */
    public static class TimeCount extends CountDownTimer {
        private TimeCountListener mListener;

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (mListener != null) {
                mListener.onFinish();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (mListener != null) {
                mListener.onTick(millisUntilFinished);
            }
        }

        public void setListener(TimeCountListener listener) {
            this.mListener = listener;
        }

        public interface TimeCountListener {
            void onTick(long millisUntilFinished);
            void onFinish();
        }
    }
}
