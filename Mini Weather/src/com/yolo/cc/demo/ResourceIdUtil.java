package com.yolo.cc.demo;

import java.lang.reflect.Field;




public class ResourceIdUtil {

	/**
	 * 获取图片的id
	 * 
	 * @param imageName
	 *            图片的名字
	 * @return
	 */
	public static int getImageResourceId(String imageName) {
		R.drawable drawable = new R.drawable();
		int resId = 0;
		try {
			Field field = R.drawable.class.getField(imageName);
			resId = field.getInt(drawable);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resId;
	}

}
