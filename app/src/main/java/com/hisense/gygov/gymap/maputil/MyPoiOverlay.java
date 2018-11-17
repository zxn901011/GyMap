package com.hisense.gygov.gymap.maputil;


import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.hisense.gygov.gymap.maputil.overlayutil.PoiOverlay;

public class MyPoiOverlay extends PoiOverlay {
    private PoiSearch mPoiSearch;
    private PoiInfo poiInfo;

    /**
     * 构造函数
     *
     * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
     */
    public MyPoiOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }
    /**
     * 构造函数
     *
     * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
     */
    public MyPoiOverlay(BaiduMap baiduMap,PoiSearch poiSearch ) {
        super(baiduMap);
        this.mPoiSearch=poiSearch;
    }
    @Override
    public boolean onPoiClick(int index) {
        super.onPoiClick(index);
        poiInfo = getPoiResult().getAllPoi().get(index);
        // 检索poi详细信息
        mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()
                .poiUid(poiInfo.uid));
        return true;
    }

    public PoiInfo getPoiInfo() {
        return poiInfo;
    }
}
