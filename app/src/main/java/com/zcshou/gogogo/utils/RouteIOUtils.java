package com.zcshou.gogogo.utils;

import android.content.Context;
import android.util.Log;

import com.zcshou.gogogo.data.entity.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 路线导入导出工具类
 * 支持 JSON 格式的路线文件和剪贴板操作
 */
public class RouteIOUtils {
    private static final String TAG = "RouteIOUtils";
    private static final String ROUTE_DIR = "routes";
    private static final String FILE_EXTENSION = ".json";

    /**
     * 获取路线保存目录
     */
    private static File getRouteDir(Context context) {
        File dir = new File(context.getExternalFilesDir(null), ROUTE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 将 Route 实体转换为 JSON 字符串
     */
    public static String routeToJson(Route route) {
        if (route == null) {
            return null;
        }

        try {
            JSONObject routeJson = new JSONObject();
            routeJson.put("name", route.routeName);
            routeJson.put("pointsJson", route.pointsJson);
            routeJson.put("createTime", route.timestamp);
            return routeJson.toString(2);
        } catch (JSONException e) {
            Log.e(TAG, "Error converting route to JSON", e);
            return null;
        }
    }

    /**
     * 从 JSON 字符串创建 Route 实体
     */
    public static Route jsonToRoute(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }

        try {
            JSONObject routeJson = new JSONObject(jsonString);
            Route route = new Route();
            
            // 优先尝试从 pointsJson 字段获取，这是我们当前存储的格式
            if (routeJson.has("pointsJson")) {
                route.routeName = routeJson.optString("name", "Unknown Route");
                route.pointsJson = routeJson.optString("pointsJson", "[]");
                route.timestamp = routeJson.optLong("createTime", System.currentTimeMillis());
            } else {
                // 兼容旧格式，从 points 数组构建
                route.routeName = routeJson.optString("name", "Unknown Route");
                route.timestamp = routeJson.optLong("createTime", System.currentTimeMillis());
                
                // 从 points 数组构建 pointsJson
                JSONArray pointsArray = routeJson.optJSONArray("points");
                if (pointsArray != null) {
                    JSONArray simplePoints = new JSONArray();
                    for (int i = 0; i < pointsArray.length(); i++) {
                        JSONObject pointJson = pointsArray.getJSONObject(i);
                        JSONObject simplePoint = new JSONObject();
                        simplePoint.put("lat", pointJson.optDouble("latitude", 0));
                        simplePoint.put("lng", pointJson.optDouble("longitude", 0));
                        simplePoints.put(simplePoint);
                    }
                    route.pointsJson = simplePoints.toString();
                } else {
                    route.pointsJson = "[]";
                }
            }
            
            return route;
        } catch (JSONException e) {
            Log.e(TAG, "Error converting JSON to route", e);
            return null;
        }
    }

    /**
     * 导出路线为 JSON 文件
     */
    public static boolean exportRoute(Context context, Route route) {
        if (route == null) {
            Log.e(TAG, "Route is null");
            return false;
        }

        try {
            String jsonString = routeToJson(route);
            if (jsonString == null) {
                return false;
            }

            String fileName = sanitizeFileName(route.routeName) + "_" + route.timestamp + FILE_EXTENSION;
            File routeFile = new File(getRouteDir(context), fileName);

            FileOutputStream fos = new FileOutputStream(routeFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.write(jsonString);
            writer.close();

            Log.d(TAG, "Route exported successfully: " + routeFile.getAbsolutePath());
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Error exporting route", e);
            return false;
        }
    }

    /**
     * 从 JSON 文件导入路线
     */
    public static Route importRoute(File file) {
        if (file == null || !file.exists()) {
            Log.e(TAG, "File not found");
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            return jsonToRoute(sb.toString());

        } catch (IOException e) {
            Log.e(TAG, "Error importing route", e);
            return null;
        }
    }

    /**
     * 获取所有已保存的路线文件
     */
    public static List<File> getSavedRouteFiles(Context context) {
        List<File> routeFiles = new ArrayList<>();
        File routeDir = getRouteDir(context);
        File[] files = routeDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(FILE_EXTENSION)) {
                    routeFiles.add(file);
                }
            }
        }
        return routeFiles;
    }

    /**
     * 删除路线文件
     */
    public static boolean deleteRouteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }
    
    /**
     * 清理文件名中的非法字符
     */
    private static String sanitizeFileName(String name) {
        if (name == null) {
            return "route";
        }
        // 替换掉文件名中不允许的字符
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
