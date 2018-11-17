package com.hisense.gygov.gymap.maputil.listener;

import android.content.Context;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

public class MyLocationListenner implements BDLocationListener {
    private BaiduMap mBaiduMap;
    private Context mContext;
    private boolean isFirstLoc = true; // 是否首次定位
    private LatLng latLng;
    private BDLocation mLocation;

    public MyLocationListenner(Context context, BaiduMap baiduMap){
        this.mBaiduMap=baiduMap;
        this.mContext=context;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        this.mLocation=location;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        setLatLng(latLng);
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(15.5f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                // GPS定位结果
                Toast.makeText(mContext, location.getAddrStr(), Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                // 网络定位结果
                Toast.makeText(mContext, location.getAddrStr(), Toast.LENGTH_SHORT).show();

            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                // 离线定位结果
                Toast.makeText(mContext, location.getAddrStr(), Toast.LENGTH_SHORT).show();

            } else if (location.getLocType() == BDLocation.TypeServerError) {
                Toast.makeText(mContext, "服务器错误，请检查", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                Toast.makeText(mContext, "网络错误，请检查", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                Toast.makeText(mContext, "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    public BDLocation getLocation() {
        return mLocation;
    }
}
