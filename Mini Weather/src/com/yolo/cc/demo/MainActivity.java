package com.yolo.cc.demo;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

/**
 * @author yolo.cc
 * 
 */
public class MainActivity extends Activity implements OnClickListener {

	TextView tv;
	private ListView futureWeatherList;
	//自定义的适配器
	private FutureWeatherListAdapter adapter;
	private ArrayList<WeatherInfo> weatherInfos;
	private String reasonString;
	private TextView cityname, todayTemp;
	// 用来显示天气图片
	private ImageView weatherImageView;
	private String day = "day_";
	// 默认天气预报城市
	private String city = "北京";
	private int time = 0;
	private ImageView searchImageView, refreshImageView;
	// 刷新信息效果
	private ProgressBar progressBar;
	private EditText editCityName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//获得本地存储的城市名字，没有默认为北京
		SharedPreferences sharedPreferences = getSharedPreferences("city",
				MODE_PRIVATE);
		city = sharedPreferences.getString("cityname", "北京");
		initView();

	}

	/**
	 * 初始化数据
	 */
	public void initView() {
		editCityName = (EditText) findViewById(R.id.editCityName);
		refreshImageView = (ImageView) findViewById(R.id.refresh);
		searchImageView = (ImageView) findViewById(R.id.search);
		todayTemp = (TextView) findViewById(R.id.temperature);
		weatherImageView = (ImageView) findViewById(R.id.weather);
		cityname = (TextView) findViewById(R.id.cityname);
		weatherInfos = new ArrayList<WeatherInfo>();
		futureWeatherList = (ListView) findViewById(R.id.future_weather);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		// 刷新天气信息
		refreshImageView.setOnClickListener(this);
		// 查找某个城市的天气信息
		searchImageView.setOnClickListener(this);
		getWeatherData(city);
	}

	/**
	 * 从网络上获取天气预报信息
	 */
	public void getWeatherData(String cityname) {
		progressBar.setVisibility(View.VISIBLE);
		refreshImageView.setVisibility(View.INVISIBLE);
		Parameters params = new Parameters();
		params.add("cityname", cityname);
		params.add("dtype", "json");
		params.add("key", "f7fa92fa1d6557b3394b52893e025778");
		// f7fa92fa1d6557b3394b52893e025778
		/**
		 * 请求的方法 参数: 第一个参数 接口id 第二个参数 接口请求的url 第三个参数 接口请求的方式 第四个参数
		 * 接口请求的参数,键值对com.thinkland.sdk.android.Parameters类型; 第五个参数
		 * 请求的回调方法,com.thinkland.sdk.android.DataCallBack;
		 * 
		 */

		JuheData.executeWithAPI(39, "http://v.juhe.cn/weather/index",
				JuheData.GET, params, new DataCallBack() {

					/**
					 * @param err
					 *            错误码,0为成功
					 * @param reason
					 *            原因
					 * @param result
					 *            数据
					 */
					public void resultLoaded(int err, String reason,
							String result) {
						// TODO Auto-generated method stub
						if (true) {
							reasonString = reason;
							// 调用异步方法
							ParseWeatherDataByAsyncTask parseWeatherDataByAsyncTask = new ParseWeatherDataByAsyncTask();
							parseWeatherDataByAsyncTask.execute(result);
							System.out.println(result);
						} else {
							refreshImageView.setVisibility(View.VISIBLE);
							progressBar.setVisibility(View.INVISIBLE);
							// 在屏幕上显示一个提示
							Toast.makeText(MainActivity.this, reason,
									Toast.LENGTH_SHORT).show();
						}
					}
				});

	}

	/**
	 * 异步解析得到的天气预报信息
	 * 
	 * @author yolo.cc
	 * 
	 */
	class ParseWeatherDataByAsyncTask extends
			AsyncTask<String, Void, ArrayList<WeatherInfo>> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected ArrayList<WeatherInfo> doInBackground(String... params) {
			// 获取当前时间例如：20150119
			Date statusDate = new Date(System.currentTimeMillis());
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
			String imagename = simpleDateFormat.format(statusDate);
			time = Integer.parseInt(imagename);

			String data = params[0];
			JSONObject dataJsonObject = null;
			try {
				// 将String类型的数据转成json类型
				dataJsonObject = new JSONObject(data);
				// 判断是否获取数据成功
				if (dataJsonObject.getInt("error_code") == 0) {
					// 将你查找的城市放入本地，作为下次的默认城市
					SharedPreferences preferences = getSharedPreferences(
							"city", MODE_PRIVATE);
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("cityname", city);
					editor.commit();
					JSONObject resultJsonObject = dataJsonObject
							.getJSONObject("result");
					WeatherInfo weatherInfo = new WeatherInfo();
					weatherInfo.setTemp(resultJsonObject.getJSONObject("sk")
							.getString("temp") + "℃");
					JSONObject today = resultJsonObject.getJSONObject("today");
					weatherInfo.setCity(today.getString("city"));
					weatherInfo.setFa(today.getJSONObject("weather_id")
							.getString("fa"));
					if (weatherInfos.size() != 0) {
						weatherInfos.set(0, weatherInfo);
					} else {
						weatherInfos.add(weatherInfo);
					}
					JSONObject futureJsonObject = resultJsonObject
							.getJSONObject("future");
					for (int i = 0; i < 6; i++) {
						WeatherInfo weather = new WeatherInfo();
						time = time + 1;
						System.out.println("day:" + (day + (time + "")));
						JSONObject jsonObject = futureJsonObject
								.getJSONObject((day + (time + "")));
						weather.setWeek(jsonObject.getString("week"));
						weather.setTemperature(jsonObject
								.getString("temperature"));
						weather.setFa(jsonObject.getJSONObject("weather_id")
								.getString("fa"));
						if (weatherInfos.size() > (i + 1)) {
							weatherInfos.set((i + 1), weather);
						} else {
							weatherInfos.add(weather);
						}
					}
					return weatherInfos;
					// System.out.println("resultjson:" + resultJsonObject);
				} else {
					reasonString = dataJsonObject.getString("reason");
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		@SuppressLint("ShowToast")
		@Override
		protected void onPostExecute(ArrayList<WeatherInfo> result) {
			refreshImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
			// 将解析好的数据显示在屏幕上
			if (result != null) {
				System.out.println("result size:" + result.size());
				WeatherInfo weatherInfo = result.get(0);
				cityname.setText(weatherInfo.getCity());
				todayTemp.setText(weatherInfo.getTemp());
				String imageName = "w";
				imageName = imageName + weatherInfo.getFa();
				weatherImageView.setImageResource(ResourceIdUtil
						.getImageResourceId(imageName));

				result.remove(0);
				adapter = new FutureWeatherListAdapter(MainActivity.this,
						result);
				futureWeatherList.setAdapter(adapter);

			} else {
				refreshImageView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.INVISIBLE);
				Toast.makeText(MainActivity.this, reasonString,
						Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh:
			getWeatherData(city);
			Toast.makeText(MainActivity.this, "刷新", Toast.LENGTH_SHORT).show();
			break;
		case R.id.search:
			if (editCityName.getVisibility() == View.INVISIBLE) {
				editCityName.setVisibility(View.VISIBLE);
			} else {
				View view = getWindow().peekDecorView();
				// 使软键盘自动关闭
				InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
				city = editCityName.getText().toString().trim();
				if (city != null) {
					getWeatherData(city);
				} else {
					Toast.makeText(this, "城市名不能为空!", Toast.LENGTH_SHORT).show();
				}
				editCityName.setVisibility(View.INVISIBLE);
			}
			break;
		default:
			break;
		}
	}
}
