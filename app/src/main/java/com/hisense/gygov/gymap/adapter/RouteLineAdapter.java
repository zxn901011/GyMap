/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.hisense.gygov.gymap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.hisense.gygov.gymap.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteLineAdapter extends BaseAdapter {

    private List<? extends  RouteLine> routeLines;
    private LayoutInflater layoutInflater;
    private Type mtype;

    public RouteLineAdapter(Context context, List<?extends RouteLine> routeLines, Type type) {
        this.routeLines = routeLines;
        layoutInflater = LayoutInflater.from(context);
        mtype = type;
    }

    @Override
    public int getCount() {
        return routeLines.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NodeViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_transit_item, null);
            holder = new NodeViewHolder();
            holder.name = convertView.findViewById(R.id.transitName);
            holder.lightNum = convertView.findViewById(R.id.lightNum);
            holder.dis = convertView.findViewById(R.id.dis);
            convertView.setTag(holder);
        } else {
            holder = (NodeViewHolder) convertView.getTag();
        }

        switch (mtype) {
            case TRANSIT_ROUTE:
                RouteLine routeLine=routeLines.get(position);
                TransitRouteLine transitRouteLine = (TransitRouteLine) routeLine;
                List<TransitRouteLine.TransitStep> steps = transitRouteLine.getAllStep();
                StringBuilder sb = new StringBuilder("");
                for (TransitRouteLine.TransitStep step : steps) {
                    if (step.getStepType() == TransitRouteLine.TransitStep.TransitRouteStepType.SUBWAY) {
                        sb.append(step.getInstructions());
                    } else if (step.getStepType() == TransitRouteLine.TransitStep.TransitRouteStepType.BUSLINE) {
                        String bus = step.getInstructions();
                        Pattern pattern = Pattern.compile("乘坐.*?路");
                        Matcher m = pattern.matcher(bus);
                        if (m.find()) {
                            sb.append(m.group().replace("乘坐", "") + " ");
                        }
                    }
                }
                holder.name.setText(sb.toString());
                int time = routeLines.get(position).getDuration();
                if ( time / 3600 == 0 ) {
                    holder.lightNum.setText( "大约需要：" + time / 60 + "分钟" );
                } else {
                    holder.lightNum.setText( "大约需要：" + time / 3600 + "小时" + (time % 3600) / 60 + "分钟" );
                }
                holder.dis.setText("距离大约是：" + routeLines.get(position).getDistance() + "米");
                break;
            case WALKING_ROUTE:
                holder.name.setText("路线" + (position + 1));
                time = routeLines.get(position).getDuration();
                if ( time / 3600 == 0 ) {
                    holder.lightNum.setText( "大约需要：" + time / 60 + "分钟" );
                } else {
                    holder.lightNum.setText( "大约需要：" + time / 3600 + "小时" + (time % 3600) / 60 + "分钟" );
                }
                holder.dis.setText("距离大约是：" + routeLines.get(position).getDistance() + "米");
                break;
            case BIKING_ROUTE:
                holder.name.setText("路线" + (position + 1));
                time = routeLines.get(position).getDuration();
                if ( time / 3600 == 0 ) {
                    holder.lightNum.setText( "大约需要：" + time / 60 + "分钟" );
                } else {
                    holder.lightNum.setText( "大约需要：" + time / 3600 + "小时" + (time % 3600) / 60 + "分钟" );
                }
                holder.dis.setText("距离大约是：" + routeLines.get(position).getDistance() + "米");
                break;

            case DRIVING_ROUTE:
                DrivingRouteLine drivingRouteLine = (DrivingRouteLine) routeLines.get(position);
                holder.name.setText( "线路" + (position + 1));
                holder.lightNum.setText( "红绿灯数：" + drivingRouteLine.getLightNum());
                holder.dis.setText("拥堵距离为：" + drivingRouteLine.getCongestionDistance() + "米");
                break;
            default:
                break;

        }

        return convertView;
    }

    private class NodeViewHolder {

        private TextView name;
        private TextView lightNum;
        private TextView dis;
    }

    public enum Type {
        MASS_TRANSIT_ROUTE, // 综合交通
        TRANSIT_ROUTE, // 公交
        DRIVING_ROUTE, // 驾车
        WALKING_ROUTE, // 步行
        BIKING_ROUTE // 骑行

    }
}
