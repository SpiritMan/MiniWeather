package com.yolo.cc.demo;

import com.thinkland.sdk.android.SDKInitializer;
import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//初始化聚合数据SDK
		SDKInitializer.initialize(getApplicationContext());
	}
}
