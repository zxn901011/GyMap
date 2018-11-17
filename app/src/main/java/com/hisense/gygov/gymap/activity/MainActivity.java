package com.hisense.gygov.gymap.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiFilter;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.hisense.gygov.gymap.R;
import com.hisense.gygov.gymap.maputil.MyPoiOverlay;
import com.hisense.gygov.gymap.maputil.listener.MyLocationListenner;
import com.hisense.gygov.gymap.util.EmptyUtils;
import com.hisense.gygov.gymap.util.NaviUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener,
        BaiduMap.OnMapClickListener,OnGetPoiSearchResultListener {
    public static final String START_ACTIVITY_SAFETY_KEY = "START_ACTIVITY_SAFETY_KEY";
    PoiInfo poiInfo;
    MyPoiOverlay poiOverlay;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    public MyLocationListenner myListener;
    private Button bt;
    private Button button;
    private Button buttons;
    private Button button_road;
    private Button buttons_hot;
    private LatLng latLng;
    private PoiSearch mPoiSearch;
    private AutoCompleteTextView etContent;
    private ArrayAdapter adapter;
    private SuggestionSearch mSuggestionSearch;
    TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            mSuggestionSearch = SuggestionSearch.newInstance();
            mSuggestionSearch.setOnGetSuggestionResultListener(listener);
            mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
            .keyword(s.toString())
            .city("青岛")
            );
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };
    OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
        public void onGetSuggestionResult(SuggestionResult res) {
            if (res == null || res.getAllSuggestions() == null) {
                return;
            }
            List<SuggestionResult.SuggestionInfo> suggestionInfoList = new ArrayList<>();
            for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                if (info!= null) {
                    suggestionInfoList.add(info);
                }
            }
//            HashMap<String,String> map=new HashMap<>();
//            for (SuggestionResult.SuggestionInfo info: suggestionInfoList){
//                map.put(info.key,info.uid);
//            }
            List<String> suggest = new ArrayList<>();
            for (SuggestionResult.SuggestionInfo info: suggestionInfoList) {
                if (info.key!= null) {
                    suggest.add(info.key);
                }
            }
            adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line,
                    suggest);
            etContent.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    };
    private BDLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initMap();
    }
    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14.5f));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        myListener= new MyLocationListenner(MainActivity.this,mBaiduMap);
        mLocationClient.registerLocationListener(myListener);
        //配置定位SDK参数
        initLocation();
        //开启定位
        mLocationClient.start();
        //图片点击事件，回到定位点
        mLocationClient.requestLocation();
    }

    //配置定位SDK参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true); // 打开gps
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    private void initView() {
        etContent= findViewById(R.id.et_text);
        etContent.addTextChangedListener(textWatcher);
        Button btn_seek=findViewById(R.id.bt_seek);
        btn_seek.setOnClickListener(this);
        mMapView = findViewById(R.id.bmapView);
        bt = findViewById(R.id.bt);
        bt.setOnClickListener(this);
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
        buttons = findViewById(R.id.buttons);
        buttons.setOnClickListener(this);
        button_road=findViewById(R.id.button_road);
        button_road.setOnClickListener(this);
        buttons_hot=findViewById(R.id.button_hot);
        buttons_hot.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        BaiduMapNavigation.finish(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mLocationClient.unRegisterLocationListener(myListener);
        mMapView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_seek:
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                String content = etContent.getText().toString();
                location=myListener.getLocation();
                mPoiSearch = PoiSearch.newInstance();
                mPoiSearch.setOnGetPoiSearchResultListener(this);
                mPoiSearch.searchInCity(new PoiCitySearchOption()
                        .keyword(content)
                        .city("青岛")
                        .pageNum(0)
                        .pageCapacity(50)
                        .scope(1)
                );
                break;
            case R.id.bt:
                //把定位点再次显现出来
                latLng=myListener.getLatLng();
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(mapStatusUpdate);
                break;
            case R.id.button:
                //卫星地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                mBaiduMap.setBaiduHeatMapEnabled(false);
                mBaiduMap.setTrafficEnabled(false);
                mBaiduMap.setIndoorEnable(false);
                break;
            case R.id.buttons:
                //普通地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mBaiduMap.setBaiduHeatMapEnabled(false);
                mBaiduMap.setTrafficEnabled(false);
                mBaiduMap.setIndoorEnable(false);
                break;
            case R.id.button_road:
                mBaiduMap.setTrafficEnabled(true);
                mBaiduMap.setBaiduHeatMapEnabled(false);
                mBaiduMap.setIndoorEnable(false);
                break;
            case R.id.button_hot:
                mBaiduMap.setBaiduHeatMapEnabled(true);
                mBaiduMap.setTrafficEnabled(false);
                mBaiduMap.setIndoorEnable(false);
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //返回键处理
        if (keyCode == KeyEvent.KEYCODE_BACK)
            finish();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        return;
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        //获取POI检索结果
        //待完善
        if (poiResult == null
                || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
            Toast.makeText(MainActivity.this, "未找到结果",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
            mBaiduMap.clear();
            if (EmptyUtils.isNotEmpty(poiResult)) {
                poiOverlay = new MyPoiOverlay(mBaiduMap, mPoiSearch);
                mBaiduMap.setOnMarkerClickListener(poiOverlay);
                poiOverlay.setData(poiResult);// 设置POI数据
                poiOverlay.addToMap();//将所有的overlay添加到地图上
                poiOverlay.zoomToSpan(mBaiduMap.getLocationData());
                LatLng currentLatLng= poiResult.getAllPoi().get(0).getLocation();
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(currentLatLng).zoom(16.5f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }else{
                Toast.makeText(MainActivity.this, "未查询到任何结果", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onGetPoiDetailResult(final PoiDetailResult poiDetailResult) {
        if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, "抱歉，未找到结果",
                    Toast.LENGTH_SHORT).show();
        } else {
            // 正常返回结果的时候，此处可以获得很多相关信息
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(poiDetailResult.getName())//标题
                    .setMessage(poiDetailResult.getAddress())//内容
                    .setIcon(R.mipmap.icon_gcoding)//图标
                    .setPositiveButton("导航去这里", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            NaviUtil.startCarNavi(MainActivity.this,location,poiOverlay.getPoiInfo());
                            poiInfo=poiOverlay.getPoiInfo();
                            Intent intent=new Intent(MainActivity.this,RouteActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("current_LatLng",location);
                            bundle.putParcelable("target_LatLng",poiInfo);
                            intent.putExtra(START_ACTIVITY_SAFETY_KEY,bundle);
                            startActivity(intent);
                        }
                    }).create();
            alertDialog.show();

        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {}

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {}

}
