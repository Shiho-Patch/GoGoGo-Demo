package com.zcshou.gogogo.utils;

import android.content.Context;
import android.util.Log;

import com.zcshou.gogogo.data.model.LocationInfo;
import com.zcshou.gogogo.data.model.RouteInfo;

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
 * 支持 JSON 格式的路线文件
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
     * 导出路线为 JSON 文件
     */
    public static boolean exportRoute(Context context, RouteInfo routeInfo) {
        if (routeInfo == null || routeInfo.getPointCount() == 0) {
            Log.e(TAG, "Route is empty");
            return false;
        }

        try {
            JSONObject routeJson = new JSONObject();
            routeJson.put("name", routeInfo.getName());
            routeJson.put("description", routeInfo.getDescription() != null ? routeInfo.getDescription() : "");
            routeJson.put("speed", routeInfo.getSpeed());
            routeJson.put("loopCount", routeInfo.getLoopCount());
            routeJson.put("speedFluctuation", routeInfo.isSpeedFluctuation());
            routeJson.put("createTime", routeInfo.getCreateTime());

            JSONArray pointsArray = new JSONArray();
            for (LocationInfo point : routeInfo.getPoints()) {
                JSONObject pointJson = new JSONObject();
                pointJson.put("latitude", point.getLatitude());
                pointJson.put("longitude", point.getLongitude());
                pointJson.put("altitude", point.getAltitude());
                pointJson.put("name", point.getName() != null ? point.getName() : "");
                pointJson.put("address", point.getAddress() != null ? point.getAddress() : "");
                pointsArray.put(pointJson);
            }
            routeJson.put("points", pointsArray);

            String fileName = routeInfo.getName() + "_" + System.currentTimeMillis() + FILE_EXTENSION;
            File routeFile = new File(getRouteDir(context), fileName);

            FileOutputStream fos = new FileOutputStream(routeFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.write(routeJson.toString(2));
            writer.close();

            Log.d(TAG, "Route exported successfully: " + routeFile.getAbsolutePath());
            return true;

        } catch (JSONException | IOException e) {
            Log.e(TAG, "Error exporting route", e);
            return false;
        }
    }

    /**
     * 从 JSON 文件导入路线
     */
    public static RouteInfo importRoute(File file) {
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

            JSONObject routeJson = new JSONObject(sb.toString());
            RouteInfo routeInfo = new RouteInfo();
            routeInfo.setName(routeJson.optString("name", "Unknown Route"));
            routeInfo.setDescription(routeJson.optString("description", ""));
            routeInfo.setSpeed(routeJson.optDouble("speed", 1.0));
            routeInfo.setLoopCount(routeJson.optInt("loopCount", 1));
            routeInfo.setSpeedFluctuation(routeJson.optBoolean("speedFluctuation", false));
            routeInfo.setCreateTime(routeJson.optLong("createTime", System.currentTimeMillis()));

            JSONArray pointsArray = routeJson.optJSONArray("points");
            if (pointsArray != null) {
                List<LocationInfo> points = new ArrayList<>();
                for (int i = 0; i < pointsArray.length(); i++) {
                    JSONObject pointJson = pointsArray.getJSONObject(i);
                    LocationInfo point = new LocationInfo();
                    point.setLatitude(pointJson.getDouble("latitude"));
                    point.setLongitude(pointJson.getDouble("longitude"));
                    point.setAltitude(pointJson.optDouble("altitude", 0.0));
                    point.setName(pointJson.optString("name", ""));
                    point.setAddress(pointJson.optString("address", ""));
                    points.add(point);
                }
                routeInfo.setPoints(points);
            }

            Log.d(TAG, "Route imported successfully: " + routeInfo.getName());
            return routeInfo;

        } catch (JSONException | IOException e) {
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
}
