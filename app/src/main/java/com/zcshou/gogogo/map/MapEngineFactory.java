package com.zcshou.gogogo.map;

import android.content.Context;

public class MapEngineFactory {

    public static MapEngine createEngine(Context context, MapEngine.MapProvider provider) {
        switch (provider) {
            case BAIDU:
                return new BaiduMapEngine();
            case AMAP:
                return new AMapEngine();
            default:
                return new BaiduMapEngine();
        }
    }

    public static MapEngine createDefaultEngine(Context context) {
        return createEngine(context, MapEngine.MapProvider.BAIDU);
    }
}
