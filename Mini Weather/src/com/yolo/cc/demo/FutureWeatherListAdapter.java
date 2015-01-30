package com.yolo.cc.demo;

import java.util.ArrayList;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 自定义适配器
 * @author yolo.cc
 *
 */
public class FutureWeatherListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<WeatherInfo> arrayList;

	public FutureWeatherListAdapter(Context context,
			ArrayList<WeatherInfo> arrayList) {
		inflater = LayoutInflater.from(context);
		this.arrayList = arrayList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("ViewHolder") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderView holderView = null;
		holderView = new HolderView();
		//导入自定义的item
		convertView = inflater.inflate(R.layout.list_item, parent, false);
		holderView.week = (TextView) convertView.findViewById(R.id.item_week);
		holderView.weather = (ImageView) convertView
				.findViewById(R.id.item_weather);
		holderView.temp = (TextView) convertView.findViewById(R.id.item_temp);

		if (position < arrayList.size()) {
			WeatherInfo weatherInfo = arrayList.get(position);
			holderView.temp.setText(weatherInfo.getTemperature());
			String imageName = "w";
			imageName = imageName + weatherInfo.getFa();
			holderView.weather.setImageResource(ResourceIdUtil
					.getImageResourceId(imageName));
			holderView.week.setText(weatherInfo.getWeek());
		}
		return convertView;
	}

	private class HolderView {
		TextView week;
		ImageView weather;
		TextView temp;
	}
}
