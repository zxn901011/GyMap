package com.hisense.gygov.gymap.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.utils.OpenClientUtil;

public class NaviUtil {
    /**
     * 启动百度地图导航(Native)
     */
    public static void startCarNavi(Context context,BDLocation bdLocation, PoiInfo poiInfo) {
        LatLng startLatLng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
        LatLng targetLatLng=poiInfo.getLocation();
        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(startLatLng).endPoint(targetLatLng)
                .startName(bdLocation.getAddrStr()).endName(poiInfo.getName());
        try {
            BaiduMapNavigation.openBaiduMapNavi(para, context);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            showDialog(context);
        }
    }

    /**
     * 启动百度地图导航(Web)
     */
    public static void startWebCarNavi(Context context,BDLocation bdLocation, PoiInfo poiInfo) {
        LatLng startLatLng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
        LatLng targetLatLng=poiInfo.getLocation();
        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(startLatLng).endPoint(targetLatLng)
                .startName(bdLocation.getAddrStr()).endName(poiInfo.getName());
        try {
            BaiduMapNavigation.openWebBaiduMapNavi(para, context);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            showDialog(context);
        }
    }

    /**
     * 启动百度地图步行导航(Native)
     */
    public static void startWalkingNavi(Context context,BDLocation bdLocation, PoiInfo poiInfo) {
        LatLng startLatLng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
        LatLng targetLatLng=poiInfo.getLocation();
        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(startLatLng).endPoint(targetLatLng)
                .startName(bdLocation.getAddrStr()).endName(poiInfo.getName());
        try {
            BaiduMapNavigation.openBaiduMapWalkNavi(para, context);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            showDialog(context);
        }

    }

    /**
     * 启动百度地图步行AR导航(Native)
     */
    public static void startWalkingNaviAR(Context context,BDLocation bdLocation, PoiInfo poiInfo) {

        LatLng startLatLng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
        LatLng targetLatLng=poiInfo.getLocation();
        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(startLatLng).endPoint(targetLatLng)
                .startName(bdLocation.getAddrStr()).endName(poiInfo.getName());
        try {
            BaiduMapNavigation.openBaiduMapWalkNaviAR(para, context);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            showDialog(context);
        }

    }

    private static void showDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(context);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    /**
     * 启动百度地图骑行导航(Native)
     */
    public static void startBikingNavi(Context context,BDLocation bdLocation, PoiInfo poiInfo) {
        LatLng startLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        LatLng targetLatLng = poiInfo.getLocation();
        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(startLatLng).endPoint(targetLatLng)
                .startName(bdLocation.getAddrStr()).endName(poiInfo.getName());
        try {
            BaiduMapNavigation.openBaiduMapBikeNavi(para, context);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            showDialog(context);
        }
    }
}
